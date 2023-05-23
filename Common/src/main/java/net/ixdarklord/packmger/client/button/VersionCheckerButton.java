package net.ixdarklord.packmger.client.button;

import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.CurseAPI;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.Services;
import net.ixdarklord.packmger.util.ManagerUtils;
import net.ixdarklord.packmger.util.VersionUtils;
import net.ixdarklord.packmger.util.WebUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class VersionCheckerButton extends ButtonBase {
    private static final String IDENTIFIER = ConfigHandler.CLIENT.MODPACK_UPDATE_IDENTIFIER.get();
    private static final String CURRENT_VERSION = ConfigHandler.CLIENT.MODPACK_VERSION.get();
    private static final String UPDATE_KEY = ConfigHandler.CLIENT.MODPACK_UPDATE_KEY.get();
    private static boolean IS_FIRST_TIME_PRESSED = ConfigHandler.CLIENT.IS_FIRST_TIME_PRESSED.get();
    private static final Map<String, List<String>> cachedValues = new HashMap<>();
    private static final List<String> invalidURL = new ArrayList<>();
    private static final List<String> currentlyUpdatingPlaceholders = new ArrayList<>();

    private static String buttonMessage;
    private static boolean isUpdateAvailable;
    private static boolean isInternetReachable;
    private static boolean isProcessed;
    private static boolean isActivated;
    private final VersionUtils VC = new VersionUtils();
    public static Button modButton;
    public static Object screenEvent;

    public VersionCheckerButton(Object event) {
        screenEvent = event;
        initializeButton(event);
    }
    @Override
    protected void initializeButton(Object event) {
        this.moveButtonsLayout(Services.BUTTON.buttonsList(event), Services.BUTTON.screenHeight(event) / 4 + 48 + 72 + 12 );
        int i = 24;
        modButton = new Button(
                Services.BUTTON.screenWidth(event) / 2 - 100, Services.BUTTON.screenHeight(event) / 4 + 48 + 24 * 4 - i,
                200, 20,
                Component.empty(),
                button -> {
                    IS_FIRST_TIME_PRESSED = false;
                    isActivated = false;
                    buttonFunction();
                }
        );
        if (IS_FIRST_TIME_PRESSED) {
            buttonMessage = "\u00A7f" + I18n.get("menu.packmger.press_to_check");
            modButton.setMessage(Component.literal(buttonMessage));
        } else {
            if (!isProcessed) {
                buttonFunction();
            } else if (!cachedValues.isEmpty()) {
                updateButton();
                adjustAlignment();
            }
        }
        Services.BUTTON.registerButton(event, modButton);
    }

    protected void buttonFunction() {
        isProcessed = true;

        if (!isInternetReachable) {
            buttonMessage = "\u00A7c" + I18n.get("menu.packmger.no_internet");
            modButton.setMessage(Component.literal(buttonMessage));
            adjustAlignment();
            return;
        }
        if (!isUpdateAvailable) {
            buttonMessage = "\u00A7f" + I18n.get("menu.packmger.checking_for_update");
            modButton.setMessage(Component.literal(buttonMessage));
            adjustAlignment();
            if (!isValuesUpdating("version")) {
                cacheValues(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID, "version", UPDATE_KEY, "modpack.identifier", "modpack.version");
            }
        }
    }

    private static boolean previousChecking;
    public static void checkInternetConnectivity() {
        new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                isInternetReachable = address.isReachable(5000); // Timeout in milliseconds
            } catch (IOException ignored) {
                isInternetReachable = false;
                previousChecking = true;
            }
        }).start();

        if (!IS_FIRST_TIME_PRESSED && isInternetReachable != previousChecking) {
            modButton.onPress();
        }

        previousChecking = isInternetReachable;
    }

    @Override
    protected void updateButton() {
        if (isURLInvalid(UPDATE_KEY)) {
            buttonMessage = "\u00A7c" + I18n.get("menu.packmger.connection_failed");
            modButton.setMessage(Component.literal(buttonMessage));
            logConnectionFailed();
            return;
        }

        String ID = cachedValues.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
        ID = ID.substring(1, ID.length()-1);
        if (!ID.equals(IDENTIFIER)) {
            buttonMessage = "\u00A7c" + I18n.get("menu.packmger.invalid_manifest");
            modButton.setMessage(Component.literal(buttonMessage));
            logInvalidManifest();
            return;
        }

        if (cachedValues.get("version") != null) {
            String NEW_VERSION = cachedValues.get("version").toString().replaceAll("[()\\[\\]{}]", "");
            int result = VC.compare(CURRENT_VERSION, NEW_VERSION);
            if (result < 0) {
                if (WindowHandler.CACHED_NEW_VERSION == null) {
                    WindowHandler.CACHED_NEW_VERSION = NEW_VERSION;
                    WindowHandler.updateUpdateHolder();
                }
                buttonMessage = "\u00A76" + I18n.get("menu.packmger.update_available", NEW_VERSION);
                modButton.setMessage(Component.literal(buttonMessage));
                isUpdateAvailable = true;
            } else {
                buttonMessage = "\u00A7f" + I18n.get("menu.packmger.no_updates");
                modButton.setMessage(Component.literal(buttonMessage));
            }
        }
    }

    private void adjustAlignment() {
        int i = Math.max(0, Minecraft.getInstance().font.width(buttonMessage) - 200);
        assert Minecraft.getInstance().screen != null;
        if (i > 0) {
            i += 10;
            modButton.x = Minecraft.getInstance().screen.width / 2 - 100 - (i/2);
            modButton.setWidth(200 + i);
        } else {
            modButton.x = Minecraft.getInstance().screen.width / 2 - 100;
            modButton.setWidth(200);
        }
    }
    private void cacheValues(String identifier, String placeholder, String KEY, String identifierJsonPath, String versionJsonPath) {
        try {
            if (!currentlyUpdatingPlaceholders.contains(placeholder)) {
                currentlyUpdatingPlaceholders.add(placeholder);
                new Thread(() -> {
                    try {
                        if (WebUtils.isValidURL(KEY)) {
                            cachedValues.put(identifier, ManagerUtils.getManifestValue(KEY, identifierJsonPath));
                            cachedValues.put(placeholder, ManagerUtils.getManifestValue(KEY, versionJsonPath));
                            updateButton();
                        } else {
                            try {
                                String MC_VERSION = SharedConstants.getCurrentVersion().getName();
                                int PROJECT_ID = Integer.parseInt(KEY);
                                CurseAPI curseAPI = new CurseAPI(PROJECT_ID, IDENTIFIER);
                                String IDENTIFIER = curseAPI.getProjectSlug();
                                List<String> VERSIONS = curseAPI.getProjectVersions(MC_VERSION, CURRENT_VERSION);

                                cachedValues.put(identifier, Collections.singletonList(IDENTIFIER));
                                cachedValues.put(placeholder, Collections.singletonList(VERSIONS.get(0)));
                                updateButton();
                            } catch (NumberFormatException e) {
                                invalidURL.add(KEY);
                                updateButton();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        currentlyUpdatingPlaceholders.remove(placeholder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isValuesUpdating(String placeholder) {
        try {
            return currentlyUpdatingPlaceholders.contains(placeholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    private boolean isURLInvalid(String link) {
        try {
            return invalidURL.contains(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    private void logInvalidManifest() {
        if (!isActivated) {
            isActivated = true;
            String ID = cachedValues.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
            ID = ID.substring(1, ID.length()-1);
            try {
                Integer.parseInt(UPDATE_KEY);
                String log = I18n.get( "menu.packmger.warn.curse.identifier_dissimilar");
                Constants.LOGGER.warn("\n\u001B[31m"+ log +"\nProject Slug: [{}] | Config Identifier: [{}]\u001B[0m", ID, IDENTIFIER);
            } catch (Exception ignored) {
                String log = I18n.get( "menu.packmger.warn.url.identifier_dissimilar");
                Constants.LOGGER.warn("\n\u001B[31m"+ log +"\nJSON Identifier: [{}] | Config Identifier: [{}]\u001B[0m", ID, IDENTIFIER);
            }
        }
    }
    private void logConnectionFailed() {
        if (!isActivated) {
            isActivated = true;
            try {
                Integer.parseInt(UPDATE_KEY);
                String log = I18n.get( "menu.packmger.error.curse.connection_failed", UPDATE_KEY);
                Constants.LOGGER.error("\u001B[31m"+ log +"\u001B[0m");
            } catch (Exception ignored) {
                String log = I18n.get( "menu.packmger.error.url.connection_failed", UPDATE_KEY);
                Constants.LOGGER.error("\u001B[31m"+ log +"\u001B[0m");
            }
        }
    }
}
