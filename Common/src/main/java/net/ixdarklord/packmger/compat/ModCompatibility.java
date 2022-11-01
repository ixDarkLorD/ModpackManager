package net.ixdarklord.packmger.compat;

import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.compat.fancymenu.FancyMenuRegistry;
import net.ixdarklord.packmger.helper.Services;

public class ModCompatibility {

    public static String FANCY_MENU_ID = "fancymenu";
    public static String MOD_MENU_ID = "modmenu";
    public static void registerClient() {
        String LOADER = Services.PLATFORM.getPlatformName();
        // Common Mods
        if (LOADER.equals("Forge") || LOADER.equals("Fabric")) {
            if (isFancyMenuLoaded(true)) FancyMenuRegistry.init();
        }
        // Fabric Mods
        if (LOADER.equals("Fabric")) {
            isModMenuLoaded(true);
        }
    }

    public static boolean isFancyMenuLoaded(boolean printLog) {
        if (Services.PLATFORM.isModLoaded(FANCY_MENU_ID)) {
            if (printLog) Constants.LOGGER.info("[{}] FancyMenu is loaded!", Constants.MOD_NAME);
            return true;
        } else {
            if (printLog) Constants.LOGGER.info("[{}] FancyMenu isn't loaded!", Constants.MOD_NAME);
            return false;
        }
    }
    public static boolean isModMenuLoaded(boolean printLog) {
        if (Services.PLATFORM.isModLoaded(MOD_MENU_ID)) {
            if (printLog) Constants.LOGGER.info("[{}] ModMenu is loaded!", Constants.MOD_NAME);
            return true;
        } else {
            if (printLog) Constants.LOGGER.info("[{}] ModMenu isn't loaded!", Constants.MOD_NAME);
            return false;
        }
    }
}
