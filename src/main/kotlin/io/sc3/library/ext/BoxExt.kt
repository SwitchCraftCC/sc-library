package io.sc3.library.ext

import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

val unitBox = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

val Box.volume: Int
  get() {
    val sx = xLength.toInt()
    val sy = yLength.toInt()
    val sz = zLength.toInt()
    return sx * sy * sz
  }

val Box.surfaceArea: Int
  get() {
    val sx = xLength.toInt()
    val sy = yLength.toInt()
    val sz = zLength.toInt()
    return sx * sy * 2 + sx * sz * 2 + sy * sz * 2
  }

fun Box.rotateTowards(facing: Direction): Box = rotateY(when(facing) {
  Direction.EAST -> 3
  Direction.SOUTH -> 2
  Direction.WEST -> 1
  else -> 0
})

fun Box.rotate(facing: Direction) = when(facing) {
  Direction.UP -> this
  Direction.DOWN -> rotateX(2)
  else -> rotateX(1).rotateTowards(facing)
}

fun Box.rotate(axis: RotationAxis, count: Int): Box {
  val angle = count * Math.PI.toFloat() / 2
  val min = Vec3d(minX - 8, minY - 8, minZ - 8).rotate(axis, angle)
  val max = Vec3d(maxX - 8, maxY - 8, maxZ - 8).rotate(axis, angle)

  return Box(
    (min(min.x + 8, max.x + 8) * 32).roundToInt() / 32.0,
    (min(min.y + 8, max.y + 8) * 32).roundToInt() / 32.0,
    (min(min.z + 8, max.z + 8) * 32).roundToInt() / 32.0,
    (max(min.x + 8, max.x + 8) * 32).roundToInt() / 32.0,
    (max(min.y + 8, max.y + 8) * 32).roundToInt() / 32.0,
    (max(min.z + 8, max.z + 8) * 32).roundToInt() / 32.0
  )
}

fun Box.rotateX(count: Int) = rotate(RotationAxis.POSITIVE_X, count)
fun Box.rotateY(count: Int) = rotate(RotationAxis.POSITIVE_Y, count)
fun Box.rotateZ(count: Int) = rotate(RotationAxis.POSITIVE_Z, count)

fun Box.toDiv16(): Box =
  Box(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0)

fun Box.toMul16(): Box =
  Box(minX * 16.0, minY * 16.0, minZ * 16.0, maxX * 16.0, maxY * 16.0, maxZ * 16.0)

fun Box.toDiv16VoxelShape(): VoxelShape =
  VoxelShapes.cuboid(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0)

val Box.faces: List<List<Vector3f>>
  get() = unitCube.map { face -> face.map { vertex -> Vector3f(
    max(minX.toFloat() / 16.0f, min(maxX.toFloat() / 16.0f, vertex.x)),
    max(minY.toFloat() / 16.0f, min(maxY.toFloat() / 16.0f, vertex.y)),
    max(minZ.toFloat() / 16.0f, min(maxZ.toFloat() / 16.0f, vertex.z))
  )}}

fun intBox(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) =
  Box(minX.toDouble(), minY.toDouble(), minZ.toDouble(), maxX.toDouble(), maxY.toDouble(), maxZ.toDouble())

fun randBox() = intBox(
  (0..7).random(), (0..7).random(), (0..7).random(),
  (8..15).random(), (8..15).random(), (8..15).random(),
)

fun Vec3d.rotate(axis: RotationAxis, angle: Float): Vec3d = when (axis) {
  RotationAxis.POSITIVE_X -> rotateX(angle)
  RotationAxis.NEGATIVE_X -> rotateX(-angle)
  RotationAxis.POSITIVE_Y -> rotateY(angle)
  RotationAxis.NEGATIVE_Y -> rotateY(-angle)
  RotationAxis.POSITIVE_Z -> rotateZ(angle)
  RotationAxis.NEGATIVE_Z -> rotateZ(-angle)
  else -> this
}
