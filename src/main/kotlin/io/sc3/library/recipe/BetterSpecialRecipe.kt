package io.sc3.library.recipe

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

abstract class BetterSpecialRecipe(
  id: Identifier,
  category: CraftingRecipeCategory
) : SpecialCraftingRecipe(id, category) {
  protected open val width: Int = 3
  protected open val height: Int = 3

  protected abstract val outputItem: ItemStack

  protected abstract val ingredients: List<Ingredient>
  protected open val ingredientPredicates: List<(it: Ingredient, stack: ItemStack) -> Boolean> by lazy {
    DefaultedList.ofSize(width * height, Ingredient::test)
  }

  override fun matches(inv: CraftingInventory, world: World): Boolean {
    if (!fits(inv.width, inv.height)) return false

    for (i in 0 until width) {
      for (j in 0 until height) {
        val ingredientSlot = i + j * width
        val ingredient = ingredients[ingredientSlot]
        val stack = inv.getStack(i + j * inv.width)

        if (!ingredientPredicates[ingredientSlot].invoke(ingredient, stack)) {
          return false
        }
      }
    }

    return true
  }

  override fun craft(inventory: CraftingInventory): ItemStack = outputItem.copy()
  override fun fits(w: Int, h: Int) = w >= width && h >= height
  override fun getOutput() = outputItem
  override fun isIgnoredInRecipeBook() = false
  override fun getIngredients(): DefaultedList<Ingredient> =
    DefaultedList.copyOf(null, *ingredients.toTypedArray())

  override fun isEmpty(): Boolean {
    // Recipe.isEmpty incorrectly returns true if there are any empty ingredients, but ShapedRecipe overrides this to
    // filter out Ingredient.EMPTY first. Replicate the behaviour here.
    val list = getIngredients()
    return list.isEmpty() || list
      .filter { !it.isEmpty }
      .any { it.matchingStacks.isEmpty() }
  }
}
