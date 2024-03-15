package io.sc3.library.ext

import net.fabricmc.fabric.api.util.NbtType.*
import net.minecraft.nbt.NbtCompound

fun NbtCompound.byteToDouble(key: String): Double = getByte(key).toDouble()

fun NbtCompound.putOptInt(key: String, value: Int?) { value?.let { putInt(key, it) } }
fun NbtCompound.putOptString(key: String, value: String?) { value?.let { putString(key, it) } }

fun NbtCompound.putNullableCompound(key: String, value: NbtCompound?) {
  if (value != null) put(key, value) else remove(key)
}

fun NbtCompound.optBoolean(key: String): Boolean? =
  if (contains(key, BYTE)) getByte(key) != 0.toByte() else null
fun NbtCompound.optInt(key: String): Int? =
  if (contains(key, INT)) getInt(key) else null
fun NbtCompound.optString(key: String): String? =
  if (contains(key, STRING)) getString(key) else null
fun NbtCompound.optCompound(key: String): NbtCompound? =
  if (contains(key, COMPOUND)) getCompound(key) else null
