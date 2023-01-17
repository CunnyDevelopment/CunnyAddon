package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.ModuleReference;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.CompatibilityConfig;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.ModuleInfo;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.TextState;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.screens.ModuleScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = ModuleScreen.class, remap = false)
public abstract class ModuleScreenMixin extends WindowScreen {
    @Shadow
    @Final
    private Module module;

    @Shadow
    public abstract boolean toClipboard();

    public ModuleScreenMixin(GuiTheme theme, WWidget icon, String title) {
        super(theme, icon, title);
    }

    @Redirect(method = "initWidgets", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/systems/modules/Module;description:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    public String getDescription(Module instance) {
        return instance.description.split("\\^CLASSNAME:")[0];
    }

    @Inject(method = "initWidgets", at = @At(
        value = "INVOKE",
        target = "Lmeteordevelopment/meteorclient/gui/screens/ModuleScreen;add(Lmeteordevelopment/meteorclient/gui/widgets/WWidget;)Lmeteordevelopment/meteorclient/gui/utils/Cell;",
        shift = At.Shift.BY, by = -1, ordinal = 4
    )
    )
    private void injectNekoOptions(CallbackInfo ci) {
        WSection section = add(theme.section("Extra", true)).expandX().widget();

        WHorizontalList ib = section.add(theme.horizontalList()).widget();

        ib.add(theme.label("Inverted Bind: "));
        WCheckbox invertedBind = ib.add(theme.checkbox(ModuleReference.isInvertedBind(module))).widget();
        invertedBind.action = () -> {
            if (invertedBind.checked) {
                if (!ModuleReference.INVERTED_BIND.contains(module)) {
                    ModuleReference.INVERTED_BIND.add(module);
                }
            } else {
                ModuleReference.INVERTED_BIND.remove(module);
            }
        };

        WHorizontalList tol = section.add(theme.horizontalList()).widget();

        tol.add(theme.label("Toggle on Leave: "));
        WCheckbox toggleOnLeave = tol.add(theme.checkbox(ModuleReference.isToggledOnLeave(module))).widget();
        toggleOnLeave.action = () -> {
            if (toggleOnLeave.checked) {
                if (!ModuleReference.TOGGLE_ON_LEAVE.contains(module)) {
                    ModuleReference.TOGGLE_ON_LEAVE.add(module);
                }
            } else {
                ModuleReference.TOGGLE_ON_LEAVE.remove(module);
            }
        };

        WHorizontalList rnm = section.add(theme.horizontalList()).widget();

        rnm.add(theme.label("Rename: "));
        WTextBox renameModule = rnm.add(theme.textBox(ModuleReference.getModuleName(module), module.title)).minWidth(180).expandX().widget();
        renameModule.action = () -> {
            if (!Objects.equals(renameModule.get(), "")) {
                ModuleReference.MODULE_RENAME.put(module, renameModule.get());
                ((ModuleAccessor) module).setTitle(renameModule.get());
            } else {
                ModuleReference.MODULE_RENAME.remove(module);
                ((ModuleAccessor) module).setTitle(Utils.nameToTitle(module.name));
            }
        };

        if (ModuleInfo.hasInfo(module)) {
            WSection section2 = add(theme.section("Info", false)).expandX().widget();

            WVerticalList info = section2.add(theme.verticalList()).widget();

            for (TextState text : ModuleInfo.getInfo(module)) {
                WHorizontalList textSection = info.add(theme.horizontalList()).expandX().widget();
                for (WLabel label : text.getParts(theme)) {
                    textSection.add(label);
                }
            }
        }
    }
}
