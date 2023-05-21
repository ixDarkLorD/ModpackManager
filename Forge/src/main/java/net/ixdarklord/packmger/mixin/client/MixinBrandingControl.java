package net.ixdarklord.packmger.mixin.client;

import com.google.common.collect.ImmutableList;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.internal.BrandingControl;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = BrandingControl.class)
public abstract class MixinBrandingControl {
    private static List<String> brandings;
    private static List<String> brandingsNoMC;

    private static void computeBranding() {
        if (brandings == null) {
            ImmutableList.Builder<String> brd = ImmutableList.builder();
            brd.add("Forge " + ForgeVersion.getVersion());
            brd.add("Minecraft " + MCPVersion.getMCVersion());

            if (!ConfigHandler.CLIENT.getTitleName().equalsIgnoreCase("Minecraft"))
                brd.add(String.format("%s %s", ConfigHandler.CLIENT.getTitleName(), ConfigHandler.CLIENT.MODPACK_VERSION.get()));
            brd.add("MCP " + MCPVersion.getMCPVersion());

            int tModCount = ModList.get().size();
            brd.add(ForgeI18n.parseMessage("fml.menu.loadingmods", tModCount));
            brandings = brd.build();
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }
}
