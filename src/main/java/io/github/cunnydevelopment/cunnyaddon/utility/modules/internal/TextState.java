package io.github.cunnydevelopment.cunnyaddon.utility.modules.internal;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

public class TextState {
    public List<TextPart> parts = new ArrayList<>();

    public void addPart(String text) {
        parts.add(new TextPart(text));
    }

    public void addPart(String text, Color color) {
        parts.add(new TextPart(text, color));
    }

    public List<WLabel> getParts(GuiTheme theme) {
        List<WLabel> labels = new ArrayList<>();
        for (TextPart part : parts) {
            WLabel label = theme.label(part.text);
            label.color = part.color;
            labels.add(label);
        }
        return labels;
    }
}
