package io.github.skydynamic.quickbackupmulti;

import io.github.skydynamic.quickbackupmulti.config.ConfigFactory;
import io.github.skydynamic.quickbackupmulti.config.WebUiConfig;
import io.github.skydynamic.quickbackupmulti.service.HttpApiService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.logging.Level;

public final class WebUI {
    public static final String MOD_ID = "quickbackupmulti_webui";

    @Getter
    private static final String MOD_NAME = "QuickBackupMultiR-WebUI";
    @Getter
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    @Getter
    private static HttpApiService apiService;
    @Getter
    private static Path configPath;
    @Getter
    private static WebUiConfig config;

    public static void init(
        Path configDir
    ) {
        disableGrizzlyLog();

        configPath = configDir;
        config = ConfigFactory.loadConfig();

        apiService = new HttpApiService(
            config.getHost(),
            config.getApiPort()
        );
        apiService.startService();
    }

    private static void disableGrizzlyLog() {
        // java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
    }
}
