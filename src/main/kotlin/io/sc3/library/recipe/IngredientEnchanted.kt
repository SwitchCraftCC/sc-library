package io.sc3.library.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import io.sc3.library.ScLibrary
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import java.util.*

class IngredientEnchanted(
  private val enchantment: Enchantment,
  private val minLevel: Int,
) : CustomIngredient {
  override fun getMatchingStacks(): List<ItemStack> {
    val stacks = mutableListOf<ItemStack>()

    // Find any item in the registry which matches this predicate
    for (item in Registries.ITEM) {
      if (enchantment.type?.isAcceptableItem(item) == true || item is EnchantedBookItem) {
        for (level in minLevel..enchantment.maxLevel) {
          val stack = ItemStack(item)
          EnchantmentHelper.set(Collections.singletonMap(enchantment, level), stack)
          stacks.add(stack)
        }
      }
    }

    return stacks
  }

  override fun requiresTesting(): Boolean = true

  override fun test(target: ItemStack?): Boolean {
    if (target == null || target.isEmpty) return false

    val nbtEnchantments = if (target.item === Items.ENCHANTED_BOOK) {
      EnchantedBookItem.getEnchantmentNbt(target)
    } else {
      target.enchantments
    }

    for (i in nbtEnchantments.indices) {
      val tag = nbtEnchantments.getCompound(i)
      val itemEnchant = Registries.ENCHANTMENT[Identifier(tag.getString("id"))]
      if (itemEnchant == this.enchantment) {
        return tag.getShort("lvl").toInt() >= minLevel
      }
    }

    return false
  }

  override fun getSerializer(): CustomIngredientSerializer<*> = Serializer

  object Serializer : CustomIngredientSerializer<IngredientEnchanted> {
    private val ID = ScLibrary.ModId("enchantment")
    override fun getIdentifier(): Identifier = ID

    override fun read(json: JsonObject): IngredientEnchanted {
      val enchantId = Identifier(JsonHelper.getString(json, "id"))
      val enchant = Registries.ENCHANTMENT.get(enchantId) ?: throw JsonSyntaxException("Unknown enchantment $enchantId")

      val minLevel = JsonHelper.getInt(json, "level")
      return IngredientEnchanted(enchant, minLevel)
    }

    override fun write(json: JsonObject, ingredient: IngredientEnchanted) {
      json.addProperty("id", Registries.ENCHANTMENT.getId(ingredient.enchantment)!!.toString())
      json.addProperty("level", ingredient.minLevel)
    }

    override fun read(buf: PacketByteBuf): IngredientEnchanted {
      val enchantment = buf.readRegistryValue(Registries.ENCHANTMENT)!!
      val minLevel = buf.readVarInt()
      return IngredientEnchanted(enchantment, minLevel)
    }

    override fun write(buf: PacketByteBuf, ingredient: IngredientEnchanted) {
      buf.writeRegistryValue(Registries.ENCHANTMENT, ingredient.enchantment)
      buf.writeVarInt(ingredient.minLevel)
    }
  }
}
