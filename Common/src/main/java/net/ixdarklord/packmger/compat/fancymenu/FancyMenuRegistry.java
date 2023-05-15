package net.ixdarklord.packmger.compat.fancymenu;


import de.keksuccino.fancymenu.FancyMenu;
import de.keksuccino.fancymenu.api.buttonaction.ButtonActionRegistry;
import de.keksuccino.fancymenu.menu.loadingrequirement.v2.LoadingRequirementRegistry;
import de.keksuccino.fancymenu.menu.placeholder.v2.PlaceholderRegistry;

public class FancyMenuRegistry {
    public static void init() {
        try {
            ButtonActionRegistry.registerButtonAction(new ModpackCheckingUpdateButton());
            LoadingRequirementRegistry.registerRequirement(new InternetRequirement());
            LoadingRequirementRegistry.registerRequirement(new ModpackVersionRequirement());
            PlaceholderRegistry.registerPlaceholder(new ModpackTitlePlaceholder());
            PlaceholderRegistry.registerPlaceholder(new ModpackVersionPlaceholder());
            PlaceholderRegistry.registerPlaceholder(new ModpackUrlVersionPlaceholder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void modifyTitle(String title) {
        try {
            FancyMenu.config.setValue("customwindowtitle", title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
