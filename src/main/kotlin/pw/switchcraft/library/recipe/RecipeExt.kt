package pw.switchcraft.library.recipe

import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Consumer

fun <T : Recipe<C>, C : Inventory> specialRecipe(
  exporter: Consumer<RecipeJsonProvider>,
  serializer: SpecialRecipeSerializer<T>
): Identifier {
  val recipeId = Registry.RECIPE_SERIALIZER.getId(serializer)
    ?: throw IllegalStateException("Recipe serializer $serializer is not registered")
  ComplexRecipeJsonBuilder.create(serializer).offerTo(exporter, recipeId.toString())
  return recipeId
}
