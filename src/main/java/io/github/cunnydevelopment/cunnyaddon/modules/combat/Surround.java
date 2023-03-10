package io.github.cunnydevelopment.cunnyaddon.modules.combat;

import io.github.cunnydevelopment.cunnyaddon.events.MidTickEvent;
import io.github.cunnydevelopment.cunnyaddon.modules.CunnyModule;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.EntityUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.InventoryUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.RenderUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.blocks.BlockUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.pvp.CrystalUtils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class Surround extends CunnyModule {
    private final Map<BlockPos, VisualType> renderMap = Collections.synchronizedMap(new HashMap<>());
    private final List<BlockPos> savedBlocks = Collections.synchronizedList(new ArrayList<>());

    // Basic
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<TickType> tickType = sgDefault.add(new EnumSetting.Builder<TickType>()
        .name("tick-mode")
        .description("Which tick event to use.")
        .defaultValue(TickType.Pre)
        .build());
    public final Setting<Boolean> midTick = sgDefault.add(new BoolSetting.Builder()
        .name("mid-tick")
        .description("Executes code mid-tick.")
        .defaultValue(true)
        .build());
    public final Setting<BlockUtils.PlaceMode> placeMode = sgDefault.add(new EnumSetting.Builder<BlockUtils.PlaceMode>()
        .name("placing")
        .description("How to place blocks.")
        .defaultValue(BlockUtils.PlaceMode.Vanilla)
        .build());
    public final Setting<Integer> blocksPerTick = sgDefault.add(new IntSetting.Builder()
        .name("blocks-per-tick")
        .description("How many blocks to place per tick.")
        .sliderRange(1, 8)
        .defaultValue(2)
        .build());
    public final Setting<Integer> tickDelay = sgDefault.add(new IntSetting.Builder()
        .name("tick-delay")
        .description("Delay in ticks per cycle.")
        .sliderRange(0, 10)
        .defaultValue(0)
        .build());
    private final Setting<List<Item>> fallback = sgDefault.add(new ItemListSetting.Builder()
        .name("fallback-blocks")
        .description("The blocks to fall back to if you run out of obsidian.")
        .defaultValue(List.of(Items.ENDER_CHEST))
        .filter(CrystalUtils.FALLBACK_BLOCKS::contains)
        .build());
    public final Setting<Boolean> rotate = sgDefault.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically rotate to the placed block.")
        .defaultValue(true)
        .build());


    // Protection
    private final SettingGroup sgProtect = settings.createGroup("Protection");
    public final Setting<Boolean> protectUnder = sgProtect.add(new BoolSetting.Builder()
        .name("protect-under")
        .description("Places obsidian under the primary blocks if they are vulnerable.")
        .defaultValue(true)
        .build());
    public final Setting<Boolean> noConflict = sgProtect.add(new BoolSetting.Builder()
        .name("no-conflict")
        .description("Breaks crystals that could prevent the block from being replaced.")
        .defaultValue(true)
        .build());
    public final Setting<Boolean> autoReplace = sgProtect.add(new BoolSetting.Builder()
        .name("auto-replace")
        .description("Attempts to replace the crystal with obsidian.")
        .defaultValue(true)
        .visible(noConflict::get)
        .build());
    public final Setting<Boolean> saveReplaced = sgProtect.add(new BoolSetting.Builder()
        .name("save-replaced")
        .description("Saves the position and makes sure they're not broken.")
        .defaultValue(true)
        .visible(() -> autoReplace.get() && autoReplace.isVisible())
        .build());
    public final Setting<Boolean> markConflicted = sgProtect.add(new BoolSetting.Builder()
        .name("mark-as-saved")
        .description("Saves the position and makes sure they're not broken.")
        .defaultValue(true)
        .visible(() -> autoReplace.get() && autoReplace.isVisible() && saveReplaced.isVisible())
        .build());
    public final Setting<Integer> crystalTickDelay = sgProtect.add(new IntSetting.Builder()
        .name("break-delay")
        .description("The ticks between breaking crystals.")
        .sliderRange(0, 20)
        .defaultValue(3)
        .visible(noConflict::get)
        .build());

    // Toggles
    private final SettingGroup sgToggles = settings.createGroup("Toggles");
    public final Setting<Boolean> onGround = sgToggles.add(new BoolSetting.Builder()
        .name("on-ground")
        .description("Only place blocks on the ground.")
        .defaultValue(true)
        .build());

    // Render
    private final SettingGroup sgRender = settings.createGroup("Render");
    public final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Render block placements.")
        .defaultValue(true)
        .build());
    public final Setting<SettingColor> placedColor = sgRender.add(new ColorSetting.Builder()
        .name("placed")
        .defaultValue(new Color(0, 128, 0, 60))
        .build());
    public final Setting<SettingColor> waitingColor = sgRender.add(new ColorSetting.Builder()
        .name("waiting")
        .defaultValue(new Color(255, 165, 0, 60))
        .build());
    public final Setting<SettingColor> conflictingColor = sgRender.add(new ColorSetting.Builder()
        .name("conflicting")
        .defaultValue(new Color(255, 0, 0, 60))
        .build());
    public final Setting<SettingColor> failColor = sgRender.add(new ColorSetting.Builder()
        .name("fail")
        .defaultValue(new Color(255, 0, 0, 60))
        .build());
    private BlockPos currentHole = BlockPos.ORIGIN;
    private int ticks = 0;
    private int breakTicks = 0;

    public Surround() {
        super(Categories.COMBAT, "surround", "Surrounds your feet with blocks.");
    }

    @Override
    public void onActivate() {
        this.ticks = 0;
    }

    @EventHandler
    private void onTick(MidTickEvent event) {
        if (midTick.get()) tick();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (tickType.get() == TickType.Pre) tick();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (tickType.get() == TickType.Post) tick();
    }

    private void tick() {
        if (ticks > 0) {
            ticks--;
            return;
        }

        if (breakTicks > 0) breakTicks--;
        placeBlocks();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get()) return;
        try {
            synchronized (renderMap) {
                for (Map.Entry<BlockPos, VisualType> entry : renderMap.entrySet()) {
                    Color color;
                    switch (entry.getValue()) {
                        case PLACED -> color = placedColor.get();
                        case WAITING -> color = waitingColor.get();
                        case CONFLICTING -> color = conflictingColor.get();
                        case FAIL -> color = failColor.get();

                        default -> throw new IllegalStateException("Unexpected value: " + entry.getValue().name());
                    }
                    RenderUtils.renderBlock(event.renderer, entry.getKey(), color, color.copy().a(color.a + 20));
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    public void placeBlocks() {
        if (mc.player == null) return;
        if (onGround.get() && !mc.player.isOnGround()) return;

        var ref = new Object() {
            int blocks = 0;
        };
        ticks = tickDelay.get();

        BlockPos original = mc.player.getBlockPos();
        if (!currentHole.equals(original)) {
            currentHole = original;
            savedBlocks.clear();
        }

        try {
            synchronized (renderMap) {
                renderMap.clear();
            }
        } catch (ConcurrentModificationException ignored) {
        }


        for (Direction direction : EntityUtils.getHorizontals()) {
            BlockPos offset = original.offset(direction);
            if (noConflict.get()) {
                for (EndCrystalEntity crystal : BlockUtils.imposedCrystals(offset)) {
                    if (markConflicted.isVisible() && markConflicted.get()) {
                        if (!savedBlocks.contains(crystal.getBlockPos()))
                            savedBlocks.add(crystal.getBlockPos());
                    }
                    if (breakTicks > 0) break;
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, false));
                    breakTicks = crystalTickDelay.get();
                    if (autoReplace.get()) {
                        FindItemResult itemResult = InvUtils.find(itemStack -> itemStack.getItem() == Items.OBSIDIAN);
                        if (!itemResult.found())
                            itemResult = InventoryUtils.find(itemStack -> fallback.get().contains(itemStack.getItem()), false);
                        if (itemResult.found()) {
                            if (saveReplaced.get()) {
                                if (!savedBlocks.contains(crystal.getBlockPos()))
                                    savedBlocks.add(crystal.getBlockPos());
                            }
                            BlockUtils.placeBlock(itemResult, crystal.getBlockPos(), placeMode.get() == BlockUtils.PlaceMode.Packet, true, false, true);
                        }
                    }
                }
            }

            if (!BlockUtils.canExplode(offset)) {
                renderMap.put(offset, VisualType.PLACED);
                continue;
            }

            if (ref.blocks >= blocksPerTick.get()) {
                renderMap.put(offset, VisualType.WAITING);
                continue;
            }

            if (BlockUtils.isReplaceable(mc.player.getBlockPos().offset(Direction.DOWN))) {
                if (place(mc.player.getBlockPos().offset(Direction.DOWN))) ref.blocks++;
            }

            if (!CrystalUtils.isSurroundMissing()) continue;

            if (!CrystalUtils.isCentered(mc.player)) CrystalUtils.centerPlayer();

            if (place(offset)) ref.blocks++;

            if (protectUnder.get()) {
                BlockPos underPos = offset.offset(Direction.DOWN);
                if (place(underPos)) {
                    if (saveReplaced.get() && !savedBlocks.contains(underPos)) {
                        savedBlocks.add(underPos);
                    }
                    ref.blocks++;
                }
            }
        }

        if (saveReplaced.get() && ref.blocks < blocksPerTick.get()) {
            synchronized (savedBlocks) {
                for (BlockPos pos : savedBlocks) {
                    if (!BlockUtils.canExplode(pos)) {
                        renderMap.put(pos, VisualType.PLACED);
                        continue;
                    }

                    if (ref.blocks >= blocksPerTick.get()) {
                        renderMap.put(pos, VisualType.WAITING);
                        continue;
                    }

                    if (place(pos)) ref.blocks++;
                }
            }
        }
    }

    public boolean place(BlockPos pos) {
        if (BlockUtils.canPlace(pos)) {
            FindItemResult itemResult = InvUtils.find(itemStack -> itemStack.getItem() == Items.OBSIDIAN);
            if (!itemResult.found())
                itemResult = InvUtils.find(itemStack -> fallback.get().contains(itemStack.getItem()));
            if (!itemResult.found()) {
                renderMap.put(pos, VisualType.FAIL);
                return false;
            }

            if (rotate.get()) {
                Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), 1000);
            }

            if (BlockUtils.placeBlock(itemResult, pos, placeMode.get() == BlockUtils.PlaceMode.Packet, true, true, true)) {
                renderMap.put(pos, VisualType.PLACED);
                return true;
            } else {
                if (BlockUtils.hasEntitiesInside(pos)) {
                    renderMap.put(pos, VisualType.CONFLICTING);
                } else {
                    renderMap.put(pos, VisualType.FAIL);
                }
            }
        } else {
            if (BlockUtils.hasEntitiesInside(pos)) {
                renderMap.put(pos, VisualType.CONFLICTING);
            } else {
                renderMap.put(pos, VisualType.FAIL);
            }
        }
        return false;
    }

    public enum TickType {
        Pre,
        Post
    }

    public enum VisualType {
        PLACED,
        WAITING,
        CONFLICTING,
        FAIL
    }
}
