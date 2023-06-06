package com.yokudlela.dailymenu.controller.filter;

import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.model.TracedCall;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Slf4j
@ApplicationScoped
@Provider
public class RequestFilter implements ContainerRequestFilter {

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;

    @Inject
    HttpHeaderHelper headerHelper;


    @Override
    public void filter(ContainerRequestContext requestContext) {
        headerHelper.setCorrelationIdIntoRequestHeader(requestContext);
        log.info("[{}: {}] - Request arrived from IP {}: {}  {}",
                ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                requestContext.getMethod(),
                info.getPath());
    }
}
