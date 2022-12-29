package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.menu.placeholder.v2.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.menu.placeholder.v2.Placeholder;
import net.ixdarklord.packmger.config.ConfigHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModpackTitlePlaceholder extends Placeholder {
    private static final String PLACE_HOLDER_ID = "modpack_title";
    private static final String PLACE_HOLDER_NAME = "Modpack Title";
    private static final String PLACE_HOLDER_CATEGORY = "Modpack Manager";
    private static final String[] PLACE_HOLDER_DESC = new String[] {
            "It will display the modpack title."
    };

    public ModpackTitlePlaceholder() {
        super(PLACE_HOLDER_ID);
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
    public String getReplacementFor(DeserializedPlaceholderString deserializedPlaceholderString) {
        return ConfigHandler.CLIENT.getTitleName();
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
