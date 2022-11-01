package net.ixdarklord.packmger.helper.services;

import net.minecraft.client.gui.components.Button;

import java.util.List;

public interface IButtonHelper {
    List<?> buttonsList(Object event);
    int screenWidth(Object event);
    int screenHeight(Object event);
    boolean ifPresent(Object event, Button button);
    void registerButton(Object event, Button button);
}
