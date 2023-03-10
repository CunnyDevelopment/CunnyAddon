package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModulesScreen.class, remap = false)
public class ModulesScreenMixin {

    @Inject(method = "initWidgets", at = @At("TAIL"))
    public void initWidgets(CallbackInfo ci) {
        //((ModulesScreen) (Object) this).add(LogTextBox.createWindow());
    }
}
