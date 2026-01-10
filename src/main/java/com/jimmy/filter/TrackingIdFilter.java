package com.jimmy.filter;

import com.jimmy.constant.HttpHeaders;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

@Component
public class TrackingIdFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String trackingId = request.getHeader(HttpHeaders.TRACKING_ID.getName());
        if (StringUtils.isBlank(trackingId)) {
            trackingId = UUID.randomUUID().toString();
        }
        response.setHeader(HttpHeaders.TRACKING_ID.getName(), trackingId);
        MDC.put(HttpHeaders.TRACKING_ID.getName(), trackingId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(HttpHeaders.TRACKING_ID.getName());
        }
    }
}
