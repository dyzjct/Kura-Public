package me.dyzjct.kura.setting

import me.dyzjct.kura.module.IModule
import java.util.*
import java.util.function.Predicate

class ModeSetting<T : Enum<*>?>(modeName: String?, contain: IModule?, clazz: T) : Setting<T>(modeName, contain, clazz) {
    var indexValue: Int
        private set

    init {
        indexValue = getIndexMode(clazz)
    }

    val modes: Array<*>
        get() = value!!::class.java.enumConstants as Array<*>
    val modesAsStrings: Array<String>
        get() = Arrays.stream(modes).map { it.toString() }.toArray { Array() }

    private fun Array(): Array<String> {
        return Array()
    }

    override fun setValue(value: T) {
        super.setValue(value)
        indexValue = getIndexMode(getValue())
    }

    fun setValueByString(str: String?) {
        value = java.lang.Enum.valueOf(value!!::class.java, str!!) as T
    }

    fun setValueByIndex(index: Int) {
        val id = 0.coerceAtLeast((modes.size - 1).coerceAtMost(index))
        value = modes[id] as T
    }

    fun forwardLoop() {
        indexValue = if (indexValue < modes.size - 1) ++indexValue else 0
        value = modes[indexValue] as T
    }

    fun getIndexMode(clazz: T?): Int {
        for (E in modes.indices) {
            if (modes[E] === clazz) {
                return E
            }
        }
        return 0
    }

    val valueAsString: String
        get() = value.toString()

    override fun setOnChange(listener: onChangeListener<T>): Setting<T>? {
        return super.setOnChange(listener)
    }

    override fun v(predicate: Predicate<Any>): ModeSetting<T> {
        return super.v(predicate) as ModeSetting<T>
    }

    fun b(value: BooleanSetting): ModeSetting<T> {
        return super.v { value.value } as ModeSetting<T>
    }

    fun r(value: BooleanSetting): ModeSetting<T> {
        return super.v { !value.value } as ModeSetting<T>
    }

    fun c(min: Double, setting: Setting<*>?, max: Double): ModeSetting<T> {
        if (setting is IntegerSetting) {
            return super.v { v: Any? -> setting.value.toDouble() in min..max } as ModeSetting<T>
        }
        if (setting is FloatSetting) {
            return super.v { v: Any? -> setting.value.toDouble() in min..max } as ModeSetting<T>
        }
        return if (setting is DoubleSetting) {
            super.v { v: Any? -> setting.value in min..max } as ModeSetting<T>
        } else super.v { v: Any? -> true } as ModeSetting<T>
    }

    fun c(min: Double, setting: Setting<*>?): ModeSetting<*> {
        if (setting is IntegerSetting) {
            return super.v { v: Any? -> setting.value.toDouble() >= min } as ModeSetting<*>
        }
        if (setting is FloatSetting) {
            return super.v { v: Any? -> setting.value.toDouble() >= min } as ModeSetting<*>
        }
        return if (setting is DoubleSetting) {
            super.v { setting.value >= min } as ModeSetting<*>
        } else super.v { true } as ModeSetting<*>
    }

    fun c(setting: Setting<*>?, max: Double): ModeSetting<*> {
        if (setting is IntegerSetting) {
            return super.v { setting.value.toDouble() <= max } as ModeSetting<*>
        }
        if (setting is FloatSetting) {
            return super.v { setting.value.toDouble() <= max } as ModeSetting<*>
        }
        return if (setting is DoubleSetting) {
            super.v { setting.value <= max } as ModeSetting<*>
        } else super.v { true } as ModeSetting<*>
    }

    fun mc(value: ModeSetting<*>, mode: Enum<*>): ModeSetting<*> {
        visibility.add(Predicate { value.value === mode })
        return this
    }

    fun m(value: ModeSetting<T?>, mode: Enum<*>): ModeSetting<T> {
        visibility.add(Predicate { value.value === mode })
        return this
    }
}