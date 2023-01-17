package io.github.cunnydevelopment.cunnyaddon.modules;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public abstract class CunnyModule extends Module {

    public CunnyModule(Category category, String name, String description) {
        super(category, name, description);
        Modules.get().add(this);
    }
}
