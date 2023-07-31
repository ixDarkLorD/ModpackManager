package net.ixdarklord.packmger.mixin.client;

import net.ixdarklord.packmger.client.renderer.ItemDurability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen extends Screen {
    @Shadow @Nullable protected Slot hoveredSlot;

    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void renderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci, int i, int j, ItemStack itemStack, boolean bl, boolean bl2, ItemStack itemStack2, String string) {
        ItemDurability.getSlotUnderMouse = hoveredSlot;
        ItemDurability.render(guiGraphics.pose(), slot.getItem(), slot.x, slot.y, null);
    }
}