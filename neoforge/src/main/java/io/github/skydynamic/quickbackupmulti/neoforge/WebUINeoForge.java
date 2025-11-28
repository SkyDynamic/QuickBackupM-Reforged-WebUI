package io.github.skydynamic.quickbackupmulti.neoforge;

import io.github.skydynamic.quickbackupmulti.WebUI;
import net.neoforged.fml.common.Mod;

import java.nio.file.Path;

@Mod(WebUI.MOD_ID)
public final class WebUINeoForge {
    public WebUINeoForge() {
        Path configDir = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
        WebUI.init(configDir);
    }
}
