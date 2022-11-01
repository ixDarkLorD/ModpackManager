package net.ixdarklord.packmger.compat.fancymenu;


import de.keksuccino.fancymenu.FancyMenu;
import de.keksuccino.fancymenu.api.buttonaction.ButtonActionRegistry;
import de.keksuccino.fancymenu.api.placeholder.PlaceholderTextRegistry;
import de.keksuccino.fancymenu.api.visibilityrequirements.VisibilityRequirementRegistry;
import de.keksuccino.konkrete.config.Config;

public class FancyMenuRegistry {
    public static void init() {
        try {
            ButtonActionRegistry.registerButtonAction(new ModpackCheckingUpdateButton());
            VisibilityRequirementRegistry.registerRequirement(new ModpackVersionRequirement());
            PlaceholderTextRegistry.registerPlaceholder(new ModpackTitlePlaceholder());
            PlaceholderTextRegistry.registerPlaceholder(new ModpackVersionPlaceholder());
            PlaceholderTextRegistry.registerPlaceholder(new ModpackUrlVersionPlaceholder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void modifyTitle(String title) {
        Config config = new Config(FancyMenu.MOD_DIR.getPath() + "/config.txt");
        try {
            config.setValue("customwindowtitle", title);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
