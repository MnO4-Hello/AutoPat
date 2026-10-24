package com.agen.autopat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AutoPatMod.MODID)
public class AutoPatMod
{
    public static final String MODID = "autopat";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    // agen模组是否已加载
    public static boolean orderFlowLoaded = false;
    public static final String ORDERFLOW_MODID = "orderflow";

    public AutoPatMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        
        // 检查agen模组是否已加载
        orderFlowLoaded = ModList.get().isLoaded(ORDERFLOW_MODID);
        if (orderFlowLoaded) {
            LOGGER.info("[AutoPat] Detected AGen mod, running as addon");
        } else {
            LOGGER.info("[AutoPat] AGen mod not found, running standalone");
        }
        
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("[AutoPat] Mod Initialized");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("[AutoPat] Server starting");
    }
}
