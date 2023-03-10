package io.github.cunnydevelopment.cunnyaddon.utility.modules.external;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient.ModuleAccessor;
import io.github.cunnydevelopment.cunnyaddon.utility.FileSystem;
import io.github.cunnydevelopment.cunnyaddon.utility.UtilityEvent;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.ReferenceConfig;
import lombok.Getter;
import lombok.Setter;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ModuleReference extends UtilityEvent {
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .setLenient()
        .create();

    public final static Map<Class<? extends Module>, Boolean> STATES = new HashMap<>();
    public final static List<Module> INVERTED_BIND = new ArrayList<>();
    public final static List<Module> TOGGLE_ON_LEAVE = new ArrayList<>();
    public final static Map<Module, String> MODULE_RENAME = new HashMap<>();
    @Getter
    @Setter
    private static int fakeExplosionRadius = 0;

    private int ticks = 5;

    public static boolean state(Class<? extends Module> module) {
        return STATES.getOrDefault(module, false);
    }

    public static boolean isInvertedBind(Module module) {
        return INVERTED_BIND.contains(module);
    }

    public static boolean isToggledOnLeave(Module module) {
        return TOGGLE_ON_LEAVE.contains(module);
    }

    public static String getModuleName(Module module) {
        return MODULE_RENAME.getOrDefault(module, "");
    }


    public static void save() {
        ReferenceConfig config = new ReferenceConfig();
        INVERTED_BIND.forEach(module -> {
            if (module != null) config.invertedBind.add(module.name);
        });

        TOGGLE_ON_LEAVE.forEach(module -> {
            if (module != null) config.toggleOnLeave.add(module.name);
        });

        MODULE_RENAME.forEach((k, v) -> {
            if (k != null) config.moduleMap.put(k.name, v);
        });

        FileSystem.write(FileSystem.CUNNY_PATH + "references.json", GSON.toJson(config));
    }

    public static void load() {
        if (!FileSystem.exists(FileSystem.CUNNY_PATH + "references.json")) return;
        ReferenceConfig config = GSON.fromJson(FileSystem.read(FileSystem.CUNNY_PATH + "references.json"), ReferenceConfig.class);
        config.invertedBind.forEach(k -> {
            if (Modules.get().get(k) != null) INVERTED_BIND.add(Modules.get().get(k));
        });

        config.toggleOnLeave.forEach(k -> {
            if (Modules.get().get(k) != null) TOGGLE_ON_LEAVE.add(Modules.get().get(k));
        });

        config.moduleMap.forEach((k, v) -> {
            if (Modules.get().get(k) == null) return;
            MODULE_RENAME.put(Modules.get().get(k), v);
            ((ModuleAccessor) Modules.get().get(k)).setTitle(v);
        });
    }

    @EventHandler
    public void onOpenScreen(OpenScreenEvent event) {
        save();
    }

    @EventHandler
    public void onLeave(GameLeftEvent event) {
        TOGGLE_ON_LEAVE.forEach(module -> {
            if (module.isActive()) module.toggle();
        });
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (ticks >= 0 || mc.currentScreen != null) {
            ticks--;
            return;
        }

        ticks = 5;

        INVERTED_BIND.forEach(module -> {
            if (module.toggleOnBindRelease) return;
            if (module.keybind.isValid() && module.keybind.isPressed()) {
                if (module.isActive()) {
                    module.toggle();
                }
            } else {
                if (!module.isActive()) {
                    module.toggle();
                }
            }
        });
    }
}
