package net.ixdarklord.packmger.helper;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.helper.services.IPlatformHelper;
import net.minecraftforge.fml.config.ModConfig;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void registerConfig() {
        ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, ConfigHandler.CLIENT.SPEC, Constants.MOD_ID + "/client-config.toml");
    }

    @Override
    public boolean isConfigLoaded() {
        return ConfigHandler.CLIENT.SPEC.isLoaded();
    }
}
