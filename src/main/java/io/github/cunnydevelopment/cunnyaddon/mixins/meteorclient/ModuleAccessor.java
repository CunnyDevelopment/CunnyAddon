package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import meteordevelopment.meteorclient.systems.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Module.class, remap = false)
public interface ModuleAccessor {
    @Accessor("title")
    void setTitle(String title);
}
