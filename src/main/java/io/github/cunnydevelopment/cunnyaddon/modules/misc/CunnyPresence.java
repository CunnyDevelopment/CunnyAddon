package io.github.cunnydevelopment.cunnyaddon.modules.misc;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import io.github.cunnydevelopment.cunnyaddon.modules.CunnyModule;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.RichPresenceExtra;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

public class CunnyPresence extends CunnyModule {
    // Core
    private final SettingGroup sgCore = settings.createGroup("Core");
    public final Setting<Integer> tickDelay = sgCore.add(new IntSetting.Builder()
        .name("ticks")
        .description("How many ticks to wait per update.")
        .sliderRange(60, 160)
        .defaultValue(80)
        .build());
    // Image
    private final SettingGroup sgImage = settings.createGroup("Image");
    public final Setting<String> imageKey = sgImage.add(new StringSetting.Builder()
        .name("image-key")
        .description("Set the image key.")
        .defaultValue("https://images-ext-2.discordapp.net/external/yCitMxL7hePIjIsLhqDEu5yGJrBMO4zq7eJIuvbPD8k/https/pa1.narvii.com/6740/71030feb6d1c2a315c6f66cb8c4e49ace76fb763_hq.gif")
        .build());
    public final Setting<String> imageText = sgImage.add(new StringSetting.Builder()
        .name("image-text")
        .description("Set the image text.")
        .defaultValue("uuooohhhh cunnnyyyy")
        .build());
    // Text
    private final SettingGroup sgText = settings.createGroup("Text");
    public final Setting<String> firstLine = sgText.add(new StringSetting.Builder()
        .name("first-line")
        .description("Set the status state.")
        .defaultValue("Having sex with your mother")
        .build());
    public final Setting<String> secondLine = sgText.add(new StringSetting.Builder()
        .name("second-line")
        .description("Set the status description.")
        .defaultValue("in a wendies parking lot.")
        .build());
    // Other
    private final SettingGroup sgOther = settings.createGroup("Other");
    public final Setting<Party> state = sgOther.add(new EnumSetting.Builder<Party>()
        .name("state")
        .description("The state of how players are displayed.")
        .defaultValue(Party.Disabled)
        .build());
    public final Setting<Integer> currentPlayers = sgOther.add(new IntSetting.Builder()
        .name("current-players")
        .description("Fixed current player count.")
        .sliderRange(1, 10000)
        .defaultValue(69)
        .visible(() -> state.get() == Party.Fixed)
        .build());
    public final Setting<Integer> maxPlayers = sgOther.add(new IntSetting.Builder()
        .name("max-players")
        .description("Fixed max player count.")
        .sliderRange(1, 10000)
        .defaultValue(69)
        .visible(() -> state.get() == Party.Fixed)
        .onChanged(this::fixCount)
        .build());

    // Variables
    private final RichPresenceExtra presence = new RichPresenceExtra();
    private final long start = System.currentTimeMillis() / 1000L;
    private boolean wasModified = false;
    private boolean forceUpdate = false;
    private int ticks = 0;

    public CunnyPresence() {
        super(Categories.MISC, "cunny-presence", "Discord presence with additional features.");
        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        super.onActivate();
        forceUpdate = true;
        start();
        update(true);
    }

    @EventHandler
    private void onTick(TickEvent.Post tickEvent) {
        if (!Utils.canUpdate()) return;

        if (ticks > 0) {
            ticks--;
            return;
        }

        update(false);
        if (wasModified) {
            presence.update();
            wasModified = false;
        }

        ticks = tickDelay.get();
    }

    public void fixCount(int i) {
        if (i < currentPlayers.get()) {
            maxPlayers.set(currentPlayers.get());
        }
    }

    public void start() {
        DiscordIPC.start(1057465292109926511L, null);
        DiscordIPC.setOnError((integer, s) -> Cunny.LOG.error("Cunny Presence error: {}", s));
        presence.setStart(start);
        DiscordIPC.setActivity(presence);
        forceUpdate = true;
    }

    public void update(boolean first) {
        if (first) {
            presence.setLargeImage(imageKey.get(), imageText.get());
            presence.setState(firstLine.get());
            presence.setDetails(secondLine.get());
            wasModified = true;
            return;
        }

        if (!presence.getImgKey().equals(imageKey.get()) || !presence.getImgText().equals(imageText.get()) || forceUpdate) {
            presence.setLargeImage(imageKey.get(), imageText.get());
            wasModified = true;
        }

        if (state.get() == Party.Disabled) {
            if (presence.isPlayers()) {
                presence.showPlayers(false);
                wasModified = true;
            }

            if (!presence.getState().equals(secondLine.get()) || forceUpdate) {
                presence.setState(secondLine.get());
                wasModified = true;
            }

            if (!presence.getDetails().equals(firstLine.get()) || forceUpdate) {
                presence.setDetails(firstLine.get());
                wasModified = true;
            }
        } else {
            if (!presence.isPlayers()) {
                presence.showPlayers(true);
                wasModified = true;
            }

            if (state.get() == Party.Fixed) {
                presence.setCurrentPlayers(currentPlayers.get());
                presence.setMaxPlayers(maxPlayers.get());
            }

            if (!presence.getState().equals(firstLine.get()) || forceUpdate) {
                presence.setState(firstLine.get());
                wasModified = true;
            }

            if (!presence.getDetails().equals(secondLine.get()) || forceUpdate) {
                presence.setDetails(secondLine.get());
                wasModified = true;
            }
        }

        if (presence.isModified()) {
            wasModified = false;
            presence.update();
            DiscordIPC.setActivity(presence);
        }

        forceUpdate = false;
    }

    @EventHandler
    public void onScreenOpen(OpenScreenEvent event) {
        update(false);
        if (presence.isModified()) {
            wasModified = false;
            presence.update();
            DiscordIPC.setActivity(presence);
        }
    }

    public enum Party {
        Fixed,
        Disabled
    }
}
