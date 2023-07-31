package net.ixdarklord.packmger.client.handler;

import net.ixdarklord.packmger.client.button.VersionCheckerButton;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;

public class ScreenHandler {
    public void onScreenInitPost(final ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            this.titleScreenHandler(event);
        }
    }
    private void titleScreenHandler(ScreenEvent.Init.Post event) {
        if (!ModCompatibility.isFancyMenuLoaded(false))
            new VersionCheckerButton(event);
    }
}