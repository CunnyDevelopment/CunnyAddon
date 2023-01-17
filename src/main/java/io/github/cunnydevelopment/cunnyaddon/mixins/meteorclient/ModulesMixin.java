package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.CompatibilityConfig;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Modules.class, remap = false)
public abstract class ModulesMixin {
    @Inject(method = "registerCategory", at = @At("HEAD"), cancellable = true)
    private static void onRegisterCategory(Category category, CallbackInfo ci) {
        if (!Categories.add(category)) ci.cancel();
    }

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void onAdd(Module _module, CallbackInfo ci) {
        if (CompatibilityConfig.remove.contains(_module.getClass().getName())) ci.cancel();
    }
}
