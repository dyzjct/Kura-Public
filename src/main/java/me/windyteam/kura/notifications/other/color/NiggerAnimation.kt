package me.windyteam.kura.notifications.other.color

import me.windyteam.kura.notifications.other.Animation
import me.windyteam.kura.notifications.other.Easing

class NiggerAnimation(var minimum: Float, var maximum: Float, length: () -> Float, initialState: Boolean, easing: () -> Easing) : Animation(length, initialState, easing) {

    init {
        if (minimum > maximum) {
            val min = minimum
            val max = maximum

            minimum = max
            maximum = min
        }
    }
    constructor(minimum: Float, maximum: Float, length: Float, initialState: Boolean, easing: Easing) : this(minimum, maximum, { length }, initialState, { easing })

    constructor(minimum: Float, maximum: Float, length: () -> Float, initialState: Boolean, easing: Easing) : this(minimum, maximum, length, initialState, { easing })
    constructor(minimum: Float, maximum: Float, length: Float, initialState: Boolean, easing: () -> Easing) : this(minimum, maximum, { length }, initialState, easing)

    override fun getAnimationFactor(): Double {
        return minimum + ((maximum - minimum) * super.getAnimationFactor())
    }

}