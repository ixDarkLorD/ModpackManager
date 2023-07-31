package net.ixdarklord.packmger.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.ixdarklord.packmger.client.button.VersionCheckerButton;
import net.ixdarklord.packmger.client.handler.KeyHandler;
import net.ixdarklord.packmger.client.handler.ScreenHandler;

public class ClientEvents {
    public static void register() {
        KeyHandler.registerKeys(null);
        ClientTickEvents.START_CLIENT_TICK.register(VersionCheckerButton::checkInternetConnectivity);
        ClientTickEvents.END_CLIENT_TICK.register(KeyEvents::registerEvents);
        ScreenEvents.AFTER_INIT.register(ScreenHandler::onScreenInitPost);
    }
}
