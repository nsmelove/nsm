package com.nsm.mvc.filter;

import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.service.AuthService;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nieshuming on 2018/5/31
 */
public class AuthFilter implements Filter{
    public static final String sessionId = "sid";
    public static final String userId = "uid";
    private AuthService authService = new AuthService();

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

        if(!StringUtils.isEmpty(sid)){
            request.setAttribute(sessionId,sid);
            long uid = getUid(sid);
            request.setAttribute(userId, uid);
        }else {
            sid = IdUtils.nextString32();
            httpResponse.setHeader(sessionId,sid);
            httpResponse.addCookie(new Cookie(sessionId, sid));
        }
        chain.doFilter(request, response);
    }


    private long getUid(String sid) {
        Session session = authService.getSession(sid);
        if(session != null) {
            return session.getUserId();
        }else {
            return 0;
        }
    }

    @Override
    public void destroy() {

    }
}
