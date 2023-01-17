package io.github.cunnydevelopment.cunnyaddon.utility;

import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;

public class RenderUtils {
    public static void renderBlock(Renderer3D render, BlockPos pos, Color... colors) {
        render.blockLines(pos.getX(), pos.getY(), pos.getZ(), colors[0], 0);
        render.blockSides(
            pos.getX(), pos.getY(), pos.getZ(), colors.length > 1 ? colors[1] : colors[0], 0);
    }
}
