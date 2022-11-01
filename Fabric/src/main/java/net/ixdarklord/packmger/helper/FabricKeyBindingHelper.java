package net.ixdarklord.packmger.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.services.IKeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class FabricKeyBindingHelper implements IKeyBindingHelper {
    @Override
    public Map<String, KeyMapping> initializeKeys(String keyCategory) {
        return new HashMap<>(Map.of(
                "KEY_INCREASE_DURABILITY_DISPLAY", new KeyMapping(String.format("key.%s.increase_durability_display", Constants.MOD_ID),
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, keyCategory),
                "KEY_DECREASE_DURABILITY_DISPLAY", new KeyMapping(String.format("key.%s.decrease_durability_display", Constants.MOD_ID),
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, keyCategory),
                "KEY_TOGGLE_DURABILITY", new KeyMapping(String.format("key.%s.toggle_durability", Constants.MOD_ID),
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, keyCategory),
                "KEY_HIDE_DURABILITY", new KeyMapping(String.format("key.%s.hide_durability", Constants.MOD_ID),
                        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, keyCategory)
        ));
    }
    @Override
    public void register(Object event, KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }
}
