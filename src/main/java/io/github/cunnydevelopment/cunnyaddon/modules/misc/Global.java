package io.github.cunnydevelopment.cunnyaddon.modules.misc;

import io.github.cunnydevelopment.cunnyaddon.modules.CunnyModule;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import lombok.Getter;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public class Global extends CunnyModule {
    @Getter
    private static boolean strictPlacing = false;
    @Getter
    private static boolean rayCasting = false;
    @Getter
    private static double rayCastingDistance = 3.5;

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<Boolean> strictPlace = sgDefault.add(new BoolSetting.Builder()
        .name("strict-place")
        .description("Uses meteors directions instead of the custom anti double place ones.")
        .defaultValue(false)
        .onChanged(val -> strictPlacing = val)
        .build());
    public final Setting<Boolean> rayCast = sgDefault.add(new BoolSetting.Builder()
        .name("ray-cast")
        .description("Only place blocks if it can be ray-casted.")
        .defaultValue(false)
        .onChanged(val -> rayCasting = val)
        .build());
    public final Setting<Double> rayCastDist = sgDefault.add(new DoubleSetting.Builder()
        .name("ray-cast-distance")
        .description("The distance to ray-cast.")
        .sliderRange(1.5, 5.0)
        .defaultValue(3.5)
        .visible(rayCast::get)
        .onChanged(val -> rayCastingDistance = val)
        .build());


    public Global() {
        super(Categories.MISC, "global-config", "Global Cunny module options.");
        if (!isActive()) toggle();
        this.runInMainMenu = true;
    }


}
