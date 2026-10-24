# AutoPat · 自动摸头

> 按可配置的间隔，自动对周围的玩家（或所有生物）执行「摸头」动作。客户端模组，依赖 PatPat。

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-informational)
![Forge](https://img.shields.io/badge/Forge-47.3.33-informational)
![Side](https://img.shields.io/badge/side-client-blue)

---

## 简介

AutoPat 是一个纯客户端的装饰性模组：开启后每隔固定刻数扫描一次周围实体，并对目标执行一次「摸头」。
动作通过 PatPat 模组的客户端数据包发送，因此**其他玩家也能看到你摸头的动画**，和真实手动摸头一致。

模组本身不需要服务端安装，但需要本地安装 PatPat 才能产生实际动作；没装 PatPat 时模组会正常加载、只是不产生效果（日志会提示未找到 PatPat）。

## 功能

| 功能 | 说明 |
| --- | --- |
| 摸头玩家 | 对已加载区块内的所有其他玩家自动摸头 |
| 全部摸头 | 对半径 128 格内的所有生物（`LivingEntity`）自动摸头，排除自己 |
| 可调间隔 | 1 ~ 200 tick（默认 20 tick = 1 秒），间隔越长越不显眼 |
| 游戏内设置界面 | 按 `Y` 打开，两个开关 + 间隔输入框，改完立即生效 |
| 与 OrderFlow 联动 | 同时安装 [OrderFlow](https://github.com/MnO4-Hello/OrderFlow) 时，会自动把设置入口挂到 OrderFlow 的设置界面；否则按 `Y` 打开独立界面 |

## 环境要求

- Minecraft **1.20.1**
- Minecraft Forge **47.3.33**（47.x 均可）
- Java **17**
- 强烈建议安装 PatPat；不装则无实际动画

## 安装

1. 安装对应版本的 Forge。
2. 把 `autopat-1.0.0.jar` 放进 `.minecraft/mods/`。
3. 启动游戏，模组列表中出现 **AutoPat** 即为加载成功。

## 使用

1. 进入世界，按 `Y`（可在「选项 → 控制 → 按键绑定」中修改）打开设置界面。
2. 打开「摸头玩家」或「全部摸头」。
3. 设置扫描间隔（tick），关闭界面即开始按间隔执行。
4. 想停下时把两个开关都关掉即可。

## 实现说明

- 扫描在 `ClientTickEvent` 的 `END` 阶段执行，用一个 tick 计数器累加到设定间隔才触发一次。
- 摸头动作为反射调用 PatPat 的客户端渲染器：

  ```java
  PlayerConfig cfg = PlayerConfig.of(playerName, uuid);
  PacketPat packet = new PacketPat(target, cfg, localPlayer, false);
  PatPatClientRenderer.registerClientPacket(packet); // 下一 tick 发往服务端
  ```

- 使用反射而不是编译期依赖，是为了在没装 PatPat 时不产生硬依赖、也不影响模组加载。

## 构建

```bash
# 需要 JDK 17
./gradlew build
# 产物：build/libs/autopat-1.0.0.jar
```

## 目录结构

```
src/main/java/com/agen/autopat/
├── AutoPatMod.java                 # 模组入口，检测 OrderFlow 是否存在
└── client/
    ├── AutoPatClientConfig.java    # 运行期配置（开关 / 间隔）
    ├── AutoPatSettingsScreen.java  # 独立设置界面
    ├── ClientEventHandler.java     # tick 扫描 + 按键 + PatPat 反射调用
    └── OrderFlowIntegration.java   # 与 OrderFlow 的设置界面集成
```

## 注意

自动摸头属于自动化行为，部分服务器可能视为刷屏或骚扰。请自行确认目标服务器的规则后再使用，作者不对使用该模组导致的封禁负责。

## 许可

All Rights Reserved（保留所有权利）。如需二次分发或用于整合包，请先联系作者。
