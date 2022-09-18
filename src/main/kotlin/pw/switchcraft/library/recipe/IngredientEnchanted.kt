package pw.switchcraft.library.recipe

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class IngredientEnchanted(
  /** enchantment, min level **/
  private val enchantments: Map<Enchantment, Int>,
) : BaseIngredient() {
  private val stacks by lazy { itemsEnchantedWith(enchantments) }

  override fun getMatchingStacks() = stacks
  override fun isEmpty() = false

  override fun test(target: ItemStack?): Boolean {
    if (target == null || target.isEmpty) return false

    val nbtEnchantments = if (target.item === Items.ENCHANTED_BOOK) {
      EnchantedBookItem.getEnchantmentNbt(target)
    } else {
      target.enchantments
    }

    for (i in nbtEnchantments.indices) {
      val tag = nbtEnchantments.getCompound(i)
      val itemEnchant = Registry.ENCHANTMENT[Identifier(tag.getString("id"))]
      if (enchantments.containsKey(itemEnchant)) {
        return tag.getShort("lvl").toInt() >= enchantments[itemEnchant]!!
      }
    }

    return false
  }

  companion object {
    fun itemsEnchantedWith(enchantments: Map<Enchantment, Int>): Array<ItemStack> {
      if (enchantments.isEmpty()) return emptyArray()

      // Find any item in the registry which matches this predicate
      val stacks = mutableListOf<ItemStack>()

      enchantments.forEach { (enchantment, minLevel) ->
        for (item in Registry.ITEM) {
          if (enchantment.type?.isAcceptableItem(item) == true || item is EnchantedBookItem) {
            for (level in minLevel..enchantment.maxLevel) {
              val stack = ItemStack(item)
              EnchantmentHelper.set(Collections.singletonMap(enchantment, level), stack)
              stacks.add(stack)
            }
          }
        }
      }

      return stacks.toTypedArray()
    }
  }
}
