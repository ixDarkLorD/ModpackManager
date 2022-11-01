package net.ixdarklord.packmger.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.services.IKeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class ForgeKeyBindingHelper implements IKeyBindingHelper {
    @Override
    public Map<String, KeyMapping> initializeKeys(String KEY_CATEGORY) {
        return new HashMap<>(Map.of(
                "KEY_INCREASE_DURABILITY_DISPLAY", new KeyMapping(String.format("key.%s.increase_durability_display", Constants.MOD_ID),
                        KeyConflictContext.UNIVERSAL,
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY),
                "KEY_DECREASE_DURABILITY_DISPLAY", new KeyMapping(String.format("key.%s.decrease_durability_display", Constants.MOD_ID),
                        KeyConflictContext.UNIVERSAL,
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY),
                "KEY_TOGGLE_DURABILITY", new KeyMapping(String.format("key.%s.toggle_durability", Constants.MOD_ID),
                        KeyConflictContext.UNIVERSAL,
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY),
                "KEY_HIDE_DURABILITY", new KeyMapping(String.format("key.%s.hide_durability", Constants.MOD_ID),
                        KeyConflictContext.UNIVERSAL,
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, KEY_CATEGORY)
        ));
    }
    @Override
    public void register(Object eventPasser, KeyMapping keyMapping) {
        RegisterKeyMappingsEvent event = (RegisterKeyMappingsEvent) eventPasser;
        event.register(keyMapping);
    }
}
