package com.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Аспект для логирования выполнения методов сервисного слоя. Перехватывает все вызовы методов в
 * пакете com.example.demo.service и логирует начало, окончание выполнения и результат.
 */

@Aspect
@Component
public class LoggingAspect {

    /**
     * Advice для логирования выполнения методов сервисов. Перехватывает вызов метода, логирует его
     * начало и окончание, а также результат выполнения или исключение.
     *
     * @param joinPoint точка соединения, предоставляющая доступ к перехваченному методу
     * @return результат выполнения целевого метода
     * @throws Throwable если целевой метод выбросил исключение
     */
    @Around("execution(* com.example.demo.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        System.out.println("Method started: " + className + "." + methodName + "()");

        Object result;
        try {
            result = joinPoint.proceed();
            if (result != null) {
                System.out.println(
                        "Method ended: " + className + "." + methodName + "() - Result: " + result);
            } else {
                System.out.println(
                        "Method ended: " + className + "." + methodName + "() - No result");
            }

        } catch (Throwable throwable) {
            System.out.println("Method ended: " + className + "." + methodName +
                    "() - Exception: " + throwable.getMessage());
            throw throwable;
        }

        return result;
    }
}
