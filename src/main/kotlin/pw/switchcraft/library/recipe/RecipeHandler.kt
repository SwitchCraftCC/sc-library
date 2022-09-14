package pw.switchcraft.library.recipe

import net.minecraft.data.server.recipe.RecipeJsonProvider
import java.util.function.Consumer

interface RecipeHandler {
  fun registerSerializers() {}
  fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {}
}
