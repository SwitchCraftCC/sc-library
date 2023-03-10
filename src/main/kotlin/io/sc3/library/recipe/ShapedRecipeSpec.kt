package io.sc3.library.recipe

import com.google.gson.JsonObject
import io.sc3.library.ScLibrary.ModId
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.collection.DefaultedList

/**
 * The underlying structure of a [ShapedRecipe]-esque recipe, useful for writing serialisation and deserialization
 * code.
 */
class ShapedRecipeSpec private constructor(
  val group: String,
  val category: CraftingRecipeCategory,
  val width: Int,
  val height: Int,
  val ingredients: DefaultedList<Ingredient>,
  val output: ItemStack,
) {
  fun write(buf: PacketByteBuf) {
    buf.writeString(group)
    buf.writeEnumConstant(category)

    buf.writeVarInt(width)
    buf.writeVarInt(height)
    for (ingredient in ingredients) ingredient.write(buf)

    buf.writeItemStack(output)
  }

  companion object {
    fun ofRecipe(recipe: ShapedRecipe) = ShapedRecipeSpec(
      recipe.group, recipe.category, recipe.width, recipe.height, recipe.ingredients,
      recipe.getOutput(null) // TODO(1.19.4)
    )

    fun ofJson(json: JsonObject): ShapedRecipeSpec =
      ofRecipe(RecipeSerializer.SHAPED.read(ModId("ignore"), json))

    fun ofPacket(buf: PacketByteBuf): ShapedRecipeSpec {
      val group = buf.readString()
      val category = buf.readEnumConstant(CraftingRecipeCategory::class.java)

      val width = buf.readVarInt()
      val height = buf.readVarInt()
      val ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY)
      for (i in ingredients.indices) ingredients[i] = Ingredient.fromPacket(buf)

      val output = buf.readItemStack()

      return ShapedRecipeSpec(group, category, width, height, ingredients, output)
    }
  }
}
