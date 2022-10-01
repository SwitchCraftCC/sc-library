package pw.switchcraft.library.ext

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory.createArrayBacked

inline fun <reified T> event(noinline invokerFactory: (Array<T>) -> T): Event<T>
  = createArrayBacked(T::class.java, invokerFactory)
