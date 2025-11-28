package io.github.skydynamic.quickbackupmulti.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import io.github.skydynamic.quickbackupmulti.WebUI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigFactory {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    public static WebUiConfig createConfig() {
        return new WebUiConfig();
    }

    public static WebUiConfig loadConfig() {
        File configFile = WebUI.getConfigPath().resolve("QuickBackupM-WebUI.json").toFile();
        if (!configFile.exists()) {
            return createConfig().save();
        }
        try (FileInputStream input = new FileInputStream(configFile)) {
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            return GSON.fromJson(reader, WebUiConfig.class);
        } catch (IOException e) {
            WebUI.getLOGGER().warn("Failed to load config file", e);
            return createConfig();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveConfig(WebUiConfig config) {
        File configFile = WebUI.getConfigPath().resolve("QuickBackupM-WebUI.json").toFile();
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                WebUI.getLOGGER().error("Failed to create config file", e);
                return;
            }
        }
        try (BufferedWriter bfw = Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(config, bfw);
        } catch (IOException e) {
            WebUI.getLOGGER().error("Failed to save config file", e);
        }
    }
}
