package io.sc3.library

import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ScLibrary {
  val log = LoggerFactory.getLogger("ScLibrary")!!

  val modId = "sc-library"
  fun ModId(value: String) = Identifier(modId, value)
}
