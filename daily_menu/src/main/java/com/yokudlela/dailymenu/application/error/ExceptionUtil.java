package com.yokudlela.dailymenu.application.error;

import com.yokudlela.dailymenu.controller.model.TracedCall;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ExceptionUtil {

    @Inject
    TracedCall tracedCall;


    public String getStacktraceAsString(Throwable throwable) {
        if (null == throwable) {
            return null;
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public ApiError createApiError(ErrorCase errorCase, RuntimeException exception) {
        return new ApiError(
                errorCase.getErrorCode(),
                errorCase.name(), // TODO: translation
                tracedCall.getMetadata().getCorrelationId(),
                getStacktraceAsString(exception.getCause()));
    }

}
