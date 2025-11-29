package io.github.skydynamic.quickbackupmulti.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@RequireLogin
public class LoginFilter implements ContainerRequestFilter {
    private static final String HEADER_NAME = "X-Login-Token";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString(HEADER_NAME);
        boolean ok = token != null && TokenUtil.validateToken(token);
        if (!ok) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Unauthorized").build());
        }
    }
}
