package io.github.cunnydevelopment.cunnyaddon.utility;

import java.util.List;
import java.util.Random;

public class ArrayUtils {
    private static final Random RANDOM = new Random();

    public static <T> T random(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(RANDOM.nextInt(list.size()));
    }
}
