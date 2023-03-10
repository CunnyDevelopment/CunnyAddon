package io.github.cunnydevelopment.cunnyaddon.themes.gui;

import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.settings.StorageBlockListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;

public class CunnySettingsWidgetFactory extends DefaultSettingsWidgetFactory {

    public CunnySettingsWidgetFactory(GuiTheme theme) {
        super(theme);
    }

    public static void optionsW(WTable table, StringOptionSetting setting) {
        WDropdown<String> dropdown = table.add(GuiThemes.get().dropdown(setting.getValues().toArray(new String[0]), setting.get())).expandCellX().widget();
        dropdown.action = () -> setting.set(dropdown.get());
    }
}
