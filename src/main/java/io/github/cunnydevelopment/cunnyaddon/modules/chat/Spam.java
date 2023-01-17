package io.github.cunnydevelopment.cunnyaddon.modules.chat;

import io.github.cunnydevelopment.cunnyaddon.modules.CunnyModule;
import io.github.cunnydevelopment.cunnyaddon.utility.ArrayUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.PacketUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.StringUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spam extends CunnyModule {
    // Variables & Constants
    private static final Random RANDOM = new Random();
    private int ticks = 0;
    private int position = -1;

    // Messages
    private final SettingGroup sgMessages = settings.createGroup("Messages");
    public final Setting<Boolean> command = sgMessages.add(new BoolSetting.Builder()
        .name("command")
        .description("Whether or not to send all messages as a command.")
        .build());
    public final Setting<List<String>> messages = sgMessages.add(new StringListSetting.Builder()
        .name("messages")
        .description("The messages to send in chat.")
        .build());
    public final Setting<Mode> mode = sgMessages.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode for sending messages.")
        .defaultValue(Mode.Random)
        .onChanged(mode1 -> position = -1)
        .build());

    // Additional
    private final SettingGroup sgAdditional = settings.createGroup("Additional");
    public final Setting<Boolean> suffix = sgAdditional.add(new BoolSetting.Builder()
        .name("suffix")
        .description("Whether or not to add a suffix to messages.")
        .build());
    public final Setting<List<String>> suffixMessages = sgAdditional.add(new StringListSetting.Builder()
        .name("suffix-messages")
        .description("A list of suffixes.")
        .visible(suffix::get)
        .build());
    public final Setting<Boolean> prefix = sgAdditional.add(new BoolSetting.Builder()
        .name("prefix")
        .description("Whether or not to add a prefix to messages.")
        .build());
    public final Setting<List<String>> prefixMessages = sgAdditional.add(new StringListSetting.Builder()
        .name("prefix-messages")
        .description("A list of prefixes.")
        .visible(prefix::get)
        .build());
    public final Setting<Boolean> owoify = sgAdditional.add(new BoolSetting.Builder()
        .name("owoify")
        .description("Whether or not to OwOify the message.")
        .build());

    // Delays
    private final SettingGroup sgDelay = settings.createGroup("Delay");
    public final Setting<Integer> delay = sgDelay.add(new IntSetting.Builder()
        .name("delay-ticks")
        .description("The delay before sending another response, in ticks.")
        .defaultValue(25)
        .sliderRange(1, 80)
        .build());
    public final Setting<Boolean> randomize = sgDelay.add(new BoolSetting.Builder()
        .name("randomize")
        .description("Whether or not to apply a randomization factor.")
        .build());
    public final Setting<Integer> factor = sgDelay.add(new IntSetting.Builder()
        .name("factor")
        .description("The randomization factor, in ticks.")
        .defaultValue(25)
        .sliderRange(1, 80)
        .visible(randomize::get)
        .build());

    public Spam() {
        super(Categories.CHAT, "spam+", "Repeatedly send messages and or commands, Neko Tags are applied.");
    }

    @EventHandler
    public void onTick(TickEvent.Post tickEvent) {
        if (ticks > 0) {
            ticks--;
            return;
        }

        ticks = delay.get();
        if (randomize.get()) ticks += RANDOM.nextInt(0, factor.get());
        PacketUtils.chat(StringUtils.purifyText(getDecoratedMessage()), command.get());
    }

    public String getDecoratedMessage() {
        String msg = getPlainMessage();
        if (suffix.get()) {
            msg += " " + (suffixMessages.get().isEmpty() ? "Nico Nico Nii" : Objects.requireNonNull(ArrayUtils.random(suffixMessages.get())).strip());
        }

        if (prefix.get()) {
            msg = (prefixMessages.get().isEmpty() ? "UwU" : Objects.requireNonNull(ArrayUtils.random(prefixMessages.get())).strip()) + " " + msg;
        }

        if (owoify.get()) {
            msg = StringUtils.owoify(msg);
        }

        return msg;
    }

    private String getPlainMessage() {
        if (messages.get().isEmpty()) return "Neko On Top : <rwg>";

        switch (mode.get()) {
            case Ordinal -> {
                if (position == -1) position = 0;
                String message = messages.get().get(position);
                if (position + 1 >= messages.get().size()) {
                    position = 0;
                } else {
                    position++;
                }
                return message;
            }

            case Reverse -> {
                if (position == -1) position = messages.get().size() - 1;
                String message = messages.get().get(position);
                if (position - 1 < 0) {
                    position = messages.get().size() - 1;
                } else {
                    position--;
                }
                return message;
            }

            case RandomNoRepeat -> {
                int i = RANDOM.nextInt(messages.get().size());
                if (position == i) return getPlainMessage();
                position = i;
                return messages.get().get(i);
            }

            case Random -> {
                int i = RANDOM.nextInt(messages.get().size());
                position = i;
                return messages.get().get(i);
            }

            default -> {
                return "Nico Nico Nii : <rwg>";
            }
        }
    }

    public enum Mode {
        Ordinal,
        Reverse,
        RandomNoRepeat,
        Random
    }
}
