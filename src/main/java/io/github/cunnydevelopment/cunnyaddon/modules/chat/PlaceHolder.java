package io.github.cunnydevelopment.cunnyaddon.modules.chat;

import io.github.cunnydevelopment.cunnyaddon.modules.CunnyModule;
import io.github.cunnydevelopment.cunnyaddon.utility.ArrayUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.StringUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.ModuleInfo;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.TextState;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import java.util.*;

public class PlaceHolder extends CunnyModule {
    private final Map<String, List<String>> placeHolders = new HashMap<>();
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<List<String>> dummyList = sgDefault.add(new StringListSetting.Builder()
        .name("entries")
        .description("A list of placeholders.")
        .onChanged(this::onListChanged)
        .build());
    public final Setting<Boolean> strict = sgDefault.add(new BoolSetting.Builder()
        .name("strict")
        .description("Must be an exact string.")
        .defaultValue(false)
        .build());

    public PlaceHolder() {
        super(Categories.CHAT, "place-holder", "Allows to replace text with new text.");

        TextState format = new TextState();
        format.addPart("Format: ");
        format.addPart("placeholder@text", Color.CYAN);

        TextState warning = new TextState();
        warning.addPart("Supports placeholders with the same key", new Color(121, 241, 121));

        ModuleInfo.addInfo(this, format, warning);
    }

    public void onListChanged(List<String> list) {
        placeHolders.clear();
        addDefault();
        for (String str : list) {
            String newMessage = str;
            String id = "none";
            String[] strings = newMessage.split("@");
            if (strings.length > 0) {
                id = strings[0].toLowerCase();
                newMessage = newMessage.replaceFirst(strings[0] + "@", "");
            }

            add(id, newMessage);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onMessageSend(SendMessageEvent event) {
        String dummy = event.message;
        String newString = "";

        for (String msg : placeHolders.keySet()) {
            if (dummy.contains(msg)) {
                if (strict.get()) {
                    for (String message : dummy.strip().split(" ")) {
                        if (message.contains(msg)) {
                            newString = newString.equals("") ? message : newString + " " + message;
                        } else {
                            newString = "";
                        }

                        if (msg.equals(newString)) {
                            dummy = dummy.replaceFirst(msg, StringUtils.purifyText(Objects.requireNonNull(ArrayUtils.random(placeHolders.get(msg)))));
                        }
                    }
                } else {
                    while (dummy.contains(msg)) {
                        dummy = dummy.replaceFirst(msg, StringUtils.purifyText(Objects.requireNonNull(ArrayUtils.random(placeHolders.get(msg)))));
                    }
                }
            }
        }

        if (!dummy.equals(event.message)) {
            event.message = dummy;
        }
    }

    public void add(String key, String value) {
        if (!placeHolders.containsKey(key)) {
            placeHolders.put(key, new ArrayList<>());
        }
        placeHolders.get(key).add(value);
    }

    public void addDefault() {
        add(":skull:", "☠");
        add(":heart:", "♥");
        add(":heart_hollow:", "♡");
        add(":heart_big:", "❤");
        add(":heart!:", "❣");
        add(":kitty:", "子猫");
        add(":neko:", "猫");
        add(":god:", "Our lord and savior Neko.");
        add(":cross:", "✟");
        add(":cross:", "✞");
        add(":yes:", "✓");
        add(":no:", "✘");
        add(":left_brick:", "╘");
        add(":right_brick:", "╛");
        add(":squiggly:", "⸾");
    }
}
