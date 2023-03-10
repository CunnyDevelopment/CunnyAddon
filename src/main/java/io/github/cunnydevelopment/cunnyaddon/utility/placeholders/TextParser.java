package io.github.cunnydevelopment.cunnyaddon.utility.placeholders;

import joptsimple.internal.Strings;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class TextParser {
    private Pattern variablePattern = Pattern.compile(":(sex|balls):");
    private final DecimalFormat df = new DecimalFormat("#.##");
    private final Map<String, ParseVariable> variables = new HashMap<>();
    public final ParseVariable nameVar = new ParseVariable("name",
        args -> mc.player != null ? mc.player.getGameProfile().getName() : "Unknown");
    public final ParseVariable xVar = new ParseVariable("x",
        args -> mc.player != null ? df.format(mc.player.getX()) : "0.00");
    public final ParseVariable yVar = new ParseVariable("y",
        args -> mc.player != null ? df.format(mc.player.getY()) : "0.00");
    public final ParseVariable zVar = new ParseVariable("z",
        args -> mc.player != null ? df.format(mc.player.getZ()) : "0.00");

    public final ParseVariable oXVar = new ParseVariable("opposite-x", args -> {
        if (mc.player == null) return "0.00";
        assert mc.world != null;
        if (!mc.world.getDimension().bedWorks() && !mc.world.getDimension().respawnAnchorWorks()) return "0.00";
        double pos = mc.player.getX();

        if (mc.world.getDimension().respawnAnchorWorks()) {
            pos /= 8;
        } else {
            pos *= 8;
        }

        return df.format(pos);
    });
    public final ParseVariable oZVar = new ParseVariable("opposite-z", args -> {
        if (mc.player == null) return "0.00";
        assert mc.world != null;
        if (!mc.world.getDimension().bedWorks() && !mc.world.getDimension().respawnAnchorWorks()) return "0.00";

        double pos = mc.player.getZ();

        if (mc.world.getDimension().respawnAnchorWorks()) {
            pos /= 8;
        } else {
            pos *= 8;
        }

        return df.format(pos);
    });

    public TextParser() {
        df.setRoundingMode(RoundingMode.FLOOR);
        variables(nameVar, xVar, yVar, zVar, oXVar, oZVar);
    }

    private void recompilePattern() {
        variablePattern = Pattern.compile("<(" + Strings.join(variables.keySet(), "|") + ")>");
    }

    public void variables(ParseVariable... vars) {
        for (ParseVariable variable : vars) {
            variables.put(variable.name(), variable);
        }
        recompilePattern();
    }

    public String parse(String str) {
        String newString = str;
        Matcher matcher = variablePattern.matcher(newString);

        while (matcher.find()) {
            String caseStr = matcher.group();
            caseStr = caseStr.replaceFirst("<", "").replaceFirst(">", "");

            newString = newString.replaceFirst("<" + caseStr + ">", variables.getOrDefault(caseStr, nameVar).get());
        }

        return newString;
    }
}
