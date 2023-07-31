package net.ixdarklord.packmger.client.renderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemDurability {
    public static boolean isDisplayShown = ConfigHandler.CLIENT.DURABILITY_DISPLAY_VISIBILITY.get();
    public static float SIZE = ConfigHandler.CLIENT.DURABILITY_DISPLAY_SIZE.get().floatValue();
    public static double CACHED_VALUE = ConfigHandler.CLIENT.DURABILITY_DISPLAY_SIZE.get();
    public static Slot getSlotUnderMouse;
    public static void render(PoseStack poseStack, ItemStack stack, int pX, int pY, String testString) {
        if (isDisplayShown && !stack.isEmpty() && stack.isDamaged() && !ConfigHandler.ItemBlacklist.isEntryExist(stack)) {
            String count;
            Font font = Minecraft.getInstance().font;
            int barColor = stack.getBarColor();
            if (testString == null || testString.isEmpty()) {count = getRemainingDurability(stack);} else {count = testString;}
            float strWidth = font.width(count);

            List<Float> posRefactor = new ArrayList<>();
            posRefactor.add(1.0f/SIZE == 1.0f ? 1.0f : 1.0f/SIZE);
            posRefactor.add(-8.0f + (5.0f - (-8.0f)) * (1.0f/SIZE));
            float posX = (pX + 8.0f) * posRefactor.get(0) + (1.0f + strWidth / 2.0f - strWidth);
            float posY = (pY * posRefactor.get(0)) + posRefactor.get(1);

            RenderSystem.disableDepthTest();

            poseStack.pushPose();
            poseStack.translate(0, 0, 390);
            poseStack.scale(SIZE, SIZE ,SIZE);
            MultiBufferSource.BufferSource multiBufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(count, posX, posY, barColor, true, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            multiBufferSource.endBatch();
            poseStack.popPose();
        }
    }
    private static String getRemainingDurability(ItemStack stack) {
        DecimalFormat dFormat = new DecimalFormat("0.##");
        int damage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        int enchantmentLevel = 1 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        float getRemaining = ((maxDamage - damage) * enchantmentLevel);

        if (getRemaining >= 1000000) return dFormat.format(getRemaining / 1000000) + "M";
        if (getRemaining >= 1000) return dFormat.format(getRemaining / 1000) + "K";
        return Float.toString(getRemaining).replaceAll("[.].?\\d*$", "");
    }

    public static class Serializer {
        private static class ItemBlacklist {
            List<ItemEntry> items;
            public ItemBlacklist(List<ItemEntry> items) {
                this.items = items;
            }
            public static class ItemEntry {
                String name;
                JsonElement nbt;
                public ItemEntry(String name, String nbt) {
                    this.name = name;
                    this.nbt = JsonParser.parseString(nbt);
                }
            }
        }
        private File JSONFile;
        private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private List<ItemBlacklist.ItemEntry> ItemsList = new ArrayList<>();

        public String serialize(List<ItemBlacklist.ItemEntry> list) {
            return GSON.toJson(new ItemBlacklist(list));
        }
        public ItemBlacklist deserialize(Reader jsonContext) {
            return new Gson().fromJson(jsonContext, ItemBlacklist.class);
        }

        public void registerConfig(String filePath) {
            JSONFile = new File(filePath);
            try {
                if (!JSONFile.exists()) { boolean ignored = JSONFile.createNewFile();}
                if (JSONFile.length() == 0) {
                    initializeFile();
                }
                readFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public void addEntry(@NotNull ItemStack stack) {
            String itemName = stack.getItem().toString();
            assert stack.getTag() != null;
            CompoundTag itemTag = new CompoundTag().merge(stack.getTag());
            itemTag.remove("Damage");
            ItemBlacklist.ItemEntry item = new ItemBlacklist.ItemEntry(itemName, itemTag.getAsString());
            if (ItemsList != null && !ItemsList.contains(item)) {
                ItemsList.add(item);
                updateFile(ItemsList);
            }
        }
        public void removeEntry(@NotNull ItemStack stack) {
            String itemName = stack.getItem().toString();
            assert stack.getTag() != null;
            CompoundTag itemTag = new CompoundTag().merge(stack.getTag());
            itemTag.remove("Damage");
            ItemBlacklist.ItemEntry item = new ItemBlacklist.ItemEntry(itemName, itemTag.getAsString());
            for (var entry : ItemsList) {
                if (entry.name.equals(item.name) && entry.nbt.equals(item.nbt)) {
                    ItemsList.remove(entry);
                    updateFile(ItemsList);
                    break;
                }
            }
        }
        public boolean isEntryExist(@NotNull ItemStack stack) {
            String itemName = stack.getItem().toString();
            assert stack.getTag() != null;
            CompoundTag itemTag = new CompoundTag().merge(stack.getTag());
            itemTag.remove("Damage");
            ItemBlacklist.ItemEntry item = new ItemBlacklist.ItemEntry(itemName, itemTag.getAsString());
            for (var entry : ItemsList) {
                if (entry.name.equals(item.name) && entry.nbt.equals(item.nbt)) {
                    return true;
                }
            }
            return false;
        }
        private void initializeFile() {
            try {
                JsonElement JSONString = JsonParser.parseString("{\"items\":[]}");
                BufferedWriter writer = new BufferedWriter(new FileWriter(JSONFile));
                writer.append(GSON.toJson(JSONString));
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        private void updateFile(List<ItemBlacklist.ItemEntry> itemsList) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(JSONFile));
                writer.append(serialize(itemsList));
                writer.flush();
                writer.close();
                readFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        private void readFile() {
            try {
                Reader reader = Files.newBufferedReader(Paths.get(JSONFile.toURI()));
                ItemBlacklist itemBlacklist = deserialize(reader);
                reader.close();
                if (itemBlacklist.items != null) ItemsList = itemBlacklist.items;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
