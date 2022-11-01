package net.ixdarklord.packmger.core;

import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.helper.Services;

public class CommonSetup {
    public static void init() {
        ConfigHandler.initializeFiles();
        ModCompatibility.registerClient();
    }
}