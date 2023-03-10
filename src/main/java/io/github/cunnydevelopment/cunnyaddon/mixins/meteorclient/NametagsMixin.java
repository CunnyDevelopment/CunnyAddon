package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.modules.render.NametagsPlus;
import io.github.cunnydevelopment.cunnyaddon.utility.SpecialEffects;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.ModuleReference;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Nametags.class, remap = false)
public abstract class NametagsMixin {
    @Shadow
    protected abstract void drawBg(double x, double y, double width, double height);

    PlayerEntity pl;

    @Inject(method = "renderNametagPlayer", at = @At(value = "HEAD"))
    public void onRenderPlayer(PlayerEntity player, boolean shadow, CallbackInfo ci) {
        pl = player;
    }

    @Redirect(method = "renderNametagPlayer", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/renderer/text/TextRenderer;render(Ljava/lang/String;DDLmeteordevelopment/meteorclient/utils/render/color/Color;Z)D", ordinal = 1))
    public double onRenderText(TextRenderer instance, String s, double x, double y, Color color, boolean b) {
        double nx = instance.render(s, x, y, color, b);
        if (ModuleReference.state(NametagsPlus.class) && pl != null) instance.render(pl.getUuidAsString(), x, y - 30, color, b);
        return nx;
    }

    @Redirect(method = "renderNametagPlayer", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/modules/render/Nametags;drawBg(DDDD)V"))
    public void onDrawBg(Nametags instance, double x, double y, double width, double height) {
        if (pl == null) drawBg(x, y, width, height);
        if (SpecialEffects.hasColor(pl)) {
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(x - 1, y - 1, width + 2, height + 2, SpecialEffects.getColor(pl).copy().a(80));
            Renderer2D.COLOR.render(null);
        } else {
            drawBg(x, y, width, height);
        }

        pl = null;
    }
}
