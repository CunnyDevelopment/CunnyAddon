package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.CompatibilityConfig;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.TextState;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WTooltip.class, remap = false)
public abstract class WTooltipMixin {
    @Shadow
    protected String text;

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/widgets/WTooltip;add(Lmeteordevelopment/meteorclient/gui/widgets/WWidget;)Lmeteordevelopment/meteorclient/gui/utils/Cell;"))
    public Cell<WVerticalList> addTail(WTooltip instance, WWidget wWidget) {
        WVerticalList info = instance.theme.verticalList();
        if (text.isBlank() || !text.contains("^CLASSNAME:")) {
            info.add(instance.theme.label(text));
            return instance.add(info).pad(4);
        }

        String[] splitDesc = text.split("\\^CLASSNAME:");

        info.add(instance.theme.label(splitDesc[0]));

        Cunny.LOG.info("Module {}, attempted to add tooltip info.", splitDesc[1]);

        String from = CompatibilityConfig.getAddedBy(splitDesc[1]);

        TextState format = new TextState();
        format.addPart("Added By: ", Color.LIGHT_GRAY);
        format.addPart(from, CompatibilityConfig.getColor(splitDesc[1]));

        WHorizontalList textSection = info.add(instance.theme.horizontalList()).expandX().widget();
        for (WLabel label : format.getParts(instance.theme)) {
            textSection.add(label);
        }

        return instance.add(info).pad(4);
    }
}
