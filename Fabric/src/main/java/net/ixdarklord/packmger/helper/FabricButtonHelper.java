package net.ixdarklord.packmger.helper;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.ixdarklord.packmger.helper.services.IButtonHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public class FabricButtonHelper implements IButtonHelper {
    @Override
    public List<?> buttonsList(Object event) {
        return Screens.getButtons((Screen)event);
    }

    @Override
    public int screenWidth(Object event) {
        Screen screen = (Screen)event;
        return screen.width;
    }

    @Override
    public int screenHeight(Object event) {
        Screen screen = (Screen)event;
        return screen.height;
    }

    @Override
    public boolean ifPresent(Object event, Button button) {
        boolean isPresent = false;
        List<AbstractWidget> list = Screens.getButtons((Screen)event);
        if (button != null) {
            for (var entry : list) {
                if (entry.getMessage().equals(button.getMessage())) {
                    isPresent = true;
                }
            }
        }
        return isPresent;
    }

    @Override
    public void registerButton(Object event, Button button) {}
}
