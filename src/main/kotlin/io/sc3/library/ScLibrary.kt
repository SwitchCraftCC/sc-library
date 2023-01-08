package io.sc3.library

import io.sc3.library.recipe.IngredientBrew
import io.sc3.library.recipe.IngredientEnchanted
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ScLibrary : ModInitializer {
  val log = LoggerFactory.getLogger("ScLibrary")!!

  val modId = "sc-library"
  fun ModId(value: String) = Identifier(modId, value)

  override fun onInitialize() {
    CustomIngredientSerializer.register(IngredientBrew.Serializer)
    CustomIngredientSerializer.register(IngredientEnchanted.Serializer)
  }
}
