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

import static io.javalin.apibuilder.ApiBuilder.get;

public class RestServer {
    private static Javalin app = null;

    public static int getPort() {
        return app.port();
    }

    public static void init(int port) {
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
            }).start(port);

            // Routes for the API  // todo add all other resources
            app.routes(() -> {
                get(ResourceUrls.SERVERS, Resources::getServers);
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
