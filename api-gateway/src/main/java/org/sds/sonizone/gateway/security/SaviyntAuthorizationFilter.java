package org.sds.sonizone.gateway.security;


public class SaviyntAuthorizationFilter{} /*extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Authenticated user: " + auth.getName());
        System.out.println("URI: " + request.getRequestURI());
        //System.out.println("Assigned Role: " + role);

        if (auth != null && auth.isAuthenticated()) {
            String user = auth.getName();
            String role = fetchUserRoleFromSaviynt(user); // API call to Saviynt

            if (role == null || !hasAccess(request.getRequestURI(), role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String fetchUserRoleFromSaviynt(String username) {
        // TODO: REST API call to Saviynt to get role
        return "ROLE_ORDER_MANAGER";
    }

    private boolean hasAccess(String path, String role) {
        if (path.startsWith("/orders") && role.equals("ROLE_ORDER_MANAGER")) return true;
        if (path.startsWith("/payments") && role.equals("ROLE_PAYMENT_MANAGER")) return true;
        return false;
    }
}*/