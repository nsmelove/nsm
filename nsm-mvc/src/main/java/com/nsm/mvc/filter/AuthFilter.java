package com.nsm.mvc.filter;

import com.nsm.common.conf.YamlConfigUtils;
import com.nsm.common.utils.IdUtils;
import com.nsm.common.utils.JsonUtils;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nieshuming on 2018/5/31
 */
public class AuthFilter implements Filter{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String sessionId = "sid";
    public static final String userId = "uid";
    public static final String noNeedAuthUrlKey = "noNeedAuthUrl";
    public static final String defaultConfigFile = "authConfig.yaml";
    private PatternsRequestCondition patternsRequestCondition = null;
    private AuthService authService = new AuthService();

    @Override
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
        String configFile = filterConfig.getInitParameter("configFile");
        if(StringUtils.isBlank(configFile)) {
            configFile = defaultConfigFile;
        }
        try {
            Map configs = YamlConfigUtils.loadConfig(configFile, HashMap.class);
            Object noNeedAuthUrl = configs.get(noNeedAuthUrlKey);
            if(noNeedAuthUrl instanceof List){
                List<String> noNeedAuthUrls = (List<String>)noNeedAuthUrl;
                patternsRequestCondition = new PatternsRequestCondition(noNeedAuthUrls.toArray(new String[noNeedAuthUrls.size()])) ;
            }else {
                logger.warn("config item '{}' error, must be list !", noNeedAuthUrlKey);
            }
        }catch (NullPointerException e){
            logger.warn("auth config file '{}' not exist, all request will be passed !", configFile);
        }
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

        long uid = 0;
        if(!StringUtils.isEmpty(sid)){
            request.setAttribute(sessionId,sid);
            uid = getUid(sid);
            request.setAttribute(userId, uid);
        }else {
            sid = IdUtils.nextString32();
            httpResponse.setHeader(sessionId,sid);
            httpResponse.addCookie(new Cookie(sessionId, sid));
        }

        if(uid == 0 && patternsRequestCondition != null && !patternsRequestCondition.isEmpty()){
            if(patternsRequestCondition.getMatchingCondition(httpRequest) == null){
                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                String resMsg = JsonUtils.toJson(ErrorCode.NO_LOGIN);
                if(resMsg != null) {
                    ServletOutputStream out = httpResponse.getOutputStream();
                    out.write(resMsg.getBytes("utf-8"));
                    out.flush();
                }
                return;
            }
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
