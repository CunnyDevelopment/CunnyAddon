package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = InvUtils.Action.class, remap = false)
public class InvUtils$ActionMixin {
    @Inject(method = "run", at = @At("TAIL"))
    public void onRun(CallbackInfo ci) {
        assert mc.player != null;
        mc.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
    }
}
