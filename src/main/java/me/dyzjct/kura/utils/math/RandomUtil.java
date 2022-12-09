package me.dyzjct.kura.utils.math;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random();
    public static Random getRandom() {
        return random;
    }

    public static String randomString(int minLength, int maxLength) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }
    public final long randomDelay(int minDelay, int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        return startInclusive == endInclusive || endInclusive - startInclusive <= 0.0 ? startInclusive : startInclusive + (endInclusive - startInclusive) * Math.random();
    }

    public static long nextLong(long startInclusive, long endInclusive) {
        return endInclusive - startInclusive <= 0L ? startInclusive : (long) (startInclusive + (endInclusive - startInclusive) * Math.random());
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        return startInclusive == endInclusive || endInclusive - startInclusive <= 0f ? startInclusive : (float) (startInclusive + (endInclusive - startInclusive) * Math.random());
    }
}
