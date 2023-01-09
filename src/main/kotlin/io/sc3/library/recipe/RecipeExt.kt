package io.sc3.library.recipe

import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.recipe.*
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.function.Consumer

fun <T : CraftingRecipe> specialRecipe(
  exporter: Consumer<RecipeJsonProvider>,
  serializer: SpecialRecipeSerializer<T>
): Identifier {
  val recipeId = Registries.RECIPE_SERIALIZER.getId(serializer)
    ?: throw IllegalStateException("Recipe serializer $serializer is not registered")
  ComplexRecipeJsonBuilder.create(serializer).offerTo(exporter, recipeId.toString())
  return recipeId
}

/**
 * Save this recipe, overriding the recipe serialiser with a custom implementation.
 *
 * This is useful for recipes which override [ShapedRecipe] or [ShapelessRecipe].
 */
fun CraftingRecipeJsonBuilder.offerTo(
  export: Consumer<RecipeJsonProvider>,
  serializer: RecipeSerializer<*>,
  id: Identifier? = null
) {
  offerTo({
    export.accept(object : RecipeJsonProvider by it {
      override fun getSerializer(): RecipeSerializer<*> = serializer
    })
  }, id ?: CraftingRecipeJsonBuilder.getItemId(outputItem))
}
