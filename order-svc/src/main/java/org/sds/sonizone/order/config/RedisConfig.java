package org.sds.sonizone.order.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.sds.sonizone.order.domain.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/*
That "Cannot serialize" error is coming from Redis because it doesn’t know how to store your Order entity by default.

By default, Spring Data Redis uses JDK serialization for cache values, which will fail if:
Your entity is not Serializable, or
You haven’t configured a proper serializer (like JSON).

Fix Option 1 – Make your entity Serializable (quickest for local testing)

In your Order entity:

import java.io.Serializable;

@Entity
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    }

Fix Option 2 – Use JSON serializer in Redis config (better for production)

Instead of Java serialization, store JSON in Redis.

We have Choosen Option 2, WHY:
1. Works with any object without making it Serializable
2. Stores data as readable JSON in Redis (you can check with redis-cli or RedisInsight)
3. More portable between services

----------------------------------------------------------------------------------------------
Error: Internal Server Error: Could not write JSON:
Java 8 date/time type java.time.LocalDate not supported by default:
add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
(through reference chain: org.sds.sonizone.order.domain.model.Order["orderDate"])

Finding: This error occurs because Jackson (the default JSON processor in Spring Boot) cannot serialize/deserialize
Java 8 Date/Time types like java.time.LocalDate without additional configuration.
You're seeing this when trying to cache objects containing such types in Redis.

Solution:
How to Fix
1. Add the Jackson JSR-310 Module Dependency
Add the following dependency to your Maven pom.xml:

implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'

----------------------------------------------------------------------------
Error: class java.util.LinkedHashMap cannot be cast to class org.sds.sonizone.order.domain.model.Order

Finding: happens when the cached JSON data is deserialized into a generic LinkedHashMap instead of your actual Order class.

Why this happens:
1. When using GenericJackson2JsonRedisSerializer without including type information during serialization,
Redis stores JSON data but loses the Java type metadata.
2.On retrieval, Jackson deserializes JSON to a default generic type like LinkedHashMap instead of your domain
class (Order).
3. When your app tries to cast this LinkedHashMap to Order, a ClassCastException is thrown.

Solution:
1. Enable default typing in your ObjectMapper

You need to configure your custom ObjectMapper with default typing so that type info is saved into JSON
for proper deserialization

e.g. mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);
    return mapper;

-> This registers polymorphic type handling, embedding class info in the JSON.
That way, the deserializer knows to recreate the exact Order class, not just a generic map.


===========================================================================================================
Check and Verify Redis(Docker Desktop) data:
Correct way to check Redis data inside your Docker container:
1. Open an interactive shell session inside your Redis container:

> docker exec -it dapr_redis sh
2. Inside the container shell, launch the Redis CLI:

> redis-cli
You will get a prompt like:

text
127.0.0.1:6379>
3. Run Redis commands like keys * here:

> keys *
4. When done, type:

exit


--> Clear Redis Cache data:
> docker exec -it <dapr_redis> redis-cli flushall     OR   redis-cli flushall


--> GET DATA:
> GET <keys> e.g. GET orders::d734f016-b814-4b76-b994-4ece2969a0af
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper ) {
        // Create serializer for each cache
        Jackson2JsonRedisSerializer<Order> orderSerializer =
                new Jackson2JsonRedisSerializer<>(redisObjectMapper, Order.class);

        // Create custom config per cache
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("orders", RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(orderSerializer)));

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }


    //BELOW CODE WORKS FINE, BUT IT SECURITY ISSUE
    /*@Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Support for Java 8+ date/time types
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // For polymorphism

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Set cache TTL
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }*/
}

//HYBRID SOLUTION
/*
Security Considerations
Above code works, but it has a security issue :

Above Code uses: objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

This is not secure as it allows deserialization of any class, which can be a security vulnerability.

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Secure polymorphic type validator
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("org.sds.sonizone.order.domain.model.*") // Added .* for all subpackages
                .allowIfSubType("java.util.*") // Allow common Java types
                .allowIfSubType("java.time.*") // Allow Java time types
                .build();

        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }
}

 */