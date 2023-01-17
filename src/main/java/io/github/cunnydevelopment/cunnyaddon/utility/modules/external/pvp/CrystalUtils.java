package io.github.cunnydevelopment.cunnyaddon.utility.modules.external.pvp;

import io.github.cunnydevelopment.cunnyaddon.utility.EntityUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.blocks.BlockUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CrystalUtils {
    public static final List<Item> FALLBACK_BLOCKS = List.of(Items.ENDER_CHEST, Items.NETHERITE_BLOCK, Items.CRYING_OBSIDIAN);

    public static boolean isCentered(LivingEntity entity) {
        assert mc.player != null;
        Vec3d center = BlockUtils.vec3d(mc.player.getBlockPos());
        var x = Math.abs(center.getX() - entity.getX());
        var z = Math.abs(center.getZ() - entity.getZ());
        return x < 0.15 && z < 0.15;
    }

    public static void centerPlayer() {
        assert mc.player != null;
        Vec3d center = BlockUtils.vec3d(mc.player.getBlockPos());
        mc.player.setPosition(center.getX(), mc.player.getY(), center.getZ());
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
    }

    public static boolean isSurroundMissing() {
        for (Direction dir : BlockUtils.getHorizontals()) {
            assert mc.player != null;
            if (BlockUtils.isReplaceable(mc.player.getBlockPos().offset(dir))) return true;
        }
        return false;
    }

    public static int isPhased(LivingEntity player) {
        int i = 0;
        assert mc.player != null;
        assert mc.world != null;
        BlockPos pos = player.getBlockPos();
        if (!BlockUtils.isReplaceable(pos) && player.collidesWithStateAtPos(pos, mc.player.world.getBlockState(pos)))
            i++;
        for (Direction direction : EntityUtils.getHorizontals()) {
            BlockPos offset = pos.offset(direction);
            if (!BlockUtils.isReplaceable(offset) && player.collidesWithStateAtPos(offset, mc.player.world.getBlockState(offset))) {
                i++;
            }

            if (!BlockUtils.isReplaceable(offset.offset(Direction.UP)) && player.collidesWithStateAtPos(offset.offset(Direction.UP), mc.player.world.getBlockState(offset.offset(Direction.UP)))) {
                i++;
            }
        }

        return i;
    }
}
