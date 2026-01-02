package com.lendwise.middleware.utils.common;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String urn =request.getHeader("urn");
            if(urn == null) urn = TimestampSequenceGenerator.generateUniqueIdInString();
            MDC.put("urn", urn);
            String remoteAddr = request.getRemoteAddr();
            if (remoteAddr != null && !remoteAddr.isEmpty()) {
                MDC.put("hostServerIP", remoteAddr);
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }


}
