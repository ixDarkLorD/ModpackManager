package net.ixdarklord.packmger.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ixdarklord.packmger.client.renderer.ItemDurability;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class MixinGui extends GuiComponent {

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderAndDecorateItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void renderSlot(int posX, int posY, float perTick, Player player, ItemStack stack, int unknown, CallbackInfo info, PoseStack poseStack) {
        poseStack = new PoseStack();
        ItemDurability.render(poseStack, stack, posX, posY, null);
    }
}
