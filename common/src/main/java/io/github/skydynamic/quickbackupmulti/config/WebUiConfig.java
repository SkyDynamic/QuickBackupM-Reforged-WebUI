package io.github.skydynamic.quickbackupmulti.config;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WebUiConfig {
    private final String secretKey = UUID.randomUUID().toString().replace("-", "");
    private final String host = "localhost";
    private final int apiPort = 53222;
    private final int webPort = 53223;

    public WebUiConfig save() {
        ConfigFactory.saveConfig(this);
        return this;
    }
}
