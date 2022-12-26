package io.sc3.library.recipe

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil

class IngredientBrew(
  private val effect: StatusEffect,
  potion: Potion
) : BaseIngredient() {
  private val basicStacks = arrayOf(
    PotionUtil.setPotion(ItemStack(Items.POTION), potion),
    PotionUtil.setPotion(ItemStack(Items.SPLASH_POTION), potion),
    PotionUtil.setPotion(ItemStack(Items.LINGERING_POTION), potion)
  )

  override fun getMatchingStacks() = basicStacks
  override fun isEmpty() = false

  override fun test(target: ItemStack?): Boolean {
    if (target == null || target.isEmpty) return false
    return PotionUtil.getPotionEffects(target)
      .any { it.effectType === effect }
  }
}
