package com.agen.autopat.client;

import com.agen.autopat.AutoPatMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 用于与 OrderFlow 模组集成的工具类
 * 当 OrderFlow 模组存在时，把 AutoPat 的设置挂到 OrderFlow 的设置界面里
 */
public class OrderFlowIntegration {
    private static boolean integrationInitialized = false;
    private static Class<?> modSettingsScreenClass = null;
    
    /**
     * 初始化集成
     */
    public static void initIntegration() {
        if (integrationInitialized || !AutoPatMod.orderFlowLoaded) {
            return;
        }
        
        try {
            // 尝试加载 OrderFlow 的 ModSettingsScreen 类
            modSettingsScreenClass = Class.forName("com.agen.orderflow.ModSettingsScreen");
            integrationInitialized = true;
            AutoPatMod.LOGGER.info("[AutoPat] Successfully initialized integration with OrderFlow mod");
        } catch (ClassNotFoundException e) {
            AutoPatMod.LOGGER.warn("[AutoPat] Could not find OrderFlow ModSettingsScreen: {}", e.getMessage());
        }
    }
    
    /**
     * 检查是否已集成
     */
    public static boolean isIntegrated() {
        return integrationInitialized && AutoPatMod.orderFlowLoaded;
    }
    
    /**
     * 获取 AutoPat 设置界面（独立模式）
     */
    public static Screen getStandaloneSettingsScreen(Screen parent) {
        return new AutoPatSettingsScreen(parent);
    }
}
