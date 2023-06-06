package hu.soft4d.yokudlela3.controller.filter;

import static hu.soft4d.yokudlela3.controller.ControllerConstant.CORRELATION_ID_HEADER_ATTRIBUTE;

import hu.soft4d.yokudlela3.controller.model.TracedCall;

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
        return requestContext.getHeaderString(CORRELATION_ID_HEADER_ATTRIBUTE);
    }

    public void setCorrelationIdIntoRequestHeader(ContainerRequestContext requestContext) {
        final String correlationIdFromHeader = getCorrelationIdFromRequestHeader(requestContext);
        final String correlationId = null == correlationIdFromHeader
                ? tracedCall.getMetadata().getCorrelationId()
                : correlationIdFromHeader;

        if (null == requestContext.getHeaderString(CORRELATION_ID_HEADER_ATTRIBUTE)) {
            requestContext.getHeaders().put(CORRELATION_ID_HEADER_ATTRIBUTE, List.of(correlationId));
        }
    }

    public void setCorrelationIdIntoResponseHeader(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {

        final String correlationIdFromHeader = getCorrelationIdFromRequestHeader(requestContext);
        final String correlationId = null == correlationIdFromHeader
                ? tracedCall.getMetadata().getCorrelationId()
                : correlationIdFromHeader;

        if (null == responseContext.getHeaderString(CORRELATION_ID_HEADER_ATTRIBUTE)) {
            responseContext.getHeaders().put(CORRELATION_ID_HEADER_ATTRIBUTE, List.of(correlationId));
        }
    }

    public URI getLocation(UriInfo uriInfo) {
        return URI.create(uriInfo.getPath());
    }

}
