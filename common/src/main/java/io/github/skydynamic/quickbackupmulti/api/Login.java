package io.github.skydynamic.quickbackupmulti.api;

import io.github.skydynamic.quickbackupmulti.WebUI;
import io.github.skydynamic.quickbackupmulti.response.ApiResponse;
import io.github.skydynamic.quickbackupmulti.response.ApiStatusCode;
import io.github.skydynamic.quickbackupmulti.security.TokenUtil;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("api")
public class Login {
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<String> login(@FormParam("secret") String secret) {
        if (WebUI.getApiService().validateTempLoginSecret(secret)) {
            String token = TokenUtil.generateToken();
            if (token == null) {
                return new ApiResponse<>(ApiStatusCode.INTERNAL_SERVER_ERROR, "Failed to generate token");
            }
            return new ApiResponse<>(token);
        } else {
            return new ApiResponse<>(ApiStatusCode.SECRET_EXPIRED_OR_NOT_INVALID, "Invalid or expired login secret");
        }
    }

    @POST
    @Path("/requestLoginSecret")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<Void> requestLoginSecret() {
        WebUI.getApiService().newTempLoginSecret();
        return new ApiResponse<>(null);
    }

    @POST
    @Path("/verifyToken")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<Void> verifyToken(@FormParam("token") String token) {
        if (TokenUtil.validateToken(token)) {
            return new ApiResponse<>(null);
        } else {
            return new ApiResponse<>(ApiStatusCode.TOKEN_EXPIRED_OR_NOT_INVALID, null);
        }
    }
}
