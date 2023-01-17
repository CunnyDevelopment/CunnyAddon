package io.github.cunnydevelopment.cunnyaddon.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class SpecialEffects {
    protected static final Map<String, Color> PLAYER_COLORS = new HashMap<>();

    public static boolean hasColor(PlayerEntity entity) {
        return PLAYER_COLORS.containsKey(entity.getUuidAsString());
    }

    public static Color getColor(PlayerEntity entity) {
        return PLAYER_COLORS.getOrDefault(entity.getUuidAsString(), Color.YELLOW);
    }

    public static void load() {
        Gson gson = new GsonBuilder().setLenient().create();
        FileSystem.writeUrl("https://raw.githubusercontent.com/ViaTi/Varti/main/neko_special.json", FileSystem.CUNNY_PATH + "special.json");
        SpecialConfig specialConfig = gson.fromJson(FileSystem.read(FileSystem.CUNNY_PATH + "special.json"), SpecialConfig.class);
        specialConfig.parseColors();
    }
}
