package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.api.placeholder.PlaceholderTextContainer;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.CurseAPI;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.config.ConfigHandler.CLIENT.KeyData;
import net.ixdarklord.packmger.util.ManagerUtils;
import net.ixdarklord.packmger.util.VersionUtils;
import net.ixdarklord.packmger.util.WebUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.I18n;

import java.util.*;

import static net.ixdarklord.packmger.compat.fancymenu.ModpackCheckingUpdateButton.isUpdateAvailable;

public class ModpackUrlVersionPlaceholder extends PlaceholderTextContainer {
    private static final String IDENTIFIER = ConfigHandler.CLIENT.MODPACK_UPDATE_IDENTIFIER.get();
    private static final String CURRENT_VERSION = ConfigHandler.CLIENT.MODPACK_VERSION.get();
    private static final String UPDATE_KEY = ConfigHandler.CLIENT.MODPACK_UPDATE_KEY.get();
    private static final String PLACE_HOLDER_ID = "%modpack_url_version%";
    private static final String PLACE_HOLDER_NAME = "Update Checker Display";
    private static final String PLACE_HOLDER_CATEGORY = "Modpack Manager";
    private static final String[] PLACE_HOLDER_DESC = new String[] {"It will show the checking process for an update."};
    private static Map<String, List<String>> cachedValues = new HashMap<>();
    private static List<String> currentlyUpdatingPlaceholders = new ArrayList<>();
    private static List<String> invalidURL = new ArrayList<>();
    VersionUtils VC = new VersionUtils();

    public ModpackUrlVersionPlaceholder() {
        super("modpack_url_version_placeholder");
    }

    public static void reloadMenu(boolean isDebugMode) {
        try {
            WindowHandler.CACHED_NEW_VERSION = null;
            isUpdateAvailable = false;
            cachedValues.clear();
            invalidURL.clear();
            if (isDebugMode) Constants.LOGGER.info("ModpackUrlVersionPlaceholder cache successfully cleared!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String replacePlaceholders(String INFO) {
        String placeholder = INFO;
        List<String> LIST = List.of(PLACE_HOLDER_ID);
        String ID;

        for (String getLIST : LIST) {
            if (!ModpackCheckingUpdateButton.isFirstTimePressed) {
                if (!isURLInvalid(UPDATE_KEY)) {
                    String checkingMSG = I18n.get("menu.packmger.checking_for_update");
                    List<String> lines = getCachedValues(getLIST);
                    if (lines != null) {
                        ID = cachedValues.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
                        ID = ID.substring(1, ID.length()-1);
                        if (!lines.isEmpty() && !lines.contains(checkingMSG) && ID.equals(IDENTIFIER)) {
                            int result = 0;
                            result = VC.compare(CURRENT_VERSION, lines.get(0));
                            if (result < 0) {
                                if (WindowHandler.CACHED_NEW_VERSION == null) {
                                    WindowHandler.CACHED_NEW_VERSION = lines.get(0);
                                    WindowHandler.updateUpdateHolder();
                                }
                                placeholder = placeholder.replace(getLIST, I18n.get("menu.packmger.update_available", lines.get(0)));
                                isUpdateAvailable = true;

                            } else {
                                placeholder = placeholder.replace(getLIST, I18n.get("menu.packmger.no_updates"));
                            }
                        } else {
                            placeholder = placeholder.replace(getLIST, I18n.get("menu.packmger.invalid_manifest"));
                            if (!ModpackCheckingUpdateButton.isManifestInvalid)
                                ModpackCheckingUpdateButton.setInvalidManifest(cachedValues);
                        }
                    } else {
                        if (!isValuesUpdating(getLIST)) {
                            cacheValues(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID, getLIST, UPDATE_KEY, "modpack.identifier", "modpack.version");
                        }
                        placeholder = placeholder.replace(getLIST, checkingMSG);
                    }
                } else {
                    placeholder = placeholder.replace(getLIST, I18n.get("menu.packmger.connection_failed"));
                    if (!ModpackCheckingUpdateButton.isConnectionFailed)
                        ModpackCheckingUpdateButton.setConnectionFailed();
                }
            } else {
                placeholder = placeholder.replace(getLIST, I18n.get("menu.packmger.press_to_check"));
            }
        }

        return placeholder;
    }

    private static boolean isURLInvalid(String link) {
        try {
            return invalidURL.contains(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static List<String> getCachedValues(String placeholder) {
        try {
            return cachedValues.get(placeholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isValuesUpdating(String placeholder) {
        try {
            return currentlyUpdatingPlaceholders.contains(placeholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void cacheValues(String identifier, String placeholder, String KEY, String identifierJsonPath, String versionJsonPath) {
        try {
            if (!currentlyUpdatingPlaceholders.contains(placeholder)) {
                currentlyUpdatingPlaceholders.add(placeholder);
                new Thread(() -> {
                    try {
                        if (WebUtils.isValidURL(KEY)) {
                            cachedValues.put(identifier, ManagerUtils.getManifestValue(KEY, identifierJsonPath));
                            cachedValues.put(placeholder, ManagerUtils.getManifestValue(KEY, versionJsonPath));
                        } else {
                            try {
                                String MC_VERSION = SharedConstants.getCurrentVersion().getName();
                                int PROJECT_ID = Integer.parseInt(KEY);
                                CurseAPI curseAPI = new CurseAPI(PROJECT_ID, IDENTIFIER);
                                String IDENTIFIER = curseAPI.getProjectSlug();
                                List<String> VERSIONS = curseAPI.getProjectVersions(MC_VERSION, CURRENT_VERSION);

                                cachedValues.put(identifier, Collections.singletonList(IDENTIFIER));
                                cachedValues.put(placeholder, Collections.singletonList(VERSIONS.get(0)));
                            } catch (NumberFormatException e) {
                                invalidURL.add(KEY);
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

    @Override
    public String getPlaceholder() {
        return PLACE_HOLDER_ID;
    }

    @Override
    public String getCategory() {
        return PLACE_HOLDER_CATEGORY;
    }

    @Override
    public String getDisplayName() {
        return PLACE_HOLDER_NAME;
    }

    @Override
    public String[] getDescription() {
        return PLACE_HOLDER_DESC;
    }
}
