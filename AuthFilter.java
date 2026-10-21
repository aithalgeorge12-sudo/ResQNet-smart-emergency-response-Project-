package com.resqnet.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter that protects authenticated user areas from unauthorized access.
 */
@WebFilter(urlPatterns = {"/dashboard", "/profile", "/emergency-contacts", "/my-reports"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        if (isLoggedIn) {
            chain.doFilter(req, res);
        } else {
            // Save requested URL for post-login redirect
            String requestURI = request.getRequestURI();
            if (request.getQueryString() != null) {
                requestURI += "?" + request.getQueryString();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("redirectAfterLogin", requestURI);
            response.sendRedirect(request.getContextPath() + "/login?error=auth-required");
        }
    }
}
