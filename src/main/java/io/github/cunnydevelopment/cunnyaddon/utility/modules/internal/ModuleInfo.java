package io.github.cunnydevelopment.cunnyaddon.utility.modules.internal;

import meteordevelopment.meteorclient.systems.modules.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleInfo {
    public static Map<Module, List<TextState>> infoMap = new HashMap<>();

    public static void addInfo(Module module, TextState... textState) {
        if (!hasInfo(module)) infoMap.put(module, new ArrayList<>());
        for (TextState state : textState) {
            infoMap.get(module).add(state);
        }
    }

    public static boolean hasInfo(Module module) {
        return infoMap.containsKey(module);
    }

    public static List<TextState> getInfo(Module module) {
        return infoMap.get(module);
    }
}
