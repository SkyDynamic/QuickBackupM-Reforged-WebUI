package io.github.skydynamic.quickbackupmulti.api;


import io.github.skydynamic.quickbackupmulti.response.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("api")
public class Ping {
    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<String> ping() {
        return new ApiResponse<>("Pong!");
    }
}
