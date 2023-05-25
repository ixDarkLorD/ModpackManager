package net.ixdarklord.packmger.client.renderer;

import com.google.common.collect.Lists;
import net.fabricmc.loader.api.FabricLoader;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.compat.modmenu.ModMenuHandler;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class BrandingRenderer {
    private static List<String> brandings = new ArrayList<>();
    private static void compute() {
        if (brandings.isEmpty()) {
            List<String> brd = new ArrayList<>();
            var loader = FabricLoader.getInstance();

            if (loader.getModContainer("quilt_loader").isPresent()) {
                brd.add("Quilt " + loader.getModContainer("quilt_loader").get().getMetadata().getVersion().getFriendlyString());
            } else if (loader.getModContainer("fabricloader").isPresent())
                brd.add("Fabric " + loader.getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString());

            brd.add("Minecraft " + SharedConstants.getCurrentVersion().getName() + (Minecraft.getInstance().isDemo() ? " | Demo" : ""));
            if (!ConfigHandler.CLIENT.getTitleName().equalsIgnoreCase("Minecraft")) {
                brd.add(String.format("%s %s", ConfigHandler.CLIENT.getTitleName(), ConfigHandler.CLIENT.MODPACK_VERSION.get()));
            }
            if (ModCompatibility.isModMenuLoaded(false)) {
                brd.add(ModMenuHandler.getListDisplay());
            } else {
                brd.add(I18n.get("menu.packmger.loading_mods", FabricLoader.getInstance().getAllMods().size()));
            }
            brandings = brd;
        }
        if (ModCompatibility.isModMenuLoaded(false)) {
            ModMenuHandler.listUpdater(brandings);
        }
    }
    private static List<String> getList(boolean reverse) {
        compute();
        return reverse ? Lists.reverse(brandings) : brandings;
    }
    public static void forEachLine(boolean reverse, BiConsumer<Integer, String> lineConsumer) {
        final List<String> brandings = getList(reverse);
        IntStream.range(0, brandings.size()).boxed().forEachOrdered(idx -> lineConsumer.accept(idx, brandings.get(idx)));
    }
}
