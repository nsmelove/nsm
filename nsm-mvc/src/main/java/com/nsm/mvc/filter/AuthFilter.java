package com.nsm.mvc.filter;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by nieshuming on 2018/5/31.
 */
public class AuthFilter implements Filter{
    public static final String sessionId = "sid";
    public static final String userId = "uid";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("AuthFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String sid = httpRequest.getHeader(sessionId);
        if(StringUtils.isEmpty(sid)) {
            Cookie[] cookies = httpRequest.getCookies();
            if(cookies != null) {
                for(Cookie cookie : cookies) {
                    if(cookie.getName().equals(sessionId)) {
                        sid = cookie.getValue();
                        break;
                    }
                }
            }
        }
        //TODO do some req auth reject;

        if(StringUtils.isEmpty(sid)){
            request.setAttribute(sessionId,sid);
            long uid = getUid(sid);
            request.setAttribute(userId, uid);
        }else {
            httpResponse.setHeader(sessionId,generalSessionId());
        }
        chain.doFilter(request, response);
    }

    private String generalSessionId(){
        long nanoseTime = System.nanoTime() + RandomUtils.nextInt(0,1000) * 1_000_000_000;
        return Hashing.md5().hashLong(nanoseTime).toString().substring(8,24);
    }

    private long getUid(String sid) {
        //TODO get the login userId;
        return System.currentTimeMillis();
    }

    @Override
    public void destroy() {

    }
}
