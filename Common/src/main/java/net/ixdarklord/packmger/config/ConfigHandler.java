package net.ixdarklord.packmger.config;

import net.ixdarklord.packmger.core.Constants;
import net.ixdarklord.packmger.client.renderer.ItemDurability;
import net.ixdarklord.packmger.helper.Services;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ConfigHandler {
    public static ItemDurability.Serializer ItemBlacklist = new ItemDurability.Serializer();
    public static void initializeFiles() {
        Services.PLATFORM.registerConfig();
        ItemBlacklist.registerConfig(String.format("config/%s/item_blacklist.json", Constants.MOD_ID));
    }

    private static boolean isGameShutting = false;
    public static void saveData() {
        if (isGameShutting) return;
        isGameShutting = true;

        CLIENT.DURABILITY_DISPLAY_SIZE.set(ItemDurability.CACHED_VALUE);
        Constants.LOGGER.info("[{}] Saving values in Config!", Constants.MOD_NAME);
    }

    public static class CLIENT {
        public static final ForgeConfigSpec SPEC;
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec.ConfigValue<String> MODPACK_TITLE;
        public static final ForgeConfigSpec.ConfigValue<String> MODPACK_VERSION;
        public static final ForgeConfigSpec.ConfigValue<String> MODPACK_UPDATE_IDENTIFIER;
        public static final ForgeConfigSpec.ConfigValue<String> MODPACK_UPDATE_KEY;

        public static final ForgeConfigSpec.BooleanValue DURABILITY_DISPLAY_VISIBILITY;
        public static final ForgeConfigSpec.DoubleValue DURABILITY_DISPLAY_SIZE;

        public static final ForgeConfigSpec.BooleanValue STARTUP_TITLE_VISIBILITY;
        public static final ForgeConfigSpec.ConfigValue<String> STARTUP_TITLE_TEXT;
        public static final ForgeConfigSpec.BooleanValue LOADING_STATE_VISIBILITY;
        public static final ForgeConfigSpec.ConfigValue<String> LOADING_STATE_TEXT;
        public static final ForgeConfigSpec.ConfigValue<String> GAMESTATE_VISIBILITY;
        public static final ForgeConfigSpec.BooleanValue IS_FIRST_TIME_PRESSED;

        static {
            BUILDER.comment(
                    "To sit it properly, visit the mod wiki website for more information:",
                    "https://github.com/ixDarkLorD/ModpackManager/wiki");

            BUILDER.push("Manifest");
            MODPACK_TITLE = BUILDER
                    .comment("Modpack Title | Syntax: "
                            +SyntaxData.MODPACK_VERSION.ID+", "
                            +SyntaxData.MC_VERSION.ID+", "
                            +SyntaxData.GAMESTATE.ID)
                    .define(KeyData.TITLE.ID, "CloudPack "+SyntaxData.MODPACK_VERSION.ID+" - "+SyntaxData.GAMESTATE.ID);

            MODPACK_VERSION = BUILDER
                    .comment("Modpack Version")
                    .define(KeyData.VERSION.ID, "v0.0.1");

            MODPACK_UPDATE_IDENTIFIER = BUILDER
                    .comment("Modpack Identifier")
                    .define(KeyData.IDENTIFIER.ID, "Example Modpack");
            MODPACK_UPDATE_KEY = BUILDER.define(KeyData.PROJECT_ID_OR_URL.ID, "https://pastebin.com/raw/XzCsX7u8");
            BUILDER.pop();

            BUILDER.push("Visuals");
            DURABILITY_DISPLAY_VISIBILITY = BUILDER
                    .comment("Durability Display Visibility and Size")
                    .define(KeyData.DURABILITY_DISPLAY_VISIBILITY.ID, true);
            DURABILITY_DISPLAY_SIZE = BUILDER.defineInRange(KeyData.DURABILITY_DISPLAY_SIZE.ID, 0.5d, 0.0d, 1.0d);
            BUILDER.pop();

            BUILDER.push("General");
            STARTUP_TITLE_VISIBILITY = BUILDER
                    .comment("Startup Title Visibility and Text")
                    .define(KeyData.STARTUP_TITLE_VISIBILITY.ID, true);
            STARTUP_TITLE_TEXT = BUILDER.define(KeyData.STARTUP_TITLE_TEXT.ID, "Please wait! Starting up...");

            GAMESTATE_VISIBILITY = BUILDER
                    .comment("Gamestate Visibility Mode | Syntax: "
                            +GamestateType.ALL.name()+", "
                            +GamestateType.PLAYING_MODE_AND_EXTRA.name()+", "
                            +GamestateType.PLAYING_MODE_AND_PAUSE_SCREEN.name()+", "
                            +GamestateType.ONLY_PLAYING_MODE.name())
                    .defineInList(KeyData.GAMESTATE_VISIBILITY.ID, GamestateType.ALL.name(), GamestateType.TYPE_LIST);

            LOADING_STATE_VISIBILITY = BUILDER
                    .comment("Startup Loading Visibility and Text")
                    .define(KeyData.LOADING_SCREEN_GAMESTATE_VISIBILITY.ID, true );
            LOADING_STATE_TEXT = BUILDER.define(KeyData.LOADING_SCREEN_TEXT.ID, "Loading the game...");

            IS_FIRST_TIME_PRESSED = BUILDER
                    .comment("CFU Button(Checking for Updates) Behavior")
                    .define(KeyData.IS_FIRST_TIME_PRESSED.ID, true );
            BUILDER.pop();
            SPEC = BUILDER.build();
        }

        public enum GamestateType {
            ALL,
            PLAYING_MODE_AND_EXTRA,
            PLAYING_MODE_AND_PAUSE_SCREEN,
            ONLY_PLAYING_MODE;

            public static final List<String> TYPE_LIST = Stream.of(GamestateType.values()).map(GamestateType::name).collect(Collectors.toList());

            public static boolean ifEqual(String TYPE) {
                for (GamestateType value : GamestateType.values()) {
                    if (value.name().equalsIgnoreCase(TYPE))
                        return true;
                }
                return false;
            }
        }
        public enum KeyData {
            TITLE("title"),
            VERSION("version"),
            IDENTIFIER("identifier"),
            PROJECT_ID_OR_URL("project_id_or_url"),
            DURABILITY_DISPLAY_VISIBILITY("durability_display_visibility"),
            DURABILITY_DISPLAY_SIZE("durability_display_size"),
            STARTUP_TITLE_VISIBILITY("startup_title_visibility"),
            STARTUP_TITLE_TEXT("startup_title_text"),
            LOADING_SCREEN_GAMESTATE_VISIBILITY("loading_screen_gamestate_visibility"),
            LOADING_SCREEN_TEXT("loading_screen_text"),
            GAMESTATE_VISIBILITY("gamestate_visibility"),
            IS_FIRST_TIME_PRESSED("is_first_time_pressed");

            public final String ID;

            KeyData(String ID) {
                this.ID = ID;
            }
        }
        public enum SyntaxData {
            MC_VERSION("${mcversion}"),
            MODPACK_VERSION("${version}"),
            GAMESTATE("${gamestate}");

            public final String ID;

            SyntaxData(String ID) {
                this.ID = ID;
            }
        }

        public static String getTitleName() {
            String CLEAN_TITLE = MODPACK_TITLE.get();
            List<String> trashBin = new ArrayList<>();
            List<String> CONFIG_SYNTAX = new ArrayList<>();
            CONFIG_SYNTAX.add(SyntaxData.MC_VERSION.ID);
            CONFIG_SYNTAX.add(SyntaxData.MODPACK_VERSION.ID);
            CONFIG_SYNTAX.add(SyntaxData.GAMESTATE.ID);

            for (String value : CONFIG_SYNTAX) {
                String UNWANTED = getSyntaxIndex(MODPACK_TITLE.get(), MODPACK_TITLE.get(), value);
                trashBin.add(UNWANTED);
            }
            for (String del : trashBin) CLEAN_TITLE = CLEAN_TITLE.replace(del, "");
            return CLEAN_TITLE.trim();
        }
        public static String getSyntaxIndex(String cachedTitle, String title, String syntax) {
            StringBuilder cachedHolder = new StringBuilder();
            List<String> Dividers = Arrays.asList("-", "/", "|");
            int firstIndex = 0;
            int lastIndex = 0;
            int indexLocation = 0;
            boolean isLocationFound = false;
            if (cachedTitle.contains(syntax)) {
                firstIndex = cachedTitle.indexOf(syntax);
                lastIndex = firstIndex + syntax.length();
            }

            //FINDING THE INDEX OF SYNTAX VALUE IF THERE IS A DIVIDER
            for (int i = lastIndex-1; i >= 0; i--) {
                String x = String.valueOf(title.charAt(i));
                if (Dividers.contains(x)) {
                    isLocationFound = true;
                    indexLocation = i;
                    break;
                }
            }
            if (!isLocationFound) {
                //FINDING THE INDEX OF SYNTAX VALUE IF THERE ISN'T A DIVIDER
                for (int i = lastIndex-1; i >= 0; i--) {
                    char x = title.charAt(i);
                    if (x == ' ') {
                        isLocationFound = true;
                        indexLocation = i;
                        break;
                    }
                }
            }
            if (isLocationFound) {
                for (int i = 0; i < lastIndex; i++) {
                    if (i >= firstIndex-5 && i >= indexLocation) {
                        cachedHolder.append(title.charAt(i));
                    }
                }
            } else {
                return syntax;
            }
            return cachedHolder.toString();
        }
    }
}
