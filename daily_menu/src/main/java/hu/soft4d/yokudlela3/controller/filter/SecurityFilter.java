package hu.soft4d.yokudlela3.controller.filter;

import hu.soft4d.yokudlela3.controller.ControllerConstant;
import hu.soft4d.yokudlela3.controller.model.TracedCall;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonString;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Slf4j
@ApplicationScoped
@PreMatching
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    @Inject
    @Claim(standard = Claims.preferred_username)
    Optional<JsonString> currentUserIdentifier;

    @Inject
    @Claim(standard = Claims.full_name)
    Optional<JsonString> currentUserName;

    @Inject
    @Claim(standard = Claims.email)
    Optional<JsonString> currentUserEmail;

    @Inject
    SecurityIdentity securityIdentity;

    @Context
    SecurityContext securityCtx;

    @Context
    HttpServerRequest request;

    @Context
    UriInfo info;

    @Inject
    TracedCall tracedCall;


    @Override
    public void filter(ContainerRequestContext context) {
        log.info("[{}: {}] - Request needs to check by security filter from IP {}: {}  {}",
        ControllerConstant.CORRELATION_ID_LOG_PREFIX,
                tracedCall.getMetadata().getCorrelationId(),
                request.remoteAddress(),
                context.getMethod(),
                info.getPath());

        final String userId = currentUserIdentifier.isPresent()
                ? currentUserIdentifier.get().getString()
                : "";
        log.info("Security related attributes - Authentication scheme: {}; User: {}",
                securityCtx.getAuthenticationScheme(),
                currentUserIdentifier.isEmpty() ? "" : userId);

        if (currentUserIdentifier.isPresent()) {
            tracedCall.getMetadata().setUserId(userId);
            log.info("User identifier: {}", userId);
        }
        if (currentUserName.isPresent()) {
            final String userName = currentUserName.get().getString();
            tracedCall.getMetadata().setUserName(userName);
            log.info("User name: {}", userName);
        }
        if (currentUserEmail.isPresent()) {
            final String userEmail = currentUserEmail.get().getString();
            tracedCall.getMetadata().setUserEmail(userEmail);
            log.info("User email: {}", userEmail);
        }
        final Set<String> userRoles = securityIdentity.getRoles();
        tracedCall.getMetadata().setRoles(userRoles);
        log.info("User roles: {}", userRoles.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
    }
}
