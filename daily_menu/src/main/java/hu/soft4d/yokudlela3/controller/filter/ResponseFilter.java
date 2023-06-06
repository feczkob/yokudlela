package hu.soft4d.yokudlela3.controller.filter;

import hu.soft4d.yokudlela3.controller.ControllerConstant;
import hu.soft4d.yokudlela3.controller.model.TracedCall;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Slf4j
@ApplicationScoped
@Provider
public class ResponseFilter implements ContainerResponseFilter {

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;

    @Inject
    HttpHeaderHelper headerHelper;


    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        headerHelper.setCorrelationIdIntoResponseHeader(requestContext, responseContext);
        log.info("[{}: {}] - Request from IP {}: {}  {} - finished with response and status code is: {}",
                ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                requestContext.getMethod(),
                info.getPath(),
                responseContext.getStatusInfo().getStatusCode());
    }
}
