package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.themes.gui.CunnySettingsWidgetFactory;
import io.github.cunnydevelopment.cunnyaddon.themes.gui.StringOptionSetting;
import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultSettingsWidgetFactory.class, remap = false)
public abstract class DefaultSettingsWidgetFactoryMixin extends SettingsWidgetFactory {
    public DefaultSettingsWidgetFactoryMixin(GuiTheme theme) {
        super(theme);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void a(GuiTheme theme, CallbackInfo ci) {
        factories.put(StringOptionSetting.class, (table, setting) -> CunnySettingsWidgetFactory.optionsW(table, (StringOptionSetting) setting));
    }
}
