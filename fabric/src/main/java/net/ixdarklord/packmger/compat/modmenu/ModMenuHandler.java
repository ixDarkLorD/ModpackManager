package net.ixdarklord.packmger.compat.modmenu;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModMenuHandler {
    public static String getListDisplay() {
        List<Integer> countSet = getCount(true);
        if (countSet.size() < 2) {
            return (I18n.get("menu.packmger.compat.loading_mods", countSet.get(0)));
        } else {
            return (I18n.get("menu.packmger.compat.loading_mods.include_lib", countSet.get(0), countSet.get(1)));
        }
    }
    public static void listUpdater(List<String> brd) {
        String raw = (brd.size() != 0) ? brd.get(brd.size()-1) : "";
        List<Integer> countSet = getCount(true);
        if (countSet.size() < 2) {
            if (!raw.equals(I18n.get("menu.packmger.compat.loading_mods", countSet.get(0)))) {
                brd.clear();
            }
        } else {
            if (!raw.equals(I18n.get("menu.packmger.compat.loading_mods.include_lib", countSet.get(0), countSet.get(1)))) {
                brd.clear();
            }
        }
    }
    private static List<Integer> getCount(boolean includeLib) {
        List<Integer> modsSet = new ArrayList<>();
        int modsList = ModMenu.ROOT_MODS.values().stream().filter(mod -> !mod.getBadges().contains(Mod.Badge.LIBRARY)).map(Mod::getId).collect(Collectors.toSet()).size();
        modsSet.add(modsList);
        if (includeLib && ModMenuConfig.SHOW_LIBRARIES.getValue()) {
            int libList = ModMenu.ROOT_MODS.values().stream().filter(mod -> mod.getBadges().contains(Mod.Badge.LIBRARY)).map(Mod::getId).collect(Collectors.toSet()).size();
            modsSet.add(libList);
        }
        return modsSet;
    }
}
