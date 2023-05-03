package me.windyteam.kura.notifications.other.color

import me.windyteam.kura.notifications.other.Animation
import me.windyteam.kura.notifications.other.Easing
import java.awt.Color

class AnimationColors(val from: Color, val to: Color, length: () -> Float, initialState: Boolean, easing: () -> Easing) : Animation(length, initialState, easing) {

    constructor(from: Color, to: Color, length: Float, initialState: Boolean, easing: Easing) : this(from, to, { length }, initialState, { easing })

    constructor(from: Color, to: Color, length: () -> Float, initialState: Boolean, easing: Easing) : this(from, to, length, initialState, { easing })

    constructor(from: Color, to: Color, length: Float, initialState: Boolean, easing: () -> Easing) : this(from, to, { length }, initialState, easing)

    fun getColour(): Color {
        val factor = getAnimationFactor()

        return Color(
            (from.red + (to.red - from.red) * factor).toInt(),
            (from.green + (to.green - from.green) * factor).toInt(),
            (from.blue + (to.blue - from.blue) * factor).toInt(),
            (from.alpha + (to.alpha - from.alpha) * factor).toInt()
        )
    }

}