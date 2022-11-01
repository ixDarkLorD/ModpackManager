package net.ixdarklord.packmger.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ForgeSetup {
    public ForgeSetup() {
        CommonSetup.init();
    }
}