package com.yokudlela.dailymenu.controller.model;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class TracedCall {

    private static final String KEY_METADATA = "metadata";

    @Inject
    CallMetadataBean metadata;

    @Inject
    CurrentVertxRequest currentVertxRequest;


    public CallMetadataBean getMetadata() {
        final RoutingContext ctx = currentVertxRequest.getCurrent();
        if (null == ctx.get(KEY_METADATA)) {
            ctx.put(KEY_METADATA, metadata);
        }
        return ctx.get(KEY_METADATA);
    }

}
