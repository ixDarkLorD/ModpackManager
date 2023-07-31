package net.ixdarklord.packmger.helper.services;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isConfigLoaded();

    void registerConfig();
}
