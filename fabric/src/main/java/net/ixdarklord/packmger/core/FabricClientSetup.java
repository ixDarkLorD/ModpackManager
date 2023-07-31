package net.ixdarklord.packmger.core;

import net.fabricmc.api.ClientModInitializer;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.event.ClientEvents;

public class FabricClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigHandler.initializeFiles();
        ModCompatibility.registerClient();
        ClientEvents.register();
    }
}
