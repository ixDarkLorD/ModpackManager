package net.ixdarklord.packmger.client.handler;

import net.ixdarklord.packmger.client.button.VersionCheckerButton;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public class ScreenHandler {
    public static void onScreenInitPost(Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof TitleScreen) {
            titleScreenHandler(screen);
        }
    }
    private static void titleScreenHandler(Screen screen) {
        if (!ModCompatibility.isFancyMenuLoaded(false)) {
            new VersionCheckerButton(screen);
        }
    }
}
