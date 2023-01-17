package io.github.cunnydevelopment.cunnyaddon.utility.modules.external;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import meteordevelopment.discordipc.RichPresence;

import java.util.Objects;
import java.util.Random;

@Getter
public class RichPresenceExtra extends RichPresence {
    private PresenceType type = PresenceType.Playing;
    private final Random random = new Random();
    private boolean modified = true;
    private boolean players = false;
    private int maxPlayers;
    private int currentPlayers;
    private String imgKey, imgText, state, details, name = "Cunny Add-on";

    public void setName(String s) {
        if (!Objects.equals(s, name)) modified = true;
        name = s;
    }

    public void setType(PresenceType type) {
        if (type != this.type) modified = true;
        this.type = type;
    }

    public void showPlayers(boolean bool) {
        if (bool != players) modified = true;
        this.players = bool;
    }

    public void setMaxPlayers(int i) {
        if (i != maxPlayers) modified = true;
        this.maxPlayers = i;
    }

    public void setCurrentPlayers(int i) {
        if (i != currentPlayers) modified = true;
        this.currentPlayers = i;
    }

    @Override
    public void setDetails(String s) {
        if (!Objects.equals(s, details)) modified = true;
        details = s;
        super.setDetails(s);
    }

    @Override
    public void setState(String s) {
        if (!Objects.equals(s, state)) modified = true;
        state = s;
        super.setState(s);
    }

    @Override
    public void setLargeImage(String key, String text) {
        if (!Objects.equals(key, imgKey) || !Objects.equals(text, imgText)) modified = true;
        imgKey = key;
        imgText = text;
        super.setLargeImage(key, text);
    }

    @Override
    public void setSmallImage(String key, String text) {
        super.setSmallImage(key, text);
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();

        if (players) {
            JsonObject party = new JsonObject();
            party.addProperty("id", random.nextDouble() + "");
            JsonArray partySize = new JsonArray();
            partySize.add(currentPlayers);
            partySize.add(maxPlayers);
            party.add("size", partySize);
            object.add("party", party);
        }

        object.addProperty("type", convertType());

        JsonArray buttons = new JsonArray();
        JsonObject downloadButton = new JsonObject();

        downloadButton.addProperty("label", "Cunny Add-on");
        downloadButton.addProperty("url", "https://github.com/CunnyDevelopment/CunnyAddon");

        buttons.add(downloadButton);
        object.add("buttons", buttons);

        return object;
    }

    public void update() {
        modified = false;
    }

    public boolean isModified() {
        return modified;
    }

    private int convertType() {
        switch (type) {
            case Streaming -> {
                return 1;
            }

            case Listening -> {
                return 2;
            }

            default -> {
                return 0;
            }
        }
    }


    public enum PresenceType {
        Playing,
        Streaming,
        Listening,
        Watching,
        Competing
    }
}
