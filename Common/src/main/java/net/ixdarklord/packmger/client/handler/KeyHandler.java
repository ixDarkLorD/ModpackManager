package net.ixdarklord.packmger.client.handler;

import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.Services;
import net.minecraft.client.KeyMapping;

import java.util.Map;

public class KeyHandler {
    private static final String KEY_CATEGORY = String.format("key.category.%s.general", Constants.MOD_ID);
    private static final Map<String, KeyMapping> KEY_MAPPING_LIST = Services.KEY_MAPPING.initializeKeys(KEY_CATEGORY);

    public static KeyMapping KEY_INCREASE_DURABILITY_DISPLAY = KEY_MAPPING_LIST.get("KEY_INCREASE_DURABILITY_DISPLAY");
    public static KeyMapping KEY_DECREASE_DURABILITY_DISPLAY = KEY_MAPPING_LIST.get("KEY_DECREASE_DURABILITY_DISPLAY");
    public static KeyMapping KEY_TOGGLE_DURABILITY = KEY_MAPPING_LIST.get("KEY_TOGGLE_DURABILITY");
    public static KeyMapping KEY_HIDE_DURABILITY = KEY_MAPPING_LIST.get("KEY_HIDE_DURABILITY");
    public static void registerKeys(Object event) {
        for (var entry : KEY_MAPPING_LIST.entrySet()) {
            Services.KEY_MAPPING.register(event, entry.getValue());
        }
    }
}
