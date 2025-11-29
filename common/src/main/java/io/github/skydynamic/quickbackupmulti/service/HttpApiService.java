package io.github.skydynamic.quickbackupmulti.service;

import io.github.skydynamic.quickbackupmulti.WebUI;
import io.github.skydynamic.quickbackupmulti.api.WebApplication;
import io.github.skydynamic.quickbackupmulti.security.LoginSecretUtil;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpApiService {
    private String host = "localhost";
    private int port = 53222;

    private HttpServer httpServer;

    private final Map<String, Long> tempLoginSecret = new HashMap<>();

    public HttpApiService(
        String host,
        int port
    ) {
        this.host = host;
        this.port = port;
    }

    public HttpApiService() {
    }

    public void startService() {
        URI baseUri = URI.create("http://" + host + ":" + port + "/");
        ResourceConfig rc = new WebApplication();
        httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, rc);

        WebUI.getLOGGER().info("HTTP Service started at http://{}:{}/", host, port);
    }

    public void stopService() {
        if (httpServer != null) {
            httpServer.shutdownNow();
            WebUI.getLOGGER().info("HTTP Service stopped.");
        } else {
            WebUI.getLOGGER().warn("HTTP Service is not running.");
        }
    }

    public void newTempLoginSecret() {
        tempLoginSecret.clear();
        String secret = LoginSecretUtil.generateLoginSecret();
        long expiryTime = System.currentTimeMillis() + 5 * 60 * 1000;
        tempLoginSecret.put(secret, expiryTime);
        WebUI.getLOGGER().info("Generated new temporary login secret: {}", secret);
//        ServerManager serverManager = QuickbakcupmultiReforged.getServerManager();
//        if (serverManager != null) {
//            MinecraftServer server = serverManager.getCommandSource().getServer();
//            if (!server.isDedicatedServer()) {
//                server.getPlayerList().getPlayers().forEach((player) -> {
//                        player.sendSystemMessage(
//                            Component.literal("[QuickBackupMulti-WebUI] New temporary login request: ")
//                                .append(Component.literal(secret)
//                                    .withStyle(style -> style.withColor(ChatFormatting.GOLD))
//                                    .withStyle(style -> style.withHoverEvent(
//                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy"))))
//                                    .withStyle(style -> style.withClickEvent(
//                                        new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, secret)))
//                                ));
//                    }
//                );
//            }
//        }
    }

    public boolean validateTempLoginSecret(String secret) {
        Long expiryTime = tempLoginSecret.get(secret);
        if (expiryTime != null && System.currentTimeMillis() < expiryTime) {
            tempLoginSecret.remove(secret);
            return true;
        } else {
            tempLoginSecret.clear();
            return false;
        }
    }
}
