package io.github.cunnydevelopment.cunnyaddon.utility;

import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    public static final Category COMBAT = new Category("Combat+");
    public static final Category MOVEMENT = new Category("Movement+");
    public static final Category MISC = new Category("Misc+");
    public static final Category CHAT = new Category("Chat");
    public static final Category RENDER = new Category("Render+");
    public static final Category EXPLOITS = new Category("Exploits");
    public static final Category UNKNOWN = new Category("Unknown");
    public static final HudGroup HUD = new HudGroup("Cunny Addon");
    public static final List<String> KNOWN_CATEGORIES = List.of(
        "Combat+", "Movement+", "Misc+", "Chat", "Render+", "Exploits", "Unknown", "Combat", "Movement", "Misc", "Player", "Render", "World"
    );
    private static final List<String> ADDED_CATEGORIES = new ArrayList<>();

    public static boolean add(Category category) {
        if (ADDED_CATEGORIES.contains(category.name)) return false;
        ADDED_CATEGORIES.add(category.name);
        return KNOWN_CATEGORIES.contains(category.name);
    }

    public static Category get(String str) {
        return switch (str.toLowerCase()) {
            case "combat+" -> COMBAT;
            case "movement+" -> MOVEMENT;
            case "misc+" -> MISC;
            case "chat" -> CHAT;
            case "render+" -> RENDER;
            case "exploits" -> EXPLOITS;
            case "combat" -> meteordevelopment.meteorclient.systems.modules.Categories.Combat;
            case "movement" -> meteordevelopment.meteorclient.systems.modules.Categories.Movement;
            case "misc" -> meteordevelopment.meteorclient.systems.modules.Categories.Misc;
            case "player" -> meteordevelopment.meteorclient.systems.modules.Categories.Player;
            case "render" -> meteordevelopment.meteorclient.systems.modules.Categories.Render;
            case "world" -> meteordevelopment.meteorclient.systems.modules.Categories.World;
            default -> UNKNOWN;
        };
    }

    public static Category getFromModule(@NotNull Module module, String str) {
        if (!module.getClass().getPackageName().startsWith("meteordevelopment.meteorclient.")) {
            if (KNOWN_CATEGORIES.contains(str + "+")) {
                return get(str + "+");
            }
        }

        return get(str);
    }
}
