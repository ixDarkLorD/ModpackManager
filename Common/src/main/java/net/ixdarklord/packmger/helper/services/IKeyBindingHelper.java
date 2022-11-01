package net.ixdarklord.packmger.helper.services;

import net.minecraft.client.KeyMapping;

import java.util.Map;

public interface IKeyBindingHelper {
    Map<String, KeyMapping> initializeKeys(String keyCategory);
    void register(Object eventPasser, KeyMapping keyMapping);
}
