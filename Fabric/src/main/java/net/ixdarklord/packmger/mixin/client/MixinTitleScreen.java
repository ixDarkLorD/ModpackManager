package net.ixdarklord.packmger.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ixdarklord.packmger.client.button.VersionCheckerButton;
import net.ixdarklord.packmger.client.renderer.BrandingRenderer;
import net.ixdarklord.packmger.helper.Services;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
    @Shadow @Final private boolean fading;
    @Shadow private long fadeInStart;

    protected MixinTitleScreen(Component title) {
        super(title);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private String removeOldBranding(String string) {
        return "";
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderBranding(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        float f1 = fading ? (float)(Util.getMillis() - fadeInStart) / 1000.0F : 1.0F;
        float f2 = fading ? Mth.clamp(f1 - 1.0F, 0.0F, 1.0F) : 1.0F;
        int height = (Minecraft.getInstance().screen != null) ? Minecraft.getInstance().screen.height : 0;
        BrandingRenderer.forEachLine(true, (lineIndex, brd) -> {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, brd, 2, height - ( 10 + lineIndex * (Minecraft.getInstance().font.lineHeight + 1)), 16777215 | Mth.ceil(f2 * 255.0F) << 24);
        });
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void addCFUButton(CallbackInfo ci) {
        if (Minecraft.getInstance().screen == null) return;
        if (!Services.BUTTON.ifPresent(VersionCheckerButton.screenEvent, VersionCheckerButton.modButton)) {
            this.addRenderableWidget(VersionCheckerButton.modButton);
        }
    }
}
