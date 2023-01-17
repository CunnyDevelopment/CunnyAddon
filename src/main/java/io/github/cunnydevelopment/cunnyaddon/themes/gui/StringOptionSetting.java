package io.github.cunnydevelopment.cunnyaddon.themes.gui;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.function.Consumer;

public class StringOptionSetting extends Setting<String> {
    private List<String> values;

    public StringOptionSetting(String name, String description, String defaultValue, List<String> values, Consumer<String> onChanged, Consumer<Setting<String>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    protected String parseImpl(String str) {
        return str;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return values;
    }

    @Override
    public NbtCompound save(NbtCompound tag) {
        tag.putString("value", get());

        return tag;
    }

    @Override
    public String load(NbtCompound tag) {
        parse(tag.getString("value"));

        return get();
    }


    public static class Builder extends SettingBuilder<Builder, String, StringOptionSetting> {
        private List<String> values;

        public Builder() {
            super(null);
        }

        public Builder setValues(List<String> values) {
            this.values = values;
            return this;
        }

        @Override
        public StringOptionSetting build() {
            return new StringOptionSetting(name, description, defaultValue, values, onChanged, onModuleActivated, visible);
        }
    }
}
