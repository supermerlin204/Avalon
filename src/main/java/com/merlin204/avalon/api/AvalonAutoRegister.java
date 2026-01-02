package com.merlin204.avalon.api;

import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注册实体的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvalonAutoRegister {
    String value();

    Class<? extends EntityPatch> entityPatch() default LivingEntityPatch.class;
    String clientRenderer() default "com.merlin204.avalon.entity.client.renderer.EmptyRenderer";
    String renderPatch() default "com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch";

    // 是否注册属性，使用约定好的方法名"getDefaultAttributes"
    boolean registerAttributes() default false;
}
