package com.example.banktoyproject.config.aop;


import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Log4j2
public class TimeTraceAop {

    // AOP 적용 위치(service package 내부 클래스)
    @Around("execution(* *..service.*.*(..))")
    public Object calculateExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            return joinPoint.proceed();
        } finally {
            sw.stop();
            long executionTime = sw.getTotalTimeMillis();

            // AOP 적용 메소드 위치 출력용
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            String task = className + "." + methodName;

            System.out.println("[TimeTraceAop] " + task + "-->" + executionTime + "(ms)");
            log.debug("[TimeTraceAop] " + task + "-->" + executionTime + "(ms)");
        }
    }
}