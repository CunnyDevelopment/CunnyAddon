package io.github.cunnydevelopment.cunnyaddon.utility;

import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.HashMap;
import java.util.Map;

public class SpecialConfig {
    public Map<String, String> colors = new HashMap<>();

    public void parseColors() {
        colors.forEach((k, v) -> {
            if (v.isBlank()) return;
            String[] color = v.split(":");
            if (color.length < 2) return;
            SpecialEffects.PLAYER_COLORS.put(k,
                new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
        });
    }
}
