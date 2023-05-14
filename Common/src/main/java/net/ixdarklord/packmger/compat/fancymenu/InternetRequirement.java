package net.ixdarklord.packmger.compat.fancymenu;

import de.keksuccino.fancymenu.api.visibilityrequirements.VisibilityRequirement;
import de.keksuccino.konkrete.input.CharacterFilter;
import net.ixdarklord.packmger.core.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class InternetRequirement extends VisibilityRequirement {
    private static final String REQUIREMENT_NAME = "Is Internet Connection Available";
    private static final String[] REQUIREMENT_DESC = new String[] {
            "This requirement will behave depending on internet connectivity!",
            "If there is an internet connection, it will sit to true. Otherwise, it will sit to false."
    };
    private boolean isInternetReachable = false;

    public InternetRequirement() {
        super("internet_requirement");
    }

    @Override
    public boolean isRequirementMet(@Nullable String value) {
        new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                isInternetReachable= address.isReachable(5000); // Timeout in milliseconds
            } catch (IOException ignored) {
                Constants.LOGGER.warn("Error occurred while checking internet connectivity! Check if you're connected to the internet");
            }
        }).start();
        return isInternetReachable;
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
