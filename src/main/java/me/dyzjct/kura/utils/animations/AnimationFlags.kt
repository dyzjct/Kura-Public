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
 * This file was created by SagiriXiguajerry at 2021/11/21 下午7:53
 */

package me.dyzjct.kura.utils.animations

class AnimationFlag(private val interpolation: InterpolateFunction) {

    constructor(easing: Easing, length: Float) : this({ time, prev, current ->
        easing.incOrDec(Easing.toDelta(time, length), prev, current)
    })

    private var prev = 0.0f
    private var current = 0.0f
    private var time = System.currentTimeMillis()

    fun forceUpdate(prev: Float, current: Float) {
        this.prev = prev
        this.current = current
        time = System.currentTimeMillis()
    }

    fun getAndUpdate(input: Float): Float {
        val render = interpolation.invoke(time, prev, current)

        if (input != current) {
            prev = render
            current = input
            time = System.currentTimeMillis()
        }

        return render
    }

    fun get(input: Float, update: Boolean): Float {
        val render = interpolation.invoke(time, prev, current)

        if (update && input != current) {
            prev = render
            current = input
            time = System.currentTimeMillis()
        }

        return render
    }
}