package io.github.cunnydevelopment.cunnyaddon.hud;

import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;

public class CunnyHud extends HudElement {
    public CunnyHud(HudElementInfo<?> info) {
        super(info);
        Hud.get().register(info);
    }
}
