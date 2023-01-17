package io.github.cunnydevelopment.cunnyaddon.utility.blocks;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import io.github.cunnydevelopment.cunnyaddon.utility.EntityUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.InventoryUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.PacketUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockUtils {
    private static final List<Direction> horizontals = List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST);
    private static final List<Direction> allDirs = List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.DOWN, Direction.UP);

    public static List<Direction> getHorizontals() {
        return horizontals;
    }

    public static List<Direction> getDirections() {
        return allDirs;
    }

    public static boolean isItem(BlockPos pos, Item... items) {
        return mc.player != null
            && Arrays.stream(items).toList().contains(mc.player.world.getBlockState(pos).getBlock().asItem());
    }

    public static boolean isItem(BlockPos pos, List<Item> items) {
        return mc.player != null
            && items.contains(mc.player.world.getBlockState(pos).getBlock().asItem());
    }

    //Misc
    public static boolean isContainer(BlockPos pos) {
        assert mc.player != null;
        Block block = mc.player.world.getBlockState(pos).getBlock();
        return mc.player != null
            && (mc.player.world.getBlockState(pos).getProperties().contains(Properties.CHEST_TYPE)
            || isShulker(mc.player.world.getBlockState(pos).getBlock())
            || block == Blocks.BARREL
            || block == Blocks.ENDER_CHEST
            || block == Blocks.HOPPER);
    }

    public static boolean isContainer(Block block) {
        return mc.player != null
            && (block == Blocks.CHEST
            || block == Blocks.TRAPPED_CHEST
            || isShulker(block)
            || block == Blocks.BARREL
            || block == Blocks.ENDER_CHEST
            || block == Blocks.HOPPER);
    }

    public static boolean isContainer(int x, int y, int z) {
        return isContainer(new BlockPos(x, y, z));
    }

    public static boolean isShulker(Block block) {
        return block.getTranslationKey().endsWith("shulker_box");
    }

    public static boolean isBed(BlockPos pos) {
        assert mc.player != null;
        return mc.player.world.getBlockState(pos).getBlock().getTranslationKey().endsWith("_bed");
    }

    public static boolean isBed(Block block) {
        return block.getTranslationKey().endsWith("_bed");
    }

    public static boolean isBlock(BlockPos pos, Block... blocks) {
        return mc.player != null
            && Arrays.stream(blocks).toList().contains(mc.player.world.getBlockState(pos).getBlock());
    }

    public static boolean isBlock(double x, double y, double z, Block... blocks) {
        return mc.player != null
            && Arrays.stream(blocks)
            .toList()
            .contains(mc.player.world.getBlockState(new BlockPos(x, y, z)).getBlock());
    }

    public static boolean isBlock(int x, int y, int z, Block... blocks) {
        return mc.player != null
            && Arrays.stream(blocks)
            .toList()
            .contains(mc.player.world.getBlockState(new BlockPos(x, y, z)).getBlock());
    }

    public static boolean canInstantMine(BlockPos pos) {
        assert mc.player != null;
        return mc.player.world.getBlockState(pos).getHardness(mc.player.world, pos) < 0.3;
    }

    public static boolean canExplode(BlockPos pos) {
        if (isReplaceable(pos)) return true;
        assert mc.player != null;
        return mc.player.getWorld().getBlockState(pos).getBlock().getBlastResistance() < 100;
    }

    public static boolean isReplaceable(BlockPos pos) {
        return mc.player != null && mc.player.getWorld().getBlockState(pos).isAir()
            || mc.player.getWorld().getBlockState(pos).getMaterial().isReplaceable();
    }

    public static boolean isLiquid(BlockPos pos) {
        return mc.player != null && mc.player.getWorld().getBlockState(pos).getMaterial().isLiquid();
    }

    public static BlockState getState(BlockPos pos) {
        assert mc.player != null;
        return mc.player.getWorld().getBlockState(pos);
    }

    public static boolean canUse(BlockPos pos) {
        Block block = getState(pos).getBlock();
        return isContainer(pos)
            || block instanceof ButtonBlock
            || block instanceof DoorBlock
            || block instanceof DragonEggBlock
            || block instanceof AnvilBlock
            || isBed(block);
    }

    // Crystal utilities
    public static boolean isAir(BlockPos pos) {
        return mc.player != null && mc.player.getWorld().getBlockState(pos).isAir();
    }

    public static boolean hasCrystal(BlockPos pos) {
        return mc.player != null
            && mc.player
            .getWorld()
            .getEntitiesByType(
                TypeFilter.instanceOf(EndCrystalEntity.class),
                Box.of(vec3d(pos), 1, 1, 1),
                entity -> entity.getBlockPos().equals(pos))
            .size()
            >= 1;
    }

    public static List<EndCrystalEntity> imposedCrystals(BlockPos pos) {
        assert mc.player != null;
        return mc.player.world.getEntitiesByType(EntityType.END_CRYSTAL, new Box(pos.add(3, 3, 3), pos.add(-3, -3, -3)), entity -> {
            assert mc.world != null;
            return entity.collidesWithStateAtPos(pos, Blocks.BEDROCK.getDefaultState()) || isOnSurround(entity);
        });
    }

    public static boolean isOnSurround(EndCrystalEntity crystal) {
        for (Direction direction : getHorizontals()) {
            assert mc.player != null;
            BlockPos offset = mc.player.getBlockPos().offset(direction);
            if (crystal.getBlockPos().equals(offset.offset(Direction.UP))
                || crystal.getBlockPos().equals(offset.offset(Direction.DOWN))) return true;
        }
        return false;
    }

    public static boolean canPlace(BlockPos pos) {
        assert mc.player != null;
        assert mc.world != null;
        List<Entity> entities = mc.player.world.getOtherEntities(null, new Box(pos.add(3, 3, 3), pos.add(-3, -3, -3)), entity -> {
            if (EntityUtils.canPlaceIn(entity)) {
                return false;
            }

            return entity.collidesWithStateAtPos(pos, Blocks.BEDROCK.getDefaultState());
        });
        return isReplaceable(pos) && entities.size() == 0;
    }

    public static boolean hasEntitiesInside(BlockPos pos) {
        List<Entity> entities = mc.player.world.getOtherEntities(null, new Box(pos.add(3, 3, 3), pos.add(-3, -3, -3)), entity -> {
            if (EntityUtils.canPlaceIn(entity)) {
                return false;
            }

            return entity.collidesWithStateAtPos(pos, Blocks.BEDROCK.getDefaultState());
        });
        return entities.size() != 0;
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        return isBlock(pos, Blocks.OBSIDIAN, Blocks.BEDROCK) && isAir(pos.offset(Direction.UP));
    }

    public static BlockPos getCevPos(PlayerEntity player) {
        BlockPos pos = player.getBlockPos().offset(Direction.UP);
        return null;
    }

    public static Direction getPlaceDirection(BlockPos pos) {
        for (Direction direction : getHorizontals()) {
            if (!isAir(pos.offset(direction))) return direction;
        }
        return Direction.DOWN;
    }

    public static BlockPos isOnEntity(BlockPos pos, Entity entity) {
        for (Direction dir : getDirections()) {
            if (pos.offset(dir).equals(entity.getBlockPos())) return pos.offset(dir);
            if (pos.offset(dir).offset(Direction.UP).equals(entity.getBlockPos().offset(Direction.UP)))
                return pos.offset(dir);
        }
        return pos;
    }

    public static boolean shouldAirPlace(BlockPos pos) {
        int i = 0;
        for (Direction direction : getDirections()) {
            BlockPos offset = pos.offset(direction);
            if (BlockUtils.isAir(offset)) i++;
        }
        return i >= 6;
    }

    public static Vec3d vec3d(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Vec3d clickOffset(BlockPos pos) {
        return new Vec3d(pos.getX() + Math.min(0.9f, Math.random()), pos.getY() + Math.min(0.9f, Math.random()), pos.getZ() + Math.min(0.9f, Math.random()));
    }

    public static boolean placeBlock(FindItemResult item, BlockPos pos, boolean packet, boolean airPlace) {
        return placeBlock(item, pos, packet, airPlace, false, false);
    }

    public static boolean placeBlock(FindItemResult item, BlockPos pos, boolean packet, boolean airPlace, boolean antiGhost) {
        return placeBlock(item, pos, packet, airPlace, antiGhost, false);
    }

    public static boolean placeBlock(FindItemResult item, BlockPos pos, boolean packet, boolean airPlace, boolean antiGhost, boolean silent) {
        assert mc.player != null;
        assert mc.interactionManager != null;
        if (canUse(pos)) {
            PacketUtils.send(new PlayerInputC2SPacket(mc.player.forwardSpeed, mc.player.sidewaysSpeed, mc.player.input.jumping, true));
            mc.player.setSneaking(true);
        }
        if (!(item.found() && item.slot() != -1 && item.count() > 0)) return false;
        Hand hand = Hand.MAIN_HAND;
        if (item.isOffhand()) hand = Hand.OFF_HAND;
        int prevSlot = mc.player.getInventory().selectedSlot;
        if (item.isHotbar()) {
            Cunny.LOG.info("Slot: {}", item.slot());
            InventoryUtils.swapSlot(item.slot(), silent);
        } else {
            int nextSlot = InventoryUtils.moveItemToHotbar(item, true);
            Cunny.LOG.info("Slot: {}, {}", item.slot(), nextSlot);
            InventoryUtils.swapSlot(nextSlot, silent);
        }

        if (airPlace && shouldAirPlace(pos)) {
            BlockPos airPos = isOnEntity(pos, mc.player);
            if (airPos == pos) airPos = pos.offset(Direction.DOWN);
            if (packet) {
                BlockHandling.swingHand();
                PacketUtils.send(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(clickOffset(airPos), getPlaceDirection(airPos), airPos, false), 0));
            } else {
                BlockHandling.swingHand();
                mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(clickOffset(airPos), getPlaceDirection(airPos), airPos, false));
                if (antiGhost) BlockHandling.addGhostBlock(airPos);
            }
        }

        if (packet) {
            BlockHandling.swingHand();
            PacketUtils.send(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(clickOffset(pos), getPlaceDirection(pos), pos, false), 0));
        } else {
            BlockHandling.swingHand();
            mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(clickOffset(pos), getPlaceDirection(pos), pos, false));
            if (antiGhost) BlockHandling.addGhostBlock(pos);
        }

        if (silent) {
            mc.player.getInventory().selectedSlot = prevSlot;
            InventoryUtils.syncHand();
        }

        if (canUse(pos)) {
            PacketUtils.send(new PlayerInputC2SPacket(mc.player.forwardSpeed, mc.player.sidewaysSpeed, mc.player.input.jumping, false));
            mc.player.setSneaking(false);
        }
        return true;
    }

    public enum PlaceMode {
        Packet,
        Vanilla
    }
}
