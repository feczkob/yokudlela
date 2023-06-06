package hu.soft4d.yokudlela3.business.error;

import hu.soft4d.yokudlela3.application.error.ErrorCase;
import hu.soft4d.yokudlela3.application.error.ExceptionUtil;
import hu.soft4d.yokudlela3.controller.ControllerConstant;
import hu.soft4d.yokudlela3.controller.model.TracedCall;

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
public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> {

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;

    @Inject
    ExceptionUtil exceptionUtil;


    @Override
    public Response toResponse(BusinessException exception) {
        log.info("[{}: {}] - Request failed because of business issue from IP {}: {}  {}",
                ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                request.method().name(),
                info.getPath());

        log.error("BusinessExceptionHandler caught an exception: ", exception);
        final ErrorCase errorCase = exception.getErrorCase();
        log.error("BusinessExceptionHandler recognized error: {}", errorCase);
        Response result = null;

        switch (errorCase) {
            case DISH_NOT_FOUND,
                    MENU_NOT_FOUND,
                    MENU_ITEM_NOT_FOUND, MENU_ITEM_CATEGORY_NOT_FOUND, MENU_ITEM_SECTION_NOT_FOUND, MENU_ITEM_VARIANT_NOT_FOUND,
                    RECIPE_NOT_FOUND:
                result = Response.status(Response.Status.NOT_FOUND)
                        .entity(exceptionUtil.createApiError(errorCase, exception))
                        .build();
                break;
            case DISH_ALREADY_EXISTS:
                result = Response.status(Response.Status.CONFLICT)
                        .entity(exceptionUtil.createApiError(errorCase, exception))
                        .build();
                break;
            default:
                result = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(exceptionUtil.createApiError(ErrorCase.UNKNOWN_BUSINESS_ERROR, exception))
                        .build();
        }

        return result;
    }
}
