package io.github.cunnydevelopment.cunnyaddon.utility.modules.internal;

import io.github.cunnydevelopment.cunnyaddon.utility.FileSystem;
import io.github.cunnydevelopment.cunnyaddon.utility.UtilityEvent;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompatibilityConfig extends UtilityEvent {
    // Regex
    private static final String isRename = "rename [\\w.]+ - [\\w- ]+";
    private static final String isRecategorize = "recategorize [\\w.]+ - (Combat\\+|Movement\\+|Misc\\+|Chat|Exploits|Unknown|Combat|Movement|Misc|Player|Render|World)";
    private static final String isRemove = "remove [\\w.]+";
    // Maps/Lists for actions
    public static final Map<String, String> rename = new HashMap<>();
    public static final Map<String, String> recategorize = new HashMap<>();
    public static  final List<String> remove = new ArrayList<>();
    // Class Dump
    public static final List<String> classes = new ArrayList<>();
    // Load status
    private static boolean loaded = false;

    public static void save() {
        FileSystem.write(FileSystem.CUNNY_PATH + "moduleDump.txt", String.join("\n", classes));
    }

    public static String getAddedBy(String pkg) {
        for (MeteorAddon addon : AddonManager.ADDONS) {
            if (pkg.startsWith(addon.getPackage())) {
                return addon.name;
            }
        }
        if (pkg.startsWith("meteordevelopment.meteorclient.")) return "Meteor";
        return "Unknown";
    }



    public static Color getColor(String mod) {
        for (MeteorAddon addon : AddonManager.ADDONS) {
            if (mod.startsWith(addon.getPackage())) {
                return addon.color;
            }
        }
        return new Color(157, 68, 215);
    }

    public static void load() {
        if (loaded) return;
        loaded = true;
        if (FileSystem.exists(FileSystem.COMPATIBILITY_PATH)) {
            var files = new File(FileSystem.COMPATIBILITY_PATH).listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isDirectory()) continue;
                String text = FileSystem.read(file);
                for (String txt : text.split("\n")) {
                    String newText = txt.strip().replaceAll("\n", "");
                    if (newText.isBlank() || newText.isEmpty()) continue;
                    if (newText.matches(isRename)) {
                        String[] str = newText.split(" ");
                        rename.put(str[1], newText.replaceFirst("rename [\\w.]+ - ", ""));
                    } else if (newText.matches(isRecategorize)) {
                        String[] str = newText.split(" ");
                        recategorize.put(str[1], str[3]);
                    } else if (newText.matches(isRemove)) {
                        remove.add(newText.split(" ")[1]);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(GameJoinedEvent event) {
        CompatibilityConfig.save();
    }
}
