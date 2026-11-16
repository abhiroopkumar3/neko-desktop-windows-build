package com.zedlabs.pastelplaceholder

/** Placeholder colors for covers while they load; ids resolve through Resources.getColor. */
object Pastel {
  const val BASE = 0x7f0b0000
  val light = intArrayOf(0xFFFFD1DC.toInt(), 0xFFD1E8FF.toInt(), 0xFFD7F5DD.toInt(), 0xFFFFF1C1.toInt(), 0xFFE5D4FF.toInt(), 0xFFFFE0CC.toInt(), 0xFFCFF3EE.toInt(), 0xFFF3D1F4.toInt())
  @JvmStatic fun getColorLight(): Int = BASE + light.indices.random()
  @JvmStatic fun getColorDark(): Int = getColorLight()
}
