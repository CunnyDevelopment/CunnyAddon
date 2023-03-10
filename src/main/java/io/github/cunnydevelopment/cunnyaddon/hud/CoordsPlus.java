package io.github.cunnydevelopment.cunnyaddon.hud;

import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.placeholders.TextParser;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.TextPart;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.TextState;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CoordsPlus extends CunnyHud {
    private final TextParser parser = new TextParser();
    private TextState state = null;
    public static final HudElementInfo<CoordsPlus> INFO = new HudElementInfo<>(Categories.HUD, "coords+", "A more traditional coords hud element.", CoordsPlus::new);
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<Boolean> showPos = sgDefault.add(new BoolSetting.Builder()
        .name("show-pos-text")
        .description("Shows \"Pos:\" text in-front of the coordinates.")
        .defaultValue(true)
        .onChanged((v) -> {
            if (Utils.canUpdate()) updateState();
            else state = null;
        })
        .build());
    public final Setting<Boolean> showOpposite = sgDefault.add(new BoolSetting.Builder()
        .name("show-opposite")
        .description("Shows the opposite position or none.")
        .defaultValue(true)
        .onChanged((v) -> {
            if (Utils.canUpdate()) updateState();
            else state = null;
        })
        .build());

    public CoordsPlus() {
        super(INFO);
        this.autoAnchors = false;
        this.box.xAnchor = XAnchor.Left;
        this.box.yAnchor = YAnchor.Top;
        this.box.updateAnchors();
        setSize(128, 64);
    }

    @EventHandler
    public void onScreenChange(OpenScreenEvent event) {
        state = null;
    }

    public void updateState() {
        state = new TextState();

        if (showPos.get()) state.addPart("Pos: ", Hud.get().textColors.get().get(0));

        state.addPart("<x>", Hud.get().textColors.get().get(1))
            .addPart(", ", Hud.get().textColors.get().get(0))
            .addPart("<y>", Hud.get().textColors.get().get(1))
            .addPart(", ", Hud.get().textColors.get().get(0))
            .addPart("<z>", Hud.get().textColors.get().get(1));

        if (showOpposite.get()) {
            assert mc.player != null;
            assert mc.world != null;
            if (!(!mc.world.getDimension().bedWorks() && !mc.world.getDimension().respawnAnchorWorks())) {
                state.addPart(" (", Hud.get().textColors.get().get(0))
                    .addPart("<opposite-x>", Hud.get().textColors.get().get(1))
                    .addPart(", ", Hud.get().textColors.get().get(0))
                    .addPart("<opposite-z>", Hud.get().textColors.get().get(1))
                    .addPart(")", Hud.get().textColors.get().get(0));
            }
        }
    }

    @Override
    public void render(HudRenderer renderer) {
        if (!Utils.canUpdate()) return;
        if (state == null) updateState();

        String str = "";

        for (TextPart part : state.getParts()) {
            String parsed = parser.parse(part.text);
            renderer.text(parsed, x + renderer.textWidth(str), y, part.color, false);
            str += parsed;
        }

        setSize(renderer.textWidth(str), renderer.textHeight());
    }
}
