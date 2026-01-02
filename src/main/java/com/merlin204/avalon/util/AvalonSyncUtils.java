package com.merlin204.avalon.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

public class AvalonSyncUtils {

    /**
     * 将 ItemStack 转换为 CompoundTag
     * @param stack 要转换的物品堆栈
     * @return 包含物品堆栈数据的 CompoundTag
     */
    public static CompoundTag saveItemStack(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        // 使用 saveOptional 方法保存 ItemStack
        return (CompoundTag) ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, stack)
                .result()
                .orElse(tag);
    }

    /**
     * 从 CompoundTag 还原 ItemStack
     * @param tag 包含物品堆栈数据的 CompoundTag
     * @return 还原的 ItemStack
     */
    public static ItemStack loadItemStack(CompoundTag tag) {
        // 使用 parseOptional 方法解析 ItemStack
        return ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag)
                .result()
                .orElse(ItemStack.EMPTY);
    }
}
