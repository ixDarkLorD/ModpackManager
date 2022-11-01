package net.ixdarklord.packmger.helper;

import net.ixdarklord.packmger.helper.services.IButtonHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.List;

public class ForgeButtonHelper implements IButtonHelper {
    @Override
    public List<?> buttonsList(Object event) {
        ScreenEvent.InitScreenEvent.Post sePost = (ScreenEvent.InitScreenEvent.Post) event;
        return sePost.getListenersList();
    }

    @Override
    public int screenWidth(Object event) {
        ScreenEvent.InitScreenEvent.Post sePost = (ScreenEvent.InitScreenEvent.Post) event;
        return sePost.getScreen().width;
    }

    @Override
    public int screenHeight(Object event) {
        ScreenEvent.InitScreenEvent.Post sePost = (ScreenEvent.InitScreenEvent.Post) event;
        return sePost.getScreen().height;
    }

    @Override
    public boolean ifPresent(Object event, Button button) {
        return false;
    }

    @Override
    public void registerButton(Object event, Button button) {
        ScreenEvent.InitScreenEvent.Post sePost = (ScreenEvent.InitScreenEvent.Post) event;
        sePost.addListener(button);
    }
}
