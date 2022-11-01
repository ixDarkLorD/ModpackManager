package net.ixdarklord.packmger.core;

import net.fabricmc.api.ClientModInitializer;
import net.ixdarklord.packmger.event.ClientEvents;

public class FabricClientSetup implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommonSetup.init();
        ClientEvents.register();
    }
}
