package com.yokudlela.kitchen.application

import mu.KotlinLogging
import org.apache.commons.lang3.ArrayUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import java.util.*
import java.util.stream.Collectors

@Component
@Aspect
class AspectLoggerImpl {

    // ? check if result is from cache
    // ? https://stackoverflow.com/questions/70154869/spring-cacheable-need-to-know-value-return-from-cache-or-not

    // * https://www.baeldung.com/spring-aop-pointcut-tutorial

    // * https://stackoverflow.com/questions/73407640/not-able-to-read-annotation-property-inside-aspect-when-writing-pointcut-in-a-me

    // TODO log level, make this to be based on profile

    private val logger = KotlinLogging.logger {}

    @Pointcut(value = "@within(aspectLogger) || @annotation(aspectLogger)")
    fun annotated(aspectLogger: com.yokudlela.kitchen.application.AspectLogger) {
        /**
         * Pointcut
         * @param aspectLogger AspectLogger
         */
    }

    @Around("annotated(aspectLogger)")
    @Throws(Throwable::class)
    fun logMethodWithAspect(proceedingJoinPoint: ProceedingJoinPoint, aspectLogger: com.yokudlela.kitchen.application.AspectLogger?): Any? {
        val stopWatch = StopWatch()
        stopWatch.start()
        logger.info("Starting method: {}", getMethodNameWithArgs(proceedingJoinPoint))
        val retVal: Any? = proceedingJoinPoint.proceed()
        stopWatch.stop()
        logger.info(
            "Method finished: {} {}",
            getMethodName(proceedingJoinPoint),
            retVal
        )
        // TODO catch exception, log, throw again (try - finally)
        // ! Catching exceptions here prevent ControllerAdvice to do so
        return retVal
    }

    fun getMethodNameWithArgs(proceedingJoinPoint: ProceedingJoinPoint): String {
        return String.format("%s.%s(%s)",
            proceedingJoinPoint.signature.declaringType.name,
            getMethodName(proceedingJoinPoint),
            getMethodArgs(proceedingJoinPoint).stream()
                .map { c: Any? -> c?.toString() ?: "null" }
                .collect(Collectors.joining(", ")))
    }

    fun getMethodName(proceedingJoinPoint: ProceedingJoinPoint): String {
        return proceedingJoinPoint.signature.name
    }

    private fun getMethodArgs(proceedingJoinPoint: ProceedingJoinPoint): List<Any> {
        return getMethodAnnotation(proceedingJoinPoint, com.yokudlela.kitchen.application.AspectLogger::class.java).map { aspectLogger: com.yokudlela.kitchen.application.AspectLogger ->
            if (aspectLogger.skipParametersByIndex.isEmpty()) {
                return@map listOf<Any>(*proceedingJoinPoint.args)
            } else {
                val args = proceedingJoinPoint.args
                val skippedArgs: IntArray = aspectLogger.skipParametersByIndex
                val result: MutableList<Any> = ArrayList()
                for (i in args.indices) {
                    if (!ArrayUtils.contains(skippedArgs, i)) {
                        result.add(args[i])
                    }
                }
                return@map result
            }
        }.orElseGet { emptyList() }
    }

    private fun <T : Annotation> getMethodAnnotation(
        proceedingJoinPoint: ProceedingJoinPoint,
        annotationClass: Class<T>
    ): Optional<T> {
        val signature = proceedingJoinPoint.signature
        return if (signature is MethodSignature) Optional.ofNullable(signature.method.getAnnotation(annotationClass)) else Optional.empty()
    }
}