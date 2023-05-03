package me.windyteam.kura.notifications.other


import kotlin.math.pow
enum class Easing(private val easeFunction: (Double) -> Double) {

    LINEAR({ input -> input }),
    BACK_IN_OUT({ input -> if (input < 0.5) ((2 * input).pow(2.0) * ((2.5949095 + 1) * 2 * input - 2.5949095)) / 2 else ((2 * input - 2).pow(2.0) * ((2.5949095 + 1) * (input * 2 - 2) + 2.5949095) + 2) / 2 });

    open fun ease(input: Double) = easeFunction.invoke(input)


























    /*SINE_IN({ input -> 1 - cos((input * PI) / 2) }),

   SINE_OUT({ input -> sin((input * PI) / 2) }),

   SINE_IN_OUT({ input -> -(cos(PI * input) - 1) / 2 }),


   CUBIC_IN({ input -> input * input * input }),

   CUBIC_OUT({ input -> 1 - (1 - input).pow(3.0) }),

   CUBIC_IN_OUT({ input -> if (input < 0.5) 4 * input * input * input else 1 - (-2 * input + 2).pow(3.0) / 2 }),

   QUAD_IN({ input -> input * input }),

   QUAD_OUT({ input -> 1 - (1 - input) * (1 - input) }),

   QUAD_IN_OUT({ input -> if (input < 0.5) 2.0 * input * input else 1 - (-2 * input + 2).pow(2.0) / 2 }),

   QUART_IN({ input -> input * input * input * input }),

   QUART_OUT({ input -> 1 - (1 - input).pow(4.0) }),

   QUART_IN_OUT({ input -> if (input < 0.5) 8 * input * input * input * input else 1 - (-2 * input + 2).pow(4.0) / 2 }),

   QUINT_IN({ input -> input * input * input * input * input }),

   QUINT_OUT({ input -> 1 - (1 - input).pow(5.0) }),

   QUINT_IN_OUT({ input -> if (input < 0.5) 16 * input * input * input * input * input else 1 - (-2 * input + 2).pow(5.0) / 2 }),

   CIRC_IN({ input -> 1 - sqrt(1 - input.pow(2.0)) }),

   CIRC_OUT({ input -> sqrt(1 - (input - 1).pow(2)) }),

   CIRC_IN_OUT({ input -> if (input < 0.5) (1 - sqrt(1 - (2 * input).pow(2.0))) / 2 else (sqrt(1 - (-2 * input + 2).pow(2.0)) + 1) / 2 }),

   EXPO_IN({ input -> if (input == 0.0) 0.0 else 2.0.pow(10.0 * input - 10.0) }),

   EXPO_OUT({ input -> if (input == 1.0) 1.0 else 1 - 2.0.pow(-10 * input) }),

   EXPO_IN_OUT({ input -> if (input == 0.0) 0.0 else if (input == 1.0) 1.0 else if (input < 0.5) 2.0.pow(20 * input - 10) / 2.0 else (2 - 2.0.pow(-20 * input + 10)) / 2.0 }),

   ELASTIC_IN({ input -> if (input == 0.0) 0.0 else if (input == 1.0) 1.0 else (-2.0).pow(10 * input - 10) * sin((input * 10 - 10.75) * ((2 * PI) / 3)) }),

   ELASTIC_OUT({ input -> if (input == 0.0) 0.0 else if (input == 1.0) 1.0 else 2.0.pow(-10 * input) * sin((input * 10 - 0.75) * ((2 * PI) / 3)) + 1 }),

   ELASTIC_IN_OUT({ input -> if (input == 0.0) 0.0 else if (input == 1.0) 1.0 else if (input < 0.5) -(2.0.pow(20 * input - 10) * sin((20 * input - 11.125) * ((2 * Math.PI) / 4.5))) / 2 else (2.0.pow(-20 * input + 10) * sin((20 * input - 11.125) * ((2 * Math.PI) / 4.5))) / 2 + 1 }),

   BACK_IN({ input -> 2.70158 + 1 * input * input * input - 1.70158 * input * input }),

   BACK_OUT({ input -> 1 + 2.70158 * (input - 1).pow(3.0) + 1.70158 * (input - 1).pow(2.0) }),
    */

}