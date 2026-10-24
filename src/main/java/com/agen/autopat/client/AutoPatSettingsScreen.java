package com.agen.autopat.client;

import com.agen.autopat.AutoPatMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AutoPatSettingsScreen extends Screen {
    private final Screen parent;
    private boolean patPlayersEnabled = false;
    private boolean patAllEntitiesEnabled = false;
    private EditBox intervalBox;

    public AutoPatSettingsScreen(Screen parent) {
        super(Component.literal("AutoPat Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // 先从配置加载当前状态
        patPlayersEnabled = AutoPatClientConfig.isPatPlayersEnabled();
        patAllEntitiesEnabled = AutoPatClientConfig.isPatAllEntitiesEnabled();

        // 摸头玩家按钮
        addRenderableWidget(Button.builder(
                Component.literal("摸头玩家: " + (patPlayersEnabled ? "开启" : "关闭")),
                button -> {
                    patPlayersEnabled = !patPlayersEnabled;
                    button.setMessage(Component.literal("摸头玩家: " + (patPlayersEnabled ? "开启" : "关闭")));
                    AutoPatClientConfig.setPatPlayersEnabled(patPlayersEnabled);
                })
                .pos(this.width / 2 - 100, this.height / 2 - 40)
                .size(200, 20)
                .build());

        // 全部摸头按钮
        addRenderableWidget(Button.builder(
                Component.literal("全部摸头: " + (patAllEntitiesEnabled ? "开启" : "关闭")),
                button -> {
                    patAllEntitiesEnabled = !patAllEntitiesEnabled;
                    button.setMessage(Component.literal("全部摸头: " + (patAllEntitiesEnabled ? "开启" : "关闭")));
                    AutoPatClientConfig.setPatAllEntitiesEnabled(patAllEntitiesEnabled);
                })
                .pos(this.width / 2 - 100, this.height / 2)
                .size(200, 20)
                .build());

        // 摸头间隔输入框标签
        addRenderableWidget(new net.minecraft.client.gui.components.StringWidget(
            this.width / 2 - 100, this.height / 2 + 30, 200, 20,
            Component.literal("摸头间隔（刻）:"), this.font));

        // 摸头间隔输入框
        intervalBox = new EditBox(this.font, this.width / 2 - 50, this.height / 2 + 50, 100, 20, Component.literal("Interval"));
        intervalBox.setValue(String.valueOf(AutoPatClientConfig.getPatInterval()));
        intervalBox.setFilter(s -> s.matches("\\d*")); // 只允许数字
        intervalBox.setMaxLength(3); // 最多3位数
        addRenderableWidget(intervalBox);

        // 返回按钮
        addRenderableWidget(Button.builder(
                Component.literal("返回"),
                button -> onClose())
                .pos(this.width / 2 - 100, this.height / 2 + 80)
                .size(200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xFFFFFF);
        
        // 如果PatPat未加载，显示警告信息
        if (!ClientEventHandler.isPatPatLoaded()) {
            String warning = "\u00a7c\u26a0 PatPat\u6a21\u7ec4\u672a\u52a0\u8f7d\uff0c\u6478\u5934\u529f\u80fd\u65e0\u6cd5\u4f7f\u7528";
            guiGraphics.drawCenteredString(this.font, warning, this.width / 2, 70, 0xFF5555);
            String hint = "\u00a77\u8bf7\u786e\u4fdd\u5df2\u5b89\u88c5PatPat\u6a21\u7ec4\u5e76\u901a\u8fc7\u4fe1\u96c5\u4e92\u8054\u52a0\u8f7d";
            guiGraphics.drawCenteredString(this.font, hint, this.width / 2, 85, 0xAAAAAA);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        // 实时更新间隔配置
        if (intervalBox != null && !intervalBox.getValue().isEmpty()) {
            try {
                int interval = Integer.parseInt(intervalBox.getValue());
                AutoPatClientConfig.setPatInterval(interval);
            } catch (NumberFormatException e) {
                // 忽略无效输入
            }
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
