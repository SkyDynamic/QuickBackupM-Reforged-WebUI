package io.github.skydynamic.quickbackupmulti.api;

import io.github.skydynamic.quickbackupmulti.security.LoginFilter;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class WebApplication extends ResourceConfig {
    public WebApplication() {
        register(LoginFilter.class);
        register(WebUI.class);
        register(Login.class);
        register(Ping.class);
        register(BackupManagerApi.class);
        register(JacksonFeature.class);
    }
}
