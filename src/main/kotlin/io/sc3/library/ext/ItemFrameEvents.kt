package io.sc3.library.ext

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemStack

object ItemFrameEvents {
  @JvmField
  val ITEM_RENDER = event<(frame: ItemFrameEntity, stack: ItemStack, matrices: MatrixStack,
                           consumers: VertexConsumerProvider, light: Int) -> Boolean> { cb ->
    { frame, stack, matrices, consumers, light -> cb.all {
      it(frame, stack, matrices, consumers, light)
    } }
  }

  @JvmField
  val FRAME_MODEL_ID = event<(frame: ItemFrameEntity, stack: ItemStack) -> ModelIdentifier?> { cb ->
    { frame, stack -> cb.firstNotNullOfOrNull { it(frame, stack) } }
  }
}
