package io.sc3.library.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import io.sc3.library.ScLibrary
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

class IngredientBrew(
  private val effect: StatusEffect,
  private val potion: Potion
) : CustomIngredient {
  override fun getMatchingStacks() = listOf(
    PotionUtil.setPotion(ItemStack(Items.POTION), potion),
    PotionUtil.setPotion(ItemStack(Items.SPLASH_POTION), potion),
    PotionUtil.setPotion(ItemStack(Items.LINGERING_POTION), potion)
  )

  override fun requiresTesting(): Boolean = true

  override fun test(target: ItemStack): Boolean {
    if (target.isEmpty) return false
    return PotionUtil.getPotionEffects(target)
      .any { it.effectType === effect }
  }

  override fun getSerializer(): CustomIngredientSerializer<*> = Serializer

  object Serializer : CustomIngredientSerializer<IngredientBrew> {
    private val ID = ScLibrary.ModId("brew")

    override fun getIdentifier(): Identifier = ID

    override fun read(json: JsonObject): IngredientBrew {
      val effectId = Identifier(JsonHelper.getString(json, "effect"))
      val effect = Registries.STATUS_EFFECT.get(effectId) ?: throw JsonSyntaxException("Unknown effect $effectId")

      val potionId = Identifier(JsonHelper.getString(json, "potion"))
      val potion = when {
        Registries.POTION.containsId(potionId) -> Registries.POTION.get(potionId)
        else -> throw JsonSyntaxException("Unknown effect $potionId")
      }

      return IngredientBrew(effect, potion)
    }

    override fun write(json: JsonObject, ingredient: IngredientBrew) {
      json.addProperty("effect", Registries.STATUS_EFFECT.getId(ingredient.effect)!!.toString())
      json.addProperty("potion", Registries.POTION.getId(ingredient.potion).toString())
    }

    override fun read(buf: PacketByteBuf): IngredientBrew {
      val effect = buf.readRegistryValue(Registries.STATUS_EFFECT)!!
      val potion = buf.readRegistryValue(Registries.POTION)!!
      return IngredientBrew(effect, potion)
    }

    override fun write(buf: PacketByteBuf, ingredient: IngredientBrew) {
      buf.writeRegistryValue(Registries.STATUS_EFFECT, ingredient.effect)
      buf.writeRegistryValue(Registries.POTION, ingredient.potion)
    }
  }
}
