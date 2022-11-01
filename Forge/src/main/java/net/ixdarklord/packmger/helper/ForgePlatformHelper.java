package net.ixdarklord.packmger.helper;

import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.helper.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT.SPEC, Constants.MOD_ID + "/client-config.toml");
    }

    @Override
    public boolean isConfigLoaded() {
        return ConfigHandler.CLIENT.SPEC.isLoaded();
    }
}
