package net.ixdarklord.packmger.event;

import net.ixdarklord.packmger.client.handler.ScreenHandler;
import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientForgeModBusEvents {
        @SubscribeEvent
        public static void onConstructMod(final FMLConstructModEvent evt) {
            ConfigHandler.initializeFiles();
            final ScreenHandler handler = new ScreenHandler();
            MinecraftForge.EVENT_BUS.addListener(handler::onScreenInitPost);
        }

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent evt) {
            ModCompatibility.registerClient();
            Minecraft.getInstance().getWindow().setTitle(WindowHandler.modifyTitle());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent evt) {
            KeyEvents.registerEvents(null);
        }
    }
}
