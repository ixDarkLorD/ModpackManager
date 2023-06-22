package net.ixdarklord.packmger.client.handler;

import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.compat.fancymenu.FancyMenuRegistry;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.Services;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WindowHandler {
    private static final Logger LOGGER = LogManager.getLogger("packmger/WindowHandler");
    private static final File CONFIG = new File("config/" + Constants.MOD_ID + "/client-config.toml");
    public static String TITLE;
    public static String CACHED_TITLE;
    public static String CACHED_NEW_VERSION;
    private static String STARTUP_TITLE;
    private static String LOADING_SCREEN_GS_TITLE;
    private static String GAMESTATE_TYPE;
    private static boolean IS_STARTUP_TITLE_VISIBLE = true;
    private static boolean IS_LOADING_SCREEN_GS_VISIBLE = true;

    private static final String INFO = "Creating " + Constants.MOD_NAME +  " config files...";
    private static final List<String> CONFIG_VALUES = new ArrayList<>();
    private static final List<String> CONFIG_SYNTAX = new ArrayList<>();

    public static String modifyTitle() {
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.TITLE.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.VERSION.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.STARTUP_TITLE_VISIBILITY.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.STARTUP_TITLE_TEXT.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.GAMESTATE_VISIBILITY.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.LOADING_SCREEN_GAMESTATE_VISIBILITY.ID + " = ");
        CONFIG_VALUES.add(ConfigHandler.CLIENT.KeyData.LOADING_SCREEN_TEXT.ID + " = ");

        CONFIG_SYNTAX.add(ConfigHandler.CLIENT.SyntaxData.MC_VERSION.ID);
        CONFIG_SYNTAX.add(ConfigHandler.CLIENT.SyntaxData.MODPACK_VERSION.ID);
        CONFIG_SYNTAX.add(ConfigHandler.CLIENT.SyntaxData.GAMESTATE.ID);

        if (CACHED_TITLE == null) {
            initializeTitle();
        } else {
            TITLE = CACHED_TITLE;
        }

        if (TITLE != null && TITLE.equals(CACHED_TITLE)) {
            if (IS_STARTUP_TITLE_VISIBLE && !Services.PLATFORM.isConfigLoaded()) {
                TITLE = STARTUP_TITLE;
            } else {
                String holder = ConfigHandler.CLIENT.getSyntaxIndex(CACHED_TITLE, TITLE, CONFIG_SYNTAX.get(2));

                if (ModCompatibility.isFancyMenuLoaded(false)) {
                    String cleanedTitle = TITLE.replace(holder, "").trim();
                    FancyMenuRegistry.modifyTitle(cleanedTitle);
                }

                if (!ConfigHandler.CLIENT.SAFE_WINDOW_TITLE.get()) {
                    updateGameState(holder);
                    updateUpdateHolder();
                }
            }
            return TITLE;
        }
        return INFO;
    }

    public static void updateUpdateHolder() {
        if (CACHED_NEW_VERSION != null) {
            StringBuilder updateGS = new StringBuilder(TITLE);
            updateGS.append(String.format(" | %s", I18n.get("menu.packmger.update_available", CACHED_NEW_VERSION)));
            TITLE = updateGS.toString();
            Minecraft.getInstance().getWindow().setTitle(TITLE);
        }
    }

    private static void initializeTitle() {
        try {
            //FOR READING THE CONFIG.TOML
            Scanner reader = new Scanner(CONFIG);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                data = data.replaceAll("\"", "");

                if (CACHED_TITLE == null) {
                    //TITLE
                    if (data.contains(CONFIG_VALUES.get(0)) && data.indexOf(CONFIG_VALUES.get(0)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(0), "");
                        data = data.substring(1);
                        CACHED_TITLE = data;

                        //MINECRAFT VERSION
                        CACHED_TITLE = CACHED_TITLE.replace(CONFIG_SYNTAX.get(0), SharedConstants.getCurrentVersion().getName());
                    }
                } else {
                    //MODPACK VERSION
                    if (data.contains(CONFIG_VALUES.get(1)) && data.indexOf(CONFIG_VALUES.get(1)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(1), "");
                        data = data.substring(1);
                        CACHED_TITLE = CACHED_TITLE.replace(CONFIG_SYNTAX.get(1), data);

                    //STARTUP TITLE VISIBILITY
                    } else if (data.contains(CONFIG_VALUES.get(2)) && data.indexOf(CONFIG_VALUES.get(2)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(2), "");
                        data = data.substring(1);
                        IS_STARTUP_TITLE_VISIBLE = Boolean.parseBoolean(data);

                    //STARTUP TITLE TEXT
                    } else if (data.contains(CONFIG_VALUES.get(3)) && data.indexOf(CONFIG_VALUES.get(3)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(3), "");
                        data = data.substring(1);
                        STARTUP_TITLE = data;

                    //GAMESTATE TYPE
                    } else if (data.contains(CONFIG_VALUES.get(4)) && data.indexOf(CONFIG_VALUES.get(4)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(4), "");
                        data = data.substring(1);
                        if (data.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.ALL.name())) {
                            GAMESTATE_TYPE = ConfigHandler.CLIENT.GamestateType.ALL.name();
                        } else if (data.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_EXTRA.name())) {
                            GAMESTATE_TYPE = ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_EXTRA.name();
                        } else if (data.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_PAUSE_SCREEN.name())) {
                            GAMESTATE_TYPE = ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_PAUSE_SCREEN.name();
                        } else if (data.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.ONLY_PLAYING_MODE.name())) {
                            GAMESTATE_TYPE = ConfigHandler.CLIENT.GamestateType.ONLY_PLAYING_MODE.name();
                        } else {
                            GAMESTATE_TYPE = ConfigHandler.CLIENT.GamestateType.ALL.name();
                        }

                    //LOADING SCREEN GAMESTATE VISIBILITY
                    } else if (data.contains(CONFIG_VALUES.get(5)) && data.indexOf(CONFIG_VALUES.get(5)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(5), "");
                        data = data.substring(1);
                        IS_LOADING_SCREEN_GS_VISIBLE = Boolean.parseBoolean(data);

                    //LOADING SCREEN GAMESTATE TITLE
                    } else if (data.contains(CONFIG_VALUES.get(6)) && data.indexOf(CONFIG_VALUES.get(6)) == 1 && data.indexOf("") == 0) {
                        data = data.replace(CONFIG_VALUES.get(6), "");
                        data = data.substring(1);
                        LOADING_SCREEN_GS_TITLE = data;
                    }
                }
            }
            if (CACHED_TITLE != null) TITLE = CACHED_TITLE.trim();
        } catch (FileNotFoundException e) {
            LOGGER.warn(INFO);
        }
    }

    private static void updateGameState(String GSHolder) {
        StringBuilder pauseGS = new StringBuilder();
        ClientPacketListener clientpacketlistener = Minecraft.getInstance().getConnection();

        // CHECK IF THE GAMESTATE TYPE EQUAL TO AN EXISTING VALUE
        if (ConfigHandler.CLIENT.GamestateType.ifEqual(GAMESTATE_TYPE)) {

            // WHEN YOU JOIN or CONNECT TO THE WORLD
            if (clientpacketlistener != null && clientpacketlistener.getConnection().isConnected()) {
                if (Minecraft.getInstance().getSingleplayerServer() != null && !Minecraft.getInstance().getSingleplayerServer().isPublished()) {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), I18n.get("title.singleplayer"));
                } else if (Minecraft.getInstance().isConnectedToRealms()) {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), I18n.get("title.multiplayer.realms"));
                } else if (Minecraft.getInstance().getSingleplayerServer() == null && (Minecraft.getInstance().getCurrentServer() == null || !Minecraft.getInstance().getCurrentServer().isLan())) {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), I18n.get("title.multiplayer.other"));
                } else {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), I18n.get("title.singleplayer"));
                }

                // THE PAUSE SCREEN
                if (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen.isPauseScreen() && !GAMESTATE_TYPE.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.ONLY_PLAYING_MODE.name())) {
                    pauseGS.append(TITLE);
                    if (isPauseScreen() || isOpenLinkScreen() || GAMESTATE_TYPE.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_PAUSE_SCREEN.name())) {
                        pauseGS.append(" ["+I18n.get("menu.paused")+"]");
                    } else if (isAdvancementScreen()){
                        pauseGS.append(" ["+ I18n.get("menu.paused") + " - " + I18n.get("gui.advancements") +"]");
                    } else {
                        pauseGS.append(" ["+ I18n.get("menu.paused") + " - " + Minecraft.getInstance().screen.getTitle().getString()+"]");
                    }
                    TITLE = pauseGS.toString();

                    // GAME SCREEN TITLES
                } else if (GAMESTATE_TYPE.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.ALL.name()) || GAMESTATE_TYPE.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.PLAYING_MODE_AND_EXTRA.name())) {
                    if (Minecraft.getInstance().screen != null && !Minecraft.getInstance().screen.getTitle().getString().isEmpty()) {
                        pauseGS.append(TITLE);
                        pauseGS.append(" ["+Minecraft.getInstance().screen.getTitle().getString()+"]");
                        TITLE = pauseGS.toString();
                    }
                }

                // LOADING AND MAIN SCREEN
            } else if (GAMESTATE_TYPE.equalsIgnoreCase(ConfigHandler.CLIENT.GamestateType.ALL.name())) {
                if (Minecraft.getInstance().screen != null && !Minecraft.getInstance().screen.getTitle().getString().isEmpty()) {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), Minecraft.getInstance().screen.getTitle().getString());
                } else if (IS_LOADING_SCREEN_GS_VISIBLE && Minecraft.getInstance().screen == null && Minecraft.checkModStatus().shouldReportAsModified()) {
                    TITLE = TITLE.replace(CONFIG_SYNTAX.get(2), LOADING_SCREEN_GS_TITLE);
                }

                // IF CONDITIONS DON'T MATCH, IT WILL CLEAR IT
                else {
                    TITLE = TITLE.replace(GSHolder, "");
                }
            } else {
                TITLE = TITLE.replace(GSHolder, "");
            }
        } else {
            TITLE = TITLE.replace(GSHolder, "");
        }
    }
    private static boolean isPauseScreen() {
        String SCREEN = "class net.minecraft.client.gui.screens.PauseScreen";
        return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen.getClass().toString().equals(SCREEN);
    }
    private static boolean isOpenLinkScreen() {
        String SCREEN = "class net.minecraft.client.gui.screens.ConfirmLinkScreen";
        return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen.getClass().toString().equals(SCREEN);
    }
    private static boolean isAdvancementScreen() {
        String SCREEN = "class net.minecraft.client.gui.screens.advancements.AdvancementsScreen";
        return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen.getClass().toString().equals(SCREEN);
    }
}
