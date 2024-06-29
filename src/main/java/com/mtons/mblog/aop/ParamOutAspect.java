package com.mtons.mblog.aop;

import com.mtons.mblog.utils.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Aspect
@Slf4j
public class ParamOutAspect {
    private final String executeExpr = "execution(* com.mtons.mblog..*Controller.*(..))";
    @Around(executeExpr)
    public Object processLog(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取方法名称
        String methodName = method.getName();
        //获取参数名称
        LocalVariableTableParameterNameDiscoverer paramNames = new LocalVariableTableParameterNameDiscoverer();
        String[] params = paramNames.getParameterNames(method);
        //获取参数
        Object[] args = joinPoint.getArgs();
        log.info(methodName + "请求信息为：" + params);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        boolean flag = false;
        if (ObjectUtils.isNotEmpty(cookies)) {

            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if ("__uuid".equals(name)) {
                    flag = true;
                }
            }
        }
        Object resObj = null;
        try {
            //执行原方法
            resObj = joinPoint.proceed(args);

            if(!flag) {
                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                String uuid = UUID.randomUUID().toString();
                response.addCookie(new Cookie("__uuid", uuid));
                CacheUtils.put(uuid,"1");
            }
        } catch (Throwable e) {
            log.error(methodName + "方法执行异常!", e);
            throw new RuntimeException(methodName + "方法执行异常!");
        }
      return resObj;
    }

}
