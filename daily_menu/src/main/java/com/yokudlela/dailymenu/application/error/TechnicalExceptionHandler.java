package com.yokudlela.dailymenu.application.error;

import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.model.TracedCall;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@ApplicationScoped
@Provider
public class TechnicalExceptionHandler implements ExceptionMapper<TechnicalException> {

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;

    @Inject
    ExceptionUtil exceptionUtil;


    @Override
    public Response toResponse(TechnicalException exception) {
        log.info("[{}: {}] - Request failed because of technical issue from IP {}: {}  {}",
                ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                request.method().name(),
                info.getPath());

        log.error("TechnicalExceptionHandler caught an exception: ", exception);
        final ErrorCase errorCase = exception.getErrorCase();
        log.error("TechnicalExceptionHandler recognized error: {}", errorCase);
        Response result = null;

        switch (errorCase) {
            case RECIPE_SERVICE_UNAVAILABLE:
                result = Response.status(Response.Status.NOT_FOUND)
                        .entity(exceptionUtil.createApiError(errorCase, exception))
                        .build();
                break;
            default:
                result = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(exceptionUtil.createApiError(ErrorCase.UNKNOWN_TECHNICAL_ERROR, exception))
                        .build();
        }

        return result;
    }
}
