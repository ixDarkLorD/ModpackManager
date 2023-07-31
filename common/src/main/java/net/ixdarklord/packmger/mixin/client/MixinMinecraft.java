package net.ixdarklord.packmger.mixin.client;

import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.helper.Services;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(at = @At(value = "HEAD"), method = "createTitle", cancellable = true)
    private void onGetWindowTitle(CallbackInfoReturnable<String> info) {
        if (!Services.PLATFORM.isConfigLoaded()) return;
        if (ConfigHandler.CLIENT.WINDOW_TITLE_CHANGER.get()) {
            info.setReturnValue(WindowHandler.modifyTitle());
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "stop")
    private void GameShuttingDownEvent(CallbackInfo ci) {
        ConfigHandler.saveData();
    }
}
