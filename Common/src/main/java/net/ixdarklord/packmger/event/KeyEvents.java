package net.ixdarklord.packmger.event;

import net.ixdarklord.packmger.client.handler.KeyHandler;
import net.ixdarklord.packmger.client.renderer.ItemDurability;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeyEvents {
    public static void registerEvents(Object instance) {
        if (ConfigHandler.CLIENT.DURABILITY_DISPLAY_VISIBILITY.get()) {
            toggleDurabilityDisplay.event(KeyHandler.KEY_TOGGLE_DURABILITY);
            if (ItemDurability.isDisplayShown) {
                durabilityDisplaySize.event(KeyHandler.KEY_DECREASE_DURABILITY_DISPLAY, KeyHandler.KEY_INCREASE_DURABILITY_DISPLAY);
                hideDurability.event(KeyHandler.KEY_HIDE_DURABILITY);
            }
        }
    }

    public static class toggleDurabilityDisplay {
        private static boolean isKeyPressed;
        private static void event(KeyMapping keyMapping) {
            if (keyMapping.isDown()) {
                if (!isKeyPressed) {
                    String MSG = "\u00A77";
                    if (ItemDurability.isDisplayShown) {
                        MSG = MSG + I18n.get("action.packmger.toggle_durability_off", "%s")
                                .replace("%s", "\u00A7c");
                    } else {
                        MSG = MSG + I18n.get("action.packmger.toggle_durability_on", "%s")
                                .replace("%s", "\u00A7a");
                    }
                    assert Minecraft.getInstance().player != null : "Player is null";
                    Minecraft.getInstance().player.displayClientMessage(new TextComponent(MSG), true);
                    ItemDurability.isDisplayShown ^= true;
                }
                isKeyPressed = true;
            } else {
                isKeyPressed = false;
            }
        }
    }
    public static class durabilityDisplaySize {
        private static void event(KeyMapping key1, KeyMapping key2) {
            String MSG = "\u00A77";
            float SIZE = ItemDurability.SIZE;
            float NEW_SIZE = SIZE;
            if (key1.isDown() && NEW_SIZE > 0.0f) NEW_SIZE = Math.max(0.0f, NEW_SIZE - 0.01f);
            if (key2.isDown() && NEW_SIZE < 1.0f) NEW_SIZE = Math.min(1.0f, NEW_SIZE + 0.01f);
            if (NEW_SIZE != SIZE) {
                float SIZE_METER = (0 + 100 * NEW_SIZE);
                String TEXT_COLOR = (SIZE_METER == 0.0f) ? "\u00A77" : (SIZE_METER <= 33.33f) ? "\u00A7c" : (SIZE_METER <= 66.65f) ? "\u00A7e" : (SIZE_METER < 100.0f) ? "\u00A7a" : "\u00A7d";

                if (NEW_SIZE < SIZE) MSG = MSG + I18n.get("action.packmger.durability_display_size", "%s")
                        .replace("%s", TEXT_COLOR + (int)SIZE_METER + "↓");
                if (NEW_SIZE > SIZE) MSG = MSG + I18n.get("action.packmger.durability_display_size", "%s")
                        .replace("%s", TEXT_COLOR + (int)SIZE_METER + "↑");

                ItemDurability.SIZE = NEW_SIZE;
                ItemDurability.CACHED_VALUE = Math.min(Math.max(ItemDurability.SIZE, 0.0d), 1.0d);
                assert Minecraft.getInstance().player != null : "Player is null";
                Minecraft.getInstance().player.displayClientMessage(new TextComponent(MSG), true);
            }
        }
    }
    private static class hideDurability {
        private static boolean isKeyPressed;
        private static void event(KeyMapping keyMapping) {
            if (keyMapping.isDown()) {
                if (!isKeyPressed) {
                    ItemStack stack = null;
                    Minecraft MC = Minecraft.getInstance();
                    if (MC.screen instanceof AbstractContainerScreen<?>) {

                        Slot slot = ItemDurability.getSlotUnderMouse;
                        if (slot != null && slot.hasItem() && slot.getItem().isDamageableItem()) {
                            stack = slot.getItem();
                        }
                    } else {
                        Inventory inv = (MC.player != null) ? MC.player.getInventory() : null;
                        if (inv != null && !inv.getSelected().isEmpty() && inv.getSelected().isDamageableItem()) {
                            stack = MC.player.getInventory().getSelected();
                        }
                        else if (inv != null && !inv.offhand.get(0).isEmpty() && inv.offhand.get(0).isDamageableItem()) {
                            stack = MC.player.getInventory().offhand.get(0);
                        }
                    }
                    if (stack != null) {
                        String MSG = "\u00A77";
                        if (ConfigHandler.ItemBlacklist.isEntryExist(stack)) {
                            ConfigHandler.ItemBlacklist.removeEntry(stack);
                            MSG = MSG + I18n.get("action.packmger.remove_item_blacklist", "%s")
                                    .replace("%s", "\u00A7a" + stack.getDisplayName().getString());
                        }
                        else {
                            ConfigHandler.ItemBlacklist.addEntry(stack);
                            MSG = MSG + I18n.get("action.packmger.add_item_blacklist", "%s")
                                    .replace("%s", "\u00A7c" + stack.getDisplayName().getString());
                        }
                        assert MC.player != null : "Player is null";
                        MC.player.displayClientMessage(new TextComponent(MSG), true);
                    }
                }
                isKeyPressed = true;
            } else {
                isKeyPressed = false;
            }
        }
    }
}
