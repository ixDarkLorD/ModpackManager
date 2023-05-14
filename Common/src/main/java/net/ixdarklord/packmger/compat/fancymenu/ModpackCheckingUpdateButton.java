package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.api.buttonaction.ButtonActionContainer;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.minecraft.client.resources.language.I18n;

import java.util.Map;

public class ModpackCheckingUpdateButton extends ButtonActionContainer{
    private static final String BUTTON_ID = "modpack_manager_update_button";
    private static final String BUTTON_DESC = "CFU Button (Check for updates!)";
    private static final String IDENTIFIER = ConfigHandler.CLIENT.MODPACK_UPDATE_IDENTIFIER.get();
    private static final String UPDATE_KEY = ConfigHandler.CLIENT.MODPACK_UPDATE_KEY.get();
    public static boolean isFirstTimePressed = ConfigHandler.CLIENT.IS_FIRST_TIME_PRESSED.get();
    public static boolean isUpdateAvailable = false;
    public static boolean isManifestInvalid = false;
    public static boolean isConnectionFailed = false;

    public ModpackCheckingUpdateButton() {
        super("modpack_manager_update_button");
    }

    @Override
    public void execute(String value) {
        if (!isUpdateAvailable) {
            isFirstTimePressed = false;
            isManifestInvalid = false;
            isConnectionFailed = false;
            ModpackVersionRequirement.reloadMenu(false);
            ModpackUrlVersionPlaceholder.reloadMenu(false);
        }
    }

    public static void setInvalidManifest(Map values) {
        if (!isManifestInvalid) {
            String ID = values.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
            ID = ID.substring(1, ID.length()-1);
            try {
                int PROJECT_ID = Integer.parseInt(UPDATE_KEY);
                String log = I18n.get( "menu.packmger.warn.curse.identifier_dissimilar");
                Constants.LOGGER.warn("\n\u001B[31m"+ log +"\nProject Slug: [{}] | Config Identifier: [{}]\u001B[0m", ID, IDENTIFIER);
            } catch (Exception ignored) {
                String log = I18n.get( "menu.packmger.warn.url.identifier_dissimilar");
                Constants.LOGGER.warn("\n\u001B[31m"+ log +"\nJSON Identifier: [{}] | Config Identifier: [{}]\u001B[0m", ID, IDENTIFIER);
            }
            isManifestInvalid = true;
        }
    }
    public static void setConnectionFailed() {
        if (!isConnectionFailed) {
            try {
                int PROJECT_ID = Integer.parseInt(UPDATE_KEY);
                String log = I18n.get( "menu.packmger.error.curse.connection_failed", UPDATE_KEY);
                Constants.LOGGER.error("\u001B[31m"+ log +"\u001B[0m");
            } catch (Exception ignored) {
                String log = I18n.get( "menu.packmger.error.url.connection_failed", UPDATE_KEY);
                Constants.LOGGER.error("\u001B[31m"+ log +"\u001B[0m");
            }
            isConnectionFailed = true;
        }
    }
    @Override
    public String getAction() {
        return BUTTON_ID;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public String getActionDescription() {
        return BUTTON_DESC;
    }

    @Override
    public String getValueDescription() {
        return null;
    }

    @Override
    public String getValueExample() {
        return null;
    }
}