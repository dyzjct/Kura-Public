package me.windyteam.kura.notifications.other

open class Animation(val length: () -> Float, val initialState: Boolean, val easing: () -> Easing) {

    private var lastMillis: Long = 0L
    var state: Boolean = initialState
        set(value) {
            lastMillis = if (!value) {
                System.currentTimeMillis() - ((1 - getLinearFactor()) * length.invoke().toLong()).toLong()
            } else {
                System.currentTimeMillis() - (getLinearFactor() * length.invoke().toLong()).toLong()
            }

            field = value
        }

    constructor(length: Float, initialState: Boolean, easing: Easing) : this({ length }, initialState, { easing })

    constructor(length: () -> Float, initialState: Boolean, easing: Easing) : this(length, initialState, { easing })

    constructor(length: Float, initialState: Boolean, easing: () -> Easing) : this({ length }, initialState, easing)

    open fun getAnimationFactor(): Double = if (state) {
        easing.invoke().ease(((System.currentTimeMillis() - lastMillis.toDouble()) / length.invoke().toDouble()).coerceIn(0.0, 1.0))
    } else {
        easing.invoke().ease((1 - (System.currentTimeMillis() - lastMillis.toDouble()) / length.invoke().toDouble()).coerceIn(0.0, 1.0))
    }

    fun resetToDefault() {
        state = initialState

        lastMillis = if (initialState) {
            System.currentTimeMillis() - ((1 - getLinearFactor()) * length.invoke().toLong()).toLong()
        } else {
            System.currentTimeMillis() - (getLinearFactor() * length.invoke().toLong()).toLong()
        }
    }

    private fun getLinearFactor(): Double = if (!state) { (1 - (System.currentTimeMillis() - lastMillis.toDouble()) / length.invoke().toDouble()).coerceIn(0.0, 1.0) } else { ((System.currentTimeMillis() - lastMillis.toDouble()) / length.invoke().toDouble()).coerceIn(0.0, 1.0) }

}