package com.ship.weeklycommits.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthHeaderFilter implements Filter {

    public static final String USER_ID_ATTR = "auth.userId";
    public static final String ORG_ID_ATTR = "auth.orgId";
    public static final String USER_ROLE_ATTR = "auth.userRole";

    @Value("${app.auth.default-user-id:}")
    private String defaultUserId;

    @Value("${app.auth.default-org-id:}")
    private String defaultOrgId;

    @Value("${app.auth.default-user-role:user}")
    private String defaultUserRole;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String userId = httpRequest.getHeader("X-User-Id");
        String orgId = httpRequest.getHeader("X-Org-Id");
        String userRole = httpRequest.getHeader("X-User-Role");

        // Fall back to configured defaults (for local dev without Ship proxy)
        if (userId == null || userId.isBlank()) userId = defaultUserId;
        if (orgId == null || orgId.isBlank()) orgId = defaultOrgId;

        if (userId == null || userId.isBlank() || orgId == null || orgId.isBlank()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Missing required auth headers");
            return;
        }

        httpRequest.setAttribute(USER_ID_ATTR, userId);
        httpRequest.setAttribute(ORG_ID_ATTR, orgId);
        httpRequest.setAttribute(USER_ROLE_ATTR, userRole != null && !userRole.isBlank() ? userRole : defaultUserRole);

        chain.doFilter(request, response);
    }
}
