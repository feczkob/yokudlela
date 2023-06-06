package hu.soft4d.yokudlela3.application.annotation;

import org.slf4j.Logger;

import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@MethodLogging
@Priority(9999)
@Interceptor
public class MethodLoggingInterceptor {

    @Inject
    Logger logger;

    @AroundInvoke
    Object logInvocation(InvocationContext context) throws Exception {
        final Method method = context.getMethod();
        final String methodClass = method.getDeclaringClass().getName();
        final String methodName = method.getName();

        // ...log before
        logger.info("Method starts: {}.{}", methodClass, methodName);

        Object ret = context.proceed();

        // ...log after
        logger.info("Method ends: {}.{}", methodClass, methodName);
        return ret;
    }

}
