package com.nsm.boot.cofig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nieshuming on 2018/9/20
 */
@Configuration
public class FilterConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    Filter logFilter(){
        return new OncePerRequestFilter() {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String uri = request.getRequestURI();
                //资源文件请求不过滤
                if(!uri.matches("\\S+\\.\\S+")) {
                    String method = request.getMethod();
                    long start = System.nanoTime();
                    filterChain.doFilter(request, response);
                    long end = System.nanoTime();
                    long cost = (end - start)/1_000_000;
                    logger.info("{} {} cost {} ms", method, uri, cost);
                }
            }
        };
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    Filter authFilter(){
        return new OncePerRequestFilter() {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                Object user = request.getSession().getAttribute("user");
                if(user == null) {
                    //logger.error("session {} not authed", request.getSession().getId());
                    //return;
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}
