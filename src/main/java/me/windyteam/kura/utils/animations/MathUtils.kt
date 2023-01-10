/*
 * Copyright (c) 2021 CakeSlayers Reversing Team. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * CakeSlayers' Github website: https://github.com/CakeSlayers
 * This file was created by SagiriXiguajerry at 2021/11/21 下午7:52
 */

package me.windyteam.kura.utils.animations

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Suppress("NOTHING_TO_INLINE")
object MathUtils {
    @JvmStatic
    inline fun ceilToPOT(valueIn: Int): Int {
        // Magical bit shifting
        var i = valueIn
        i--
        i = i or (i shr 1)
        i = i or (i shr 2)
        i = i or (i shr 4)
        i = i or (i shr 8)
        i = i or (i shr 16)
        i++
        return i
    }

    @JvmStatic
    inline fun round(value: Float, places: Int): Float {
        val scale = 10.0f.pow(places)
        return kotlin.math.round(value * scale) / scale
    }

    @JvmStatic
    inline fun round(value: Double, places: Int): Double {
        val scale = 10.0.pow(places)
        return kotlin.math.round(value * scale) / scale
    }

    @JvmStatic
    inline fun decimalPlaces(value: Double) = value.toString().split('.').getOrElse(1) { "0" }.length

    @JvmStatic
    inline fun decimalPlaces(value: Float) = value.toString().split('.').getOrElse(1) { "0" }.length

    @JvmStatic
    inline fun isNumberEven(i: Int): Boolean {
        return i and 1 == 0
    }

    @JvmStatic
    inline fun reverseNumber(num: Int, min: Int, max: Int): Int {
        return max + min - num
    }

    @JvmStatic
    inline fun convertRange(valueIn: Int, minIn: Int, maxIn: Int, minOut: Int, maxOut: Int): Int {
        return convertRange(
            valueIn.toDouble(),
            minIn.toDouble(),
            maxIn.toDouble(),
            minOut.toDouble(),
            maxOut.toDouble()
        ).toInt()
    }

    @JvmStatic
    inline fun convertRange(valueIn: Float, minIn: Float, maxIn: Float, minOut: Float, maxOut: Float): Float {
        return convertRange(
            valueIn.toDouble(),
            minIn.toDouble(),
            maxIn.toDouble(),
            minOut.toDouble(),
            maxOut.toDouble()
        ).toFloat()
    }

    @JvmStatic
    inline fun convertRange(valueIn: Double, minIn: Double, maxIn: Double, minOut: Double, maxOut: Double): Double {
        val rangeIn = maxIn - minIn
        val rangeOut = maxOut - minOut
        val convertedIn = (valueIn - minIn) * (rangeOut / rangeIn) + minOut
        val actualMin = min(minOut, maxOut)
        val actualMax = max(minOut, maxOut)
        return min(max(convertedIn, actualMin), actualMax)
    }

    @JvmStatic
    inline fun lerp(from: Double, to: Double, delta: Double): Double {
        return from + (to - from) * delta
    }

    @JvmStatic
    inline fun lerp(from: Float, to: Float, delta: Float): Float {
        return from + (to - from) * delta
    }
}