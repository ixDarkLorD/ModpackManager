package net.ixdarklord.packmger.event;

import net.ixdarklord.packmger.client.button.VersionCheckerButton;
import net.ixdarklord.packmger.client.handler.KeyHandler;
import net.ixdarklord.packmger.client.handler.ScreenHandler;
import net.ixdarklord.packmger.client.handler.WindowHandler;
import net.ixdarklord.packmger.compat.ModCompatibility;
import net.ixdarklord.packmger.config.ConfigHandler;
import net.ixdarklord.packmger.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent evt) {
            KeyHandler.registerKeys(evt);
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent evt) {
            if (evt.phase == TickEvent.Phase.END) return;

            if (Minecraft.getInstance().screen instanceof TitleScreen) {
                VersionCheckerButton.checkInternetConnectivity();
            }
        }
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key evt) {
            KeyEvents.registerEvents(null);
        }
    }
}
