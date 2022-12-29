package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.menu.placeholder.v2.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.menu.placeholder.v2.Placeholder;
import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.CurseAPI;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.util.ManagerUtils;
import net.ixdarklord.packmger.util.VersionUtils;
import net.ixdarklord.packmger.util.WebUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.ixdarklord.packmger.compat.fancymenu.ModpackCheckingUpdateButton.isUpdateAvailable;

public class ModpackUrlVersionPlaceholder extends Placeholder {
    private static final String IDENTIFIER = ConfigHandler.CLIENT.MODPACK_UPDATE_IDENTIFIER.get();
    private static final String CURRENT_VERSION = ConfigHandler.CLIENT.MODPACK_VERSION.get();
    private static final String UPDATE_KEY = ConfigHandler.CLIENT.MODPACK_UPDATE_KEY.get();
    private static final String PLACE_HOLDER_ID = "modpack_url_version";
    private static final String PLACE_HOLDER_NAME = "Update Checker Display";
    private static final String PLACE_HOLDER_CATEGORY = "Modpack Manager";
    private static final String[] PLACE_HOLDER_DESC = new String[] {
            "It will show the checking process for an update."
    };
    private static final Map<String, List<String>> cachedValues = new HashMap<>();
    private static final List<String> currentlyUpdatingPlaceholders = new ArrayList<>();
    private static final List<String> invalidURL = new ArrayList<>();
    VersionUtils VC = new VersionUtils();

    public ModpackUrlVersionPlaceholder() {
        super(PLACE_HOLDER_ID);
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
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        if (ModpackCheckingUpdateButton.isFirstTimePressed) {
            return I18n.get("menu.packmger.press_to_check");
        }

        if (isURLInvalid(UPDATE_KEY)) {
            if (!ModpackCheckingUpdateButton.isConnectionFailed)
                ModpackCheckingUpdateButton.setConnectionFailed();
            return I18n.get("menu.packmger.connection_failed");
        }

        String ID;
        ID = cachedValues.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
        ID = ID.substring(1, ID.length()-1);
        if (!ID.equals(IDENTIFIER)) {
            if (!ModpackCheckingUpdateButton.isManifestInvalid)
                ModpackCheckingUpdateButton.setInvalidManifest(cachedValues);
            return I18n.get("menu.packmger.invalid_manifest");
        }

        if (cachedValues.get("version") != null) {
            int result = 0;
            String NEW_VERSION = cachedValues.get("version").toString().replaceAll("[()\\[\\]{}]", "");
            result = VC.compare(CURRENT_VERSION, NEW_VERSION);
            if (result < 0) {
                if (WindowHandler.CACHED_NEW_VERSION == null) {
                    WindowHandler.CACHED_NEW_VERSION = NEW_VERSION;
                    WindowHandler.updateUpdateHolder();
                }
                isUpdateAvailable = true;
                return I18n.get("menu.packmger.update_available", NEW_VERSION);
            } else {
                return I18n.get("menu.packmger.no_updates");
            }
        }

        if (!isValuesUpdating(dps.originalString)) {
            cacheValues(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID, dps.originalString, UPDATE_KEY, "modpack.identifier", "modpack.version");
        }

        return I18n.get("menu.packmger.checking_for_update");
    }

    private static boolean isURLInvalid(String link) {
        try {
            return invalidURL.contains(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
    public String getCategory() {
        return PLACE_HOLDER_CATEGORY;
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        DeserializedPlaceholderString dps = new DeserializedPlaceholderString();
        dps.placeholder = this.getIdentifier();
        return dps;
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return null;
    }

    @Override
    public @NotNull String getDisplayName() {
        return PLACE_HOLDER_NAME;
    }

    @Override
    public List<String> getDescription() {
        return List.of(PLACE_HOLDER_DESC);
    }
}
