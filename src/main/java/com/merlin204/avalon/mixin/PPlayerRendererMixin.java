package com.merlin204.avalon.mixin;


import com.merlin204.avalon.entity.client.renderer.RenderChangeMeshItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(value = PPlayerRenderer.class)
public class PPlayerRendererMixin {

    /**
     * 取自EFMM by P1nero
     */

    @Inject(method = "getMeshProvider(Lyesman/epicfight/client/world/capabilites/entitypatch/player/AbstractClientPlayerPatch;)Lyesman/epicfight/api/asset/AssetAccessor;", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$replaceMesh(AbstractClientPlayerPatch<AbstractClientPlayer> playerPatch, CallbackInfoReturnable<AssetAccessor<HumanoidMesh>> cir){
        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (renderItemBase instanceof RenderChangeMeshItem renderChangeMeshItem){
            if (renderChangeMeshItem.mesh != null){
                cir.setReturnValue(renderChangeMeshItem.mesh);
            }
        }
    }

    @Inject(method = "prepareModel(Lyesman/epicfight/client/mesh/HumanoidMesh;Lnet/minecraft/client/player/AbstractClientPlayer;Lyesman/epicfight/client/world/capabilites/entitypatch/player/AbstractClientPlayerPatch;Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$prepareModel(HumanoidMesh mesh, AbstractClientPlayer player, AbstractClientPlayerPatch<AbstractClientPlayer> playerPatch, PlayerRenderer renderer, CallbackInfo ci){
        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (renderItemBase instanceof RenderChangeMeshItem) {
            mesh.initialize();
            renderer.setModelProperties(player);
            ci.cancel();
        }
    }

}
