package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.api.placeholder.PlaceholderTextContainer;
import net.ixdarklord.packmger.config.ConfigHandler;

public class ModpackVersionPlaceholder extends PlaceholderTextContainer {
    private static String PLACE_HOLDER_ID = "%modpack_version%";
    private static String PLACE_HOLDER_NAME = "Modpack Version";
    private static String PLACE_HOLDER_CATEGORY = "Modpack Manager";
    private static final String[] PLACE_HOLDER_DESC = new String[] {"It will display your current version."};

    public ModpackVersionPlaceholder() {
        super("modpack_version_placeholder");
    }

    @Override
    public String replacePlaceholders(String INFO) {
        String PLACE_HOLDER_ID = this.PLACE_HOLDER_ID;
        String VALUE = ConfigHandler.CLIENT.MODPACK_VERSION.get();

        return INFO.replace(PLACE_HOLDER_ID, VALUE);
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
