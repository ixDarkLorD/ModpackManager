package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.api.visibilityrequirements.VisibilityRequirement;
import de.keksuccino.konkrete.input.CharacterFilter;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.CurseAPI;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.util.ManagerUtils;
import net.ixdarklord.packmger.util.VersionUtils;
import net.ixdarklord.packmger.util.WebUtils;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.ixdarklord.packmger.compat.fancymenu.ModpackCheckingUpdateButton.isUpdateAvailable;

public class ModpackUpdateRequirement extends VisibilityRequirement {

    private static final String IDENTIFIER = ConfigHandler.CLIENT.MODPACK_UPDATE_IDENTIFIER.get();
    private static final String CURRENT_VERSION = ConfigHandler.CLIENT.MODPACK_VERSION.get();
    private static final String UPDATE_KEY = ConfigHandler.CLIENT.MODPACK_UPDATE_KEY.get();
    private static final String REQUIREMENT_NAME = "Is Update Available";
    private static final String[] REQUIREMENT_DESC = new String[] {
            "This requirement will behave depending on new update availability!",
            "If there is an update, it will sit to true. Otherwise, it will sit to false."
    };
    private static Map<String, List<String>> cachedValues = new HashMap<>();
    private static List<String> invalidURL = new ArrayList<>();
    private static List<String> currentlyUpdatingPlaceholders = new ArrayList<>();
    VersionUtils VC = new VersionUtils();

    public ModpackUpdateRequirement() {
        super("modpack_update_requirement");
    }

    public static void reloadMenu(boolean isDebugMode) {
        try {
            WindowHandler.CACHED_NEW_VERSION = null;
            isUpdateAvailable = false;
            cachedValues.clear();
            invalidURL.clear();
            if (isDebugMode) Constants.LOGGER.info("ModpackUpdateRequirement cache successfully cleared!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isRequirementMet(@Nullable String value) {
        if (!ModpackCheckingUpdateButton.isFirstTimePressed) {
            if (!isURLInvalid(UPDATE_KEY)) {
                List<String> version = cachedValues.get("version");
                if (version != null) {
                    String ID = cachedValues.get(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID).toString();
                    ID = ID.substring(1, ID.length()-1);
                    if (ID.equals(IDENTIFIER)) {
                        String NEW_VERSION = cachedValues.get("version").toString().replaceAll("[\\(\\)\\[\\]\\{\\}]", "");
                        int result = VC.compare(CURRENT_VERSION, NEW_VERSION);
                        if (result < 0) {
                            if (WindowHandler.CACHED_NEW_VERSION == null) {
                                WindowHandler.CACHED_NEW_VERSION = NEW_VERSION;
                                WindowHandler.updateUpdateHolder();
                            }
                            isUpdateAvailable = true;
                            return true;
                        }
                    } else {
                        if (!ModpackCheckingUpdateButton.isManifestInvalid)
                            ModpackCheckingUpdateButton.setInvalidManifest(cachedValues);
                    }
                } else {
                    cacheValues(ConfigHandler.CLIENT.KeyData.IDENTIFIER.ID, "version", UPDATE_KEY, "modpack.identifier", "modpack.version");
                }
            } else {
                if (!ModpackCheckingUpdateButton.isConnectionFailed)
                    ModpackCheckingUpdateButton.setConnectionFailed();
            }
        }
        return false;
    }
    private static boolean isURLInvalid(String link) {
        try {
            return invalidURL.contains(link);
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
    public String getDisplayName() {
        return REQUIREMENT_NAME;
    }

    @Override
    public List<String> getDescription() {
        return List.of(REQUIREMENT_DESC);
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public String getValueDisplayName() {
        return null;
    }

    @Override
    public String getValuePreset() {
        return null;
    }

    @Override
    public CharacterFilter getValueInputFieldFilter() {
        return null;
    }
}
