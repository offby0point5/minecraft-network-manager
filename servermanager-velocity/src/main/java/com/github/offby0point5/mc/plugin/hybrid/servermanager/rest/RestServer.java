package com.github.offby0point5.mc.plugin.hybrid.servermanager.rest;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerVelocity;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ResourceUrls;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RestServer {
    private static Javalin app = null;

    public static void init(String host, int port) {
        if (app == null) {
            // Get the current class loader.
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Temporarily set this thread's class loader to the plugin's class loader.
            // Replace JavalinTestPlugin.class with your own plugin's class.
            Thread.currentThread().setContextClassLoader(ServermanagerVelocity.class.getClassLoader());

            // Instantiate the web server (which will now load using the plugin's class loader).
            app = Javalin.create(config -> {
                config.defaultContentType = "application/json";
                config.showJavalinBanner = false;
                config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
            }).start(host, port);

            // Routes for the API
            app.routes(() -> {
                get(ResourceUrls.SERVERS, Resources::getServers);
                put(ResourceUrls.PORTS, Resources::putPorts);
                put(ResourceUrls.GROUPS, Resources::putGroups);
                put(ResourceUrls.FLAGS, Resources::putFlags);
                delete(ResourceUrls.DELETE, Resources::deleteServer);
                get(ResourceUrls.MENU_MAIN, Resources::getMenuMain);
                get(ResourceUrls.MENU_GROUP, Resources::getMenuGroup);

                post(ResourceUrls.SEND_PLAYER_TO_SERVER, Resources::postSendPlayerServer);
                post(ResourceUrls.SEND_PLAYER_TO_GROUP, Resources::postSendPlayerGroup);
            });

            // Put the original class loader back where it was.
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    private static OpenApiOptions getOpenApiOptions() {
        Optional<PluginContainer> plugin = ServermanagerVelocity.proxy.getPluginManager()
                .getPlugin("servermanager-velocity");
        assert plugin.isPresent();
        PluginDescription description = plugin.get().getDescription();
        assert description.getName().isPresent();
        assert description.getVersion().isPresent();
        assert description.getDescription().isPresent();
        Info applicationInfo = new Info()
                .title(description.getName().get())
                .version(description.getVersion().get())
                .description(description.getDescription().get());
        return new OpenApiOptions(applicationInfo)
                .path("/swagger-docs")
                .activateAnnotationScanningFor("com.github.offby0point5.mc.plugin.hybrid.servermanager.rest")
                .swagger(new SwaggerOptions("/swagger"));
    }

    public static void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
