package com.agen.autopat.client;

import com.agen.autopat.AutoPatMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;
import java.util.List;

@Mod.EventBusSubscriber(modid = AutoPatMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    // 独立模式下的Y键
    public static final KeyMapping SETTINGS_KEY = new KeyMapping(
            "key.autopat.settings",
            GLFW.GLFW_KEY_Y,
            "category.autopat"
    );

    private static int tickCounter = 0;
    private static Class<?> patPatClientManagerClass = null;
    private static Method patMethod = null;
    private static Class<?> playerConfigClass = null;
    private static Method playerConfigOfMethod = null;
    private static Class<?> patPatClientRendererClass = null;
    private static Method registerClientPacketMethod = null;
    private static Class<?> packetPatClass = null;
    private static boolean patPatLoaded = false;
    private static boolean triedLoadPatPat = false;
    
    // PacketPat构造函数
    private static java.lang.reflect.Constructor<?> packetPatConstructor = null;

    /**
     * 检查PatPat是否已成功加载
     */
    public static boolean isPatPatLoaded() {
        return patPatLoaded;
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // 始终注册Y键
        event.register(SETTINGS_KEY);
        
        // 如果agen已加载，初始化集成
        if (AutoPatMod.orderFlowLoaded) {
            OrderFlowIntegration.initIntegration();
        }
    }

    /**
     * 尝试加载PatPat模组的相关类和方法
     */
    private static void tryLoadPatPat() {
        if (triedLoadPatPat) return;
        triedLoadPatPat = true;
        
        try {
            // 加载PatPatClientManager类和pat方法
            // 注意：在信雅互联环境下，需要使用Forge的类名而不是Fabric的混淆类名
            patPatClientManagerClass = Class.forName("net.lopymine.patpat.client.manager.PatPatClientManager");
            
            // 使用Forge的LivingEntity类名
            Class<?> livingEntityClass = Class.forName("net.minecraft.world.entity.LivingEntity");
            Class<?> playerConfigClassTemp = Class.forName("net.lopymine.patpat.client.config.resourcepack.PlayerConfig");
            
            patMethod = patPatClientManagerClass.getMethod("pat", livingEntityClass, playerConfigClassTemp);
            
            // 加载PlayerConfig类和of方法
            playerConfigClass = playerConfigClassTemp;
            playerConfigOfMethod = playerConfigClass.getMethod("of", String.class, java.util.UUID.class);
            
            // 加载PatPatClientRenderer类和registerClientPacket方法
            patPatClientRendererClass = Class.forName("net.lopymine.patpat.client.render.PatPatClientRenderer");
            registerClientPacketMethod = patPatClientRendererClass.getMethod("registerClientPacket", 
                Class.forName("net.lopymine.patpat.client.render.PatPatClientRenderer$PacketPat"));
            
            // 加载PacketPat类和构造函数
            packetPatClass = Class.forName("net.lopymine.patpat.client.render.PatPatClientRenderer$PacketPat");
            // PacketPat(LivingEntity, PlayerConfig, LocalPlayer, boolean)
            packetPatConstructor = packetPatClass.getConstructor(
                Class.forName("net.minecraft.world.entity.LivingEntity"),
                playerConfigClassTemp,
                Class.forName("net.minecraft.client.player.LocalPlayer"),
                boolean.class
            );
            
            patPatLoaded = true;
            AutoPatMod.LOGGER.info("[AutoPat] Successfully loaded PatPat mod classes for direct API call");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            patPatLoaded = false;
            AutoPatMod.LOGGER.error("[AutoPat] PatPat mod not found or incompatible: {}", e.getMessage());
            AutoPatMod.LOGGER.error("[AutoPat] Stack trace: ", e);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) return;

        // 首次运行时尝试加载PatPat
        if (!triedLoadPatPat) {
            tryLoadPatPat();
        }

        // 使用配置的间隔执行扫描
        int patInterval = AutoPatClientConfig.getPatInterval();
        tickCounter++;
        if (tickCounter >= patInterval) {
            tickCounter = 0;

            // 摸头玩家功能
            if (AutoPatClientConfig.isPatPlayersEnabled()) {
                patPlayers(level, player, minecraft);
            }

            // 全部摸头功能
            if (AutoPatClientConfig.isPatAllEntitiesEnabled()) {
                patAllEntities(level, player, minecraft);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // 始终处理Y键
        if (SETTINGS_KEY.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new AutoPatSettingsScreen(minecraft.screen));
        }
    }

    /**
     * 摸头玩家 - 扫描已加载区块内的所有玩家
     */
    private static void patPlayers(Level level, LocalPlayer selfPlayer, Minecraft minecraft) {
        List<? extends Player> players = level.players();
        for (Player player : players) {
            if (player != selfPlayer) {
                // 尝试对玩家进行摸头
                performPatOnEntity(player, minecraft);
            }
        }
    }

    /**
     * 全部摸头 - 扫描已加载区块内的所有实体
     */
    private static void patAllEntities(Level level, LocalPlayer selfPlayer, Minecraft minecraft) {
        // 获取所有已加载的实体 - 使用getEntities()方法
        for (Entity entity : level.getEntities(selfPlayer, selfPlayer.getBoundingBox().inflate(128))) {
            if (entity instanceof LivingEntity livingEntity && entity != selfPlayer) {
                // 尝试对实体进行摸头
                performPatOnEntity(livingEntity, minecraft);
            }
        }
    }

    /**
     * 对指定实体执行摸头动作
     * 通过registerClientPacket发送网络包，让所有客户端都能看到摸头效果
     */
    private static void performPatOnEntity(LivingEntity target, Minecraft minecraft) {
        if (!patPatLoaded || registerClientPacketMethod == null || playerConfigOfMethod == null || packetPatConstructor == null) {
            return;
        }

        try {
            LocalPlayer player = minecraft.player;
            if (player == null) return;

            // 创建PlayerConfig对象
            Object playerConfig = playerConfigOfMethod.invoke(null, player.getName().getString(), player.getUUID());
            
            // 创建PacketPat对象: new PacketPat(entity, playerConfig, player, false)
            Object packetPat = packetPatConstructor.newInstance(target, playerConfig, player, false);
            
            // 调用PatPatClientRenderer.registerClientPacket(packetPat)
            // 这会将数据包加入队列，在下一个tick发送到服务器
            registerClientPacketMethod.invoke(null, packetPat);
            
            AutoPatMod.LOGGER.debug("[AutoPat] Registered pat packet for entity: {}", target.getName().getString());

        } catch (Exception e) {
            AutoPatMod.LOGGER.error("[AutoPat] Failed to register pat packet for entity: {}", e.getMessage());
            AutoPatMod.LOGGER.error("[AutoPat] Stack trace: ", e);
        }
    }
}
