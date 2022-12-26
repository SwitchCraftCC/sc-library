package io.sc3.library.recipe

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntComparators
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeMatcher
import java.util.stream.Stream

abstract class BaseIngredient(
  entries: Stream<out Entry> = Stream.empty()
) : Ingredient(entries) {
  private val packed by lazy {
    val packed = IntArrayList()
    for (stack in matchingStacks) packed.add(RecipeMatcher.getItemId(stack))
    packed.sort(IntComparators.NATURAL_COMPARATOR)
    packed
  }

  override fun getMatchingItemIds() = packed
}
