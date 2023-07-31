package net.ixdarklord.packmger.helper;

import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.services.IButtonHelper;
import net.ixdarklord.packmger.helper.services.IKeyBindingHelper;
import net.ixdarklord.packmger.helper.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IButtonHelper BUTTON = load(IButtonHelper.class);
    public static final IKeyBindingHelper KEY_MAPPING = load(IKeyBindingHelper.class);
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
