package io.sc3.library.mixin;

import io.sc3.library.ScLibrary;
import io.sc3.library.ext.ItemFrameEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRendererMixin {
  @Inject(
    method = "render(Lnet/minecraft/entity/decoration/ItemFrameEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V",
      ordinal = 2,
      shift = At.Shift.AFTER
    ),
    cancellable = true
  )
  private void render(ItemFrameEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                      VertexConsumerProvider consumers, int light, CallbackInfo ci) {
    try {
      ItemStack stack = entity.getHeldItemStack();
      if (stack != null && ItemFrameEvents.ITEM_RENDER.invoker().invoke(entity, stack, matrices, consumers, light)) {
        ci.cancel();
        matrices.pop();
      }
    } catch (Exception e) {
      ScLibrary.INSTANCE.getLog().error("Error in ItemFrameEvents.ITEM_RENDER", e);
    }
  }

  @Inject(
    method = "getModelId",
    at = @At("HEAD"),
    cancellable = true
  )
  private void getModelId(ItemFrameEntity entity, ItemStack stack, CallbackInfoReturnable<ModelIdentifier> cir) {
    try {
      if (stack != null) {
        ModelIdentifier model = ItemFrameEvents.FRAME_MODEL_ID.invoker().invoke(entity, stack);
        if (model != null) cir.setReturnValue(model);
      }
    } catch (Exception e) {
      ScLibrary.INSTANCE.getLog().error("Error in ItemFrameEvents.FRAME_MODEL_ID", e);
    }
  }
}
