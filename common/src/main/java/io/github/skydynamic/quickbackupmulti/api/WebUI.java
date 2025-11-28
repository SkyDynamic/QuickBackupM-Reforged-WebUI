package io.github.skydynamic.quickbackupmulti.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Path("{path:.*}")
public class WebUI {
    private static final Map<String, String> MIME = Map.ofEntries(
        Map.entry("html", "text/html; charset=UTF-8"),
        Map.entry("js", "application/javascript; charset=UTF-8"),
        Map.entry("css", "text/css; charset=UTF-8"),
        Map.entry("json", "application/json; charset=UTF-8"),
        Map.entry("png", "image/png"),
        Map.entry("jpg", "image/jpeg"),
        Map.entry("jpeg", "image/jpeg"),
        Map.entry("svg", "image/svg+xml"),
        Map.entry("webp", "image/webp"),
        Map.entry("ico", "image/x-icon"),
        Map.entry("map", "application/json; charset=UTF-8")
    );

    @GET
    @Produces(MediaType.WILDCARD)
    public Response serve(@PathParam("path") String path) {
        if (path == null) path = "";

        if (path.startsWith("api/") || path.equals("api")) {
            return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
        }

        String resourcePath = "/static/" + (path.isEmpty() ? "index.html" : path);

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                String ext = extOf(resourcePath);
                String mime = MIME.getOrDefault(ext, "application/octet-stream");
                if (mime.startsWith("text/") || mime.contains("charset")) {
                    String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    return Response.ok(content, mime).build();
                } else {
                    byte[] bytes = is.readAllBytes();
                    return Response.ok(bytes, mime).build();
                }
            }
        } catch (Exception ignored) { }

        if (!path.contains(".")) {
            try (InputStream is = getClass().getResourceAsStream("/static/index.html")) {
                if (is != null) {
                    String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    return Response.ok(html, "text/html; charset=UTF-8").build();
                }
            } catch (Exception ignored) { }
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
    }

    private static String extOf(String p) {
        int i = p.lastIndexOf('.');
        return (i >= 0) ? p.substring(i + 1).toLowerCase(Locale.ROOT) : "";
    }
}
