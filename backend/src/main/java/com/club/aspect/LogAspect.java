package com.club.aspect;

import com.club.annotation.Log;
import com.club.entity.SystemLog;
import com.club.service.SystemLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SystemLogService logService;

    @Pointcut("@annotation(com.club.annotation.Log)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        
        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long time = System.currentTimeMillis() - beginTime;
            saveLog(point, time, exception);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time, Exception exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);
        
        SystemLog sysLog = new SystemLog();
        
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof Long) {
                Long userId = (Long) principal;
                sysLog.setUserId(userId);
            }
        } catch (Exception e) {
            sysLog.setUserId(null);
        }
        
        sysLog.setOperation(logAnnotation.operation());
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName() + "()");
        sysLog.setParams(Arrays.toString(joinPoint.getArgs()));
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setIp(getIpAddr(request));
            }
        } catch (Exception e) {
            sysLog.setIp("unknown");
        }
        
        sysLog.setExecutionTime(time);
        
        if (exception != null) {
            sysLog.setStatus(1);
            sysLog.setErrorMsg(exception.getMessage());
        } else {
            sysLog.setStatus(0);
        }
        
        sysLog.setCreateTime(LocalDateTime.now());
        sysLog.setUpdateTime(LocalDateTime.now());
        
        logService.saveLog(sysLog);
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
