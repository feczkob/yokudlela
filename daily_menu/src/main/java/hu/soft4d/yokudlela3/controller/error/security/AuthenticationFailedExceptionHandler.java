package hu.soft4d.yokudlela3.controller.error.security;

import hu.soft4d.yokudlela3.application.error.ErrorCase;
import hu.soft4d.yokudlela3.application.error.ExceptionUtil;
import hu.soft4d.yokudlela3.controller.ControllerConstant;
import hu.soft4d.yokudlela3.controller.model.TracedCall;

import io.quarkus.security.AuthenticationFailedException;
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
public class AuthenticationFailedExceptionHandler implements ExceptionMapper<AuthenticationFailedException> {

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;

    @Inject
    ExceptionUtil exceptionUtil;


    @Override
    public Response toResponse(AuthenticationFailedException exception) {
        log.info("[{}: {}] - Request did not passed by security filter from IP {}: {}  {}",
                ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                request.method().name(),
                info.getPath());

        log.error("AuthenticationFailedExceptionHandler caught an exception: ", exception);
        log.error("AuthenticationFailedExceptionHandler recognized error: {}", exception.getMessage());
        return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(exceptionUtil.createApiError(ErrorCase.AUTHENTICATION_FAILURE, exception))
                        .build();
    }

}
