package pw.switchcraft.library.recipe

import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
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
