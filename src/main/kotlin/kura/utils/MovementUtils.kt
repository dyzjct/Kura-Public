package kura.utils

import net.minecraft.util.MovementInput

object MovementUtils {
    fun MovementInput.resetMove() {
        moveForward = 0.0f
        moveStrafe = 0.0f
        forwardKeyDown = false
        backKeyDown = false
        leftKeyDown = false
        rightKeyDown = false
    }
}