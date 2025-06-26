
# 🔥 Avalon EpicFight Addon API

_✨ 专为Epic Fight模组打造的轻量级动画扩展框架 | 简化附属开发流程，让动画注册变得前所未有的优雅_

---

## 🚀 功能特性

- **流畅的动画注册API** - 告别冗长繁琐的配置
- **优化后的动画类** - 提供`AvalonAttackAnimation`/`AvalonMovementAnimation`等即用动画类
- **多种动画常用方法** - 在`AvalonAnimationUtils`/`AvalonEventUtils`中

例如：
TEST = builder.nextAccessor("test", accessor -> new AvalonAttackAnimation(0.15F, accessor, armature,play_speed,damage modifier,
        //添加一个简单的Phase，其中的三个int指帧数
                AvalonAnimationUtils.createSimplePhase(35,40,55),
        //添加另一个简单的Phase
                AvalonAnimationUtils.createSimplePhase(35,40,55))
        //添加一个简单的GroundSplit事件，第一个int为发生时间
                .addEvents(AvalonEventUtils.simpleGroundSplit(39,2.5,0,0,0,4,true)));


---

✨ A lightweight animation extension framework designed for the Epic Fight mod | Streamlines addon development, making animation registration unprecedentedly elegant

---

## 🚀 Features

- **Smooth Animation Registration API** - Bid farewell to cumbersome configurations
- **Optimized Animation Classes** - Ready-to-use animation classes like `AvalonAttackAnimation`/`AvalonMovementAnimation`
- **Common Animation Utilities** - Available in `AvalonAnimationUtils`/`AvalonEventUtils`

Example:
TEST = builder.nextAccessor("test", accessor -> new AvalonAttackAnimation(0.15F, accessor, armature, play_speed, damage_modifier,
        // Add a simple Phase with three integers indicating frame indices
                AvalonAnimationUtils.createSimplePhase(35, 40, 55),
        // Add another simple Phase
                AvalonAnimationUtils.createSimplePhase(35, 40, 55))
        // Add a simple GroundSplit event, first int is trigger frame
                .addEvents(AvalonEventUtils.simpleGroundSplit(39, 2.5, 0, 0, 0, 4, true)));