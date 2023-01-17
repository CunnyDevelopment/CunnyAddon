package io.github.cunnydevelopment.cunnyaddon.mixins.meteorclient;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ESPChunk.class)
public abstract class ESPChunkMixin {
    @Redirect(method = "searchChunk", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/modules/render/blockesp/ESPChunk;add(Lnet/minecraft/util/math/BlockPos;Z)V"))
    private static void logBlocks(ESPChunk instance, BlockPos blockPos, boolean update) {
        instance.add(blockPos, update);
        Cunny.LOG.info("<SEARCH> found block at {}, {}, {}", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
