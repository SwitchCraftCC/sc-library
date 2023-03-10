package io.sc3.library.recipe

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList

/**
 * The underlying structure of a [ShapelessRecipe]-esque recipe, useful for writing serialisation and deserialization
 * code.
 */
class ShapelessRecipeSpec private constructor(
  val group: String,
  val category: CraftingRecipeCategory,
  val output: ItemStack,
  val input: DefaultedList<Ingredient>
) {
  fun write(buf: PacketByteBuf) {
    buf.writeString(group)
    buf.writeEnumConstant(category)
    buf.writeVarInt(input.size)
    for (ingredient in input) ingredient.write(buf)
    buf.writeItemStack(output)
  }

  companion object {
    private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    fun ofRecipe(recipe: ShapelessRecipe) = ShapelessRecipeSpec(
      recipe.group, recipe.category, recipe.getOutput(null) /* TODO(1.19.4) */, recipe.ingredients
    )

    fun ofJson(json: JsonObject): ShapelessRecipeSpec {
      val group = JsonHelper.getString(json, "group", "") ?: ""
      val category = CraftingRecipeCategory.CODEC.byId(
        JsonHelper.getString(json, "category", null),
        CraftingRecipeCategory.MISC
      )

      val inputs = DefaultedList.of<Ingredient>()
      for (input in JsonHelper.getArray(json, "ingredients")) {
        val ingredient = Ingredient.fromJson(input)
        if (!ingredient.isEmpty) inputs.add(ingredient)
      }

      val outputObject = JsonHelper.getObject(json, "result")
      val output = ShapedRecipe.outputFromJson(outputObject)

      outputObject.get("nbt")?.let {
        try {
          val nbtJson = if (it.isJsonObject) GSON.toJson(it) else JsonHelper.asString(it, "nbt")
          output.nbt = StringNbtReader.parse(nbtJson)
        } catch (e: CommandSyntaxException) {
          throw RuntimeException("Invalid NBT entry: ", e)
        }
      }

      return ShapelessRecipeSpec(group, category, output, inputs)
    }

    fun ofPacket(buf: PacketByteBuf): ShapelessRecipeSpec {
      val group = buf.readString()
      val category = buf.readEnumConstant(CraftingRecipeCategory::class.java)

      val size = buf.readVarInt()
      val input = DefaultedList.ofSize(size, Ingredient.EMPTY)
      for (i in input.indices) input[i] = Ingredient.fromPacket(buf)

      val output = buf.readItemStack()
      return ShapelessRecipeSpec(group, category, output, input)
    }
  }
}
