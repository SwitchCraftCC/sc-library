package pw.switchcraft.library.recipe

import com.google.gson.JsonObject
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.CriterionMerger
import net.minecraft.advancement.criterion.CriterionConditions
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory.MISC
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.function.Consumer

class BetterComplexRecipeJsonBuilder<T : CraftingRecipe>(
  output: ItemConvertible,
  private val specialSerializer: SpecialRecipeSerializer<T>
) {
  private val outputItem = output.asItem()
  private val advancementBuilder: Advancement.Builder = Advancement.Builder.create()

  fun criterion(name: String, conditions: CriterionConditions) = apply {
    advancementBuilder.criterion(name, conditions)
  }

  fun offerTo(exporter: Consumer<RecipeJsonProvider>, recipeId: Identifier = itemId(outputItem)) {
    val advancementId = recipeId.withPrefixedPath("recipes/" + MISC.asString() + "/")
    val advancement = advancementBuilder
      .parent(Identifier("recipes/root")) // TODO: PR a name to yarn for field_39377
      .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
      .rewards(AdvancementRewards.Builder.recipe(recipeId))
      .criteriaMerger(CriterionMerger.OR)
      .toJson()

    exporter.accept(object : RecipeJsonProvider {
      override fun serialize(json: JsonObject) {} // No-op
      override fun getRecipeId() = recipeId
      override fun getSerializer() = specialSerializer
      override fun toAdvancementJson() = advancement
      override fun getAdvancementId() = advancementId
    })
  }

  companion object {
    private fun itemId(item: ItemConvertible) = Registries.ITEM.getId(item.asItem())
  }
}
