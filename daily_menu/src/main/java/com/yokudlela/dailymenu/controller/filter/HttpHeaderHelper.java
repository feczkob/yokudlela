package com.yokudlela.dailymenu.controller.filter;

import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.model.TracedCall;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.UriInfo;

@RequestScoped
public class HttpHeaderHelper {

    @Inject
    TracedCall tracedCall;


    public String getCorrelationIdFromRequestHeader(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE);
    }

    public void setCorrelationIdIntoRequestHeader(ContainerRequestContext requestContext) {
        final String correlationIdFromHeader = getCorrelationIdFromRequestHeader(requestContext);
        final String correlationId = null == correlationIdFromHeader
                ? tracedCall.getMetadata().getCorrelationId()
                : correlationIdFromHeader;

        if (null == requestContext.getHeaderString(ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE)) {
            requestContext.getHeaders().put(ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE, List.of(correlationId));
        }
    }

    public void setCorrelationIdIntoResponseHeader(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {

        final String correlationIdFromHeader = getCorrelationIdFromRequestHeader(requestContext);
        final String correlationId = null == correlationIdFromHeader
                ? tracedCall.getMetadata().getCorrelationId()
                : correlationIdFromHeader;

        if (null == responseContext.getHeaderString(ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE)) {
            responseContext.getHeaders().put(ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE, List.of(correlationId));
        }
    }

    public URI getLocation(UriInfo uriInfo) {
        return URI.create(uriInfo.getPath());
    }

}
