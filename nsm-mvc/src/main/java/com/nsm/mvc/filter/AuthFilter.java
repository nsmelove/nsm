package com.nsm.mvc.filter;

import com.nsm.common.conf.YamlConfigUtils;
import com.nsm.common.utils.IdUtils;
import com.nsm.common.utils.JsonUtils;
import com.nsm.bean.ErrorCode;
import com.nsm.core.service.SessionService;
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
    private PatternsRequestCondition noNeedAuthPatternsRequestCondition = null;
    private SessionService sessionService = new SessionService();

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
                noNeedAuthPatternsRequestCondition = new PatternsRequestCondition(noNeedAuthUrls.toArray(new String[noNeedAuthUrls.size()])) ;
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
        long timeBegin = System.nanoTime();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String queryString = httpRequest.getQueryString();
        if(queryString == null) {
            queryString ="";
        }else {
            queryString = "?" + queryString;
        }
        logger.info("{} {}{}", httpRequest.getMethod(), httpRequest.getServletPath(), queryString);
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
        if(StringUtils.isEmpty(sid)){
            sid = IdUtils.nextString32();
            httpResponse.setHeader(sessionId, sid);
            httpResponse.addCookie(new Cookie(sessionId, sid));
        }else {
            uid = sessionService.getUserId(sid);
        }
        request.setAttribute(sessionId, sid);
        request.setAttribute(userId, uid);
        try{
            if(uid == 0 && noNeedAuthPatternsRequestCondition != null && !noNeedAuthPatternsRequestCondition.isEmpty()){
                if(noNeedAuthPatternsRequestCondition.getMatchingCondition(httpRequest) == null){
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
        }finally {
            long cost = (System.nanoTime() - timeBegin) / 1_000_000;
            int status = httpResponse.getStatus();
            logger.info("{} {}{} {} {}ms", httpRequest.getMethod(), httpRequest.getServletPath(), queryString , status, cost);
            if(logger.isDebugEnabled()) {
                logger.debug("auth info : sid={}, uid={}", sid, uid);
                logger.debug("req params : {}", httpRequest.getParameterMap());
            }
        }
    }

    @Override
    public void destroy() {

    }
}
