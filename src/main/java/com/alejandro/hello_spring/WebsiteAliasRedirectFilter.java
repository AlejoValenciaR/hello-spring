package com.alejandro.hello_spring;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class WebsiteAliasRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!WebsiteAliases.isAliasPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        StringBuilder location = new StringBuilder(request.getContextPath()).append(WebsiteAliases.WEBSITE_PATH);
        if (StringUtils.hasText(request.getQueryString())) {
            location.append('?').append(request.getQueryString());
        }

        response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, location.toString());
    }
}
