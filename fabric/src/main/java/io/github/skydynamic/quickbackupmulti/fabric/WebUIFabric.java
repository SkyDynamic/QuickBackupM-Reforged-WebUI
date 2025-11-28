package io.github.skydynamic.quickbackupmulti.fabric;

import io.github.skydynamic.quickbackupmulti.WebUI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class WebUIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        WebUI.init(configDir);
    }
}
