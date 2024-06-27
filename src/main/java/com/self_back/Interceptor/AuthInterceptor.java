package com.self_back.Interceptor;
import com.self_back.Exception.UnAuthException;
import com.self_back.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求详情
//        logRequestDetails(request);

        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            log.info("接收到 OPTIONS 请求，放行");
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null) {
            try {
                int userId = TokenUtil.parseToken(token);
                return true;
            } catch (UnAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token 已过期或无效");
                return false;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未提供 Token");
            return false;
        }
    }

//    private void logRequestDetails(HttpServletRequest request) {
//        // 获取客户端机器的 IP 地址
//        String ipAddress = request.getHeader("X-FORWARDED-FOR");
//        if (ipAddress == null) {
//            ipAddress = request.getRemoteAddr();
//        }
//
//        // 记录请求的方法、URL 和 IP 地址
//        log.info("请求方法: {}, 请求URL: {}, IP 地址: {}", request.getMethod(), request.getRequestURL(), ipAddress);
//    }
}
