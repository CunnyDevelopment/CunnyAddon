package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.ModuleReference;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.CompatibilityConfig;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Module.class, remap = false)
public abstract class ModuleMixin {
    @Mutable
    @Shadow
    @Final
    public String name;

    @Mutable
    @Shadow
    @Final
    public Category category;

    @Mutable
    @Shadow
    @Final
    public String title;

    @Shadow
    private boolean active;

    @Mutable
    @Shadow
    @Final
    public String description;

    @Inject(method = "toggle", at = @At("TAIL"))
    public void onToggle(CallbackInfo ci) {
        ModuleReference.STATES.put(((Module) (Object) this).getClass(), active);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/systems/modules/Module;name:Ljava/lang/String;", opcode = Opcodes.PUTFIELD))
    public void setName(Module instance, String value) {
        CompatibilityConfig.load();
        this.name = rename(instance, value);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/systems/modules/Module;title:Ljava/lang/String;", opcode = Opcodes.PUTFIELD))
    public void setTitle(Module instance, String value) {
        CompatibilityConfig.load();
        this.title = Utils.nameToTitle(name);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/systems/modules/Module;description:Ljava/lang/String;", opcode = Opcodes.PUTFIELD))
    public void setDescription(Module instance, String value) {
        this.description = value + "^CLASSNAME:" + instance.getClass().getPackageName();
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/systems/modules/Module;category:Lmeteordevelopment/meteorclient/systems/modules/Category;", opcode = Opcodes.PUTFIELD))
    public void setCategory(Module instance, Category value) {
        CompatibilityConfig.load();
        CompatibilityConfig.classes.add(instance.getClass().getName());
        if (CompatibilityConfig.recategorize.containsKey(instance.getClass().getName())) {
            this.category = Categories.get(CompatibilityConfig.recategorize.get(instance.getClass().getName()));
        } else {
            this.category = Categories.KNOWN_CATEGORIES.contains(value.name) ? Categories.getFromModule(instance, value.name) : Categories.UNKNOWN;
        }
    }

    public String rename(Module module, String str) {
        if (CompatibilityConfig.rename.containsKey(module.getClass().getName()))
            return CompatibilityConfig.rename.get(module.getClass().getName());
        if (Modules.get().get(str) != null) return rename(module, str + "+");
        else return str;
    }
}
