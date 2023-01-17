package io.github.cunnydevelopment.cunnyaddon.utility;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * The type Inv utils.
 */
public class InventoryUtils {
    public static final Predicate<ItemStack> IS_BLOCK = (itemStack) -> Item.BLOCK_ITEMS.containsValue(itemStack.getItem());

    public static FindItemResult find(Predicate<ItemStack> predicate, boolean hotbar) {
        if (hotbar) return InvUtils.findInHotbar(predicate);
        return InvUtils.find(predicate);
    }

    /**
     * Is wool boolean.
     *
     * @param item the item
     * @return the boolean
     */
    // Bed Utility
    public static boolean isWool(Item item) {
        return item.getTranslationKey().endsWith("_wool");
    }

    /**
     * Is plank boolean.
     *
     * @param item the item
     * @return the boolean
     */
    public static boolean isPlank(Item item) {
        return item.getTranslationKey().endsWith("_planks");
    }

    /**
     * Is bed boolean.
     *
     * @param item the item
     * @return the boolean
     */
    public static boolean isBed(Item item) {
        return item.getTranslationKey().endsWith("_bed");
    }

    /**
     * Is sword boolean.
     *
     * @param item the item
     * @return the boolean
     */
    public static boolean isSword(Item item) {
        return item.getTranslationKey().endsWith("_sword");
    }

    /**
     * Sync hand.
     *
     * @param i the
     */
    public static void syncHand(int i) {
        assert mc.player != null;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
    }

    /**
     * Sync hand.
     */
    public static void syncHand() {
        assert mc.player != null;
        syncHand(mc.player.getInventory().selectedSlot);
    }

    /**
     * Synchronize the clients inventory with the server.
     */
    public static void syncInv() {
        assert mc.player != null;
        mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(mc.player.currentScreenHandler.syncId,
            mc.player.currentScreenHandler.getRevision(),
            -1,
            0,
            SlotActionType.CLONE,
            ItemStack.EMPTY,
            new Int2ObjectArrayMap<>()));
    }

    /**
     * Swap slot.
     *
     * @param itemResult the item result
     * @param silent     the silent
     */
    public static void swapSlot(FindItemResult itemResult, boolean silent) {
        assert mc.player != null;
        if (itemResult.found() && itemResult.getHand() == null && itemResult.isHotbar()) {
            swapSlot(itemResult.slot(), silent);
        }
    }

    /**
     * Swap slot.
     *
     * @param i      the
     * @param silent the silent
     */
    public static void swapSlot(int i, boolean silent) {
        assert mc.player != null;
        if (mc.player.getInventory().selectedSlot != i) {
            if (!silent) mc.player.getInventory().selectedSlot = i;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
        }
    }

    /**
     * Move item to hotbar int.
     *
     * @param itemResult  the item result
     * @param preferEmpty the prefer empty
     * @return the int
     */
    public static int moveItemToHotbar(FindItemResult itemResult, boolean preferEmpty) {
        if (itemResult.found()) {
            if (itemResult.getHand() != null || itemResult.isHotbar()) return itemResult.slot();
            if (itemResult.isMain()) {
                assert mc.player != null;
                int emptySlot = 8;
                if (preferEmpty) {
                    emptySlot = findEmptySlotInHotbar(emptySlot);
                }

                assert mc.interactionManager != null;
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    itemResult.slot(),
                    emptySlot,
                    SlotActionType.SWAP,
                    mc.player);
                return emptySlot;
            }
        }
        return -1;
    }

    public static int moveItemToHotbar(int slot, boolean preferEmpty) {
        if (slot != -1) {
            if (slot < 35) {
                assert mc.player != null;
                int emptySlot = mc.player.getInventory().selectedSlot;
                if (preferEmpty) {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.getInventory().main.get(i).isEmpty()) {
                            emptySlot = i;
                            break;
                        }
                    }
                }

                assert mc.interactionManager != null;
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    slot,
                    emptySlot,
                    SlotActionType.SWAP,
                    mc.player);
                return emptySlot;
            }
        }
        return slot;
    }

    /**
     * Find the true slot (can be used in packets)
     *
     * @param items the items
     * @return the int
     */
    public static int findSlotInMain(Item... items) {
        if (mc.player != null) {
            for (var ref = new Object() {
                int i = 9;
            }; ref.i < 36; ref.i++) {
                if (Arrays.stream(items).anyMatch(item -> item == mc.player.getInventory().getStack(ref.i).getItem())) {
                    return ref.i;
                }
            }
        }
        return -1;
    }

    /**
     * Find a non-pure hotbar slot
     *
     * @param items A list of items to search for
     * @return The hotbar slot
     */
    public static int findSlotInHotbar(Item... items) {
        if (mc.player != null) {
            for (var ref = new Object() {
                int i = 0;
            }; ref.i < 9; ref.i++) {
                if (Arrays.stream(items).anyMatch(item -> item == mc.player.getInventory().getStack(getHotbarOffset() + ref.i).getItem())) {
                    return ref.i;
                }
            }
        }
        return -1;
    }

    public static int findEmptySlotInHotbar(int i) {
        if (mc.player != null) {
            for (var ref = new Object() {
                int i = 0;
            }; ref.i < 9; ref.i++) {
                if (mc.player.getInventory().getStack(getHotbarOffset() + ref.i).isEmpty()) {
                    return ref.i;
                }
            }
        }
        return i;
    }

    public static int getHotbarId(int slot) {
        assert mc.player != null;
        return mc.player.currentScreenHandler.slots.get(getHotbarOffset() + slot).id;
    }

    public static boolean isHotbarSlot(int slot) {
        if (slot < 0) return false;
        if (slot < 9) {
            return true;
        }

        return slot > getHotbarOffset();
    }

    public static int getInventoryOffset() {
        switch (Objects.requireNonNull(mc.player).currentScreenHandler.slots.size()) {
            case 39:
                return 3;
            case 38:
                return 2;
            case 41:
                return 5;
            case 46: {
                //For some reason crafting screen is offset by 1
                if (mc.player.currentScreenHandler instanceof CraftingScreenHandler) {
                    return 10;
                }

                return 9;
            }
            case 40:
                return 4;
            case 37:
                return 1;
            case 63:
                return 27;
            case 90:
                return 54;
            case 45:
                return 9;
        }
        return 0;
    }

    public static int getHotbarOffset() {
        return getInventoryOffset() + 27;
    }

    /**
     * Find the true slot (can be used in packets)
     *
     * @param items A list of items to search for
     * @return The inventory slot
     */
    public static int findSlot(Item... items) {
        if (mc.player != null) {
            DefaultedList<Slot> slots = mc.player.currentScreenHandler.slots;
            for (var ref = new Object() {
                int i = getInventoryOffset();
            }; ref.i < slots.size(); ref.i++) {
                if (Arrays.stream(items).anyMatch(item -> slots.get(ref.i).hasStack() && item == slots.get(ref.i).getStack().getItem())) {
                    return mc.player.currentScreenHandler.slots.get(ref.i).id;
                }
            }
        }
        return -1;
    }

    /**
     * Find the true slot (can be used in packets)
     *
     * @param name The item name to look for.
     * @return The inventory slot
     */
    public static int findSlot(String name) {
        return findSlot(name, false);
    }

    /**
     * Find the true slot (can be used in packets)
     *
     * @param name     The item name to look for.
     * @param contains if to look for items that names contain the name
     * @return The inventory slot
     */
    public static int findSlot(String name, boolean contains) {
        if (mc.player != null) {
            DefaultedList<Slot> slots = mc.player.currentScreenHandler.slots;
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i).hasStack()) {
                    if ((contains && !slots.get(i).getStack().getName().getString().contains(name))
                        || (!contains && !slots.get(i).getStack().getName().getString().equalsIgnoreCase(name)))
                        continue;
                    return mc.player.currentScreenHandler.slots.get(i).id;
                }
            }
        }
        return -1;
    }

    /**
     * Find blocks in hotbar int.
     *
     * @return the int
     */
    public static int findBlocksInHotbar() {
        var ref1 = new Object() {
            int i = -1;
        };
        if (mc.player != null) {
            for (var ref = new Object() {
                int i = 0;
            }; ref.i < mc.player.currentScreenHandler.slots.size() - 1; ref.i++) {
                if (mc.player.currentScreenHandler.slots.get(ref.i).getStack().getUseAction() == UseAction.BLOCK) {
                    ref1.i = mc.player.currentScreenHandler.slots.get(ref.i).id;
                }
            }
        }
        return ref1.i;
    }

    /**
     * Move item.
     *
     * @param from the from
     * @param to   the to
     */
    public static void moveItem(int from, int to) {
        assert mc.player != null;
        PlayerScreenHandler handler = mc.player.playerScreenHandler;
        ClientPlayerInteractionManager interact = mc.interactionManager;
        assert interact != null;
        interact.clickSlot(handler.syncId, from, 0, SlotActionType.PICKUP, mc.player);
        interact.clickSlot(handler.syncId, to, 0, SlotActionType.PICKUP, mc.player);
        if (!handler.getCursorStack().isEmpty()) {
            interact.clickSlot(handler.syncId, from, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    /**
     * Click slot.
     *
     * @param slot   the slot
     * @param button the button
     * @param action the action
     */
    public static void clickSlot(int slot, int button, SlotActionType action) {
        assert mc.player != null;
        ScreenHandler handler = mc.player.currentScreenHandler;
        if (slot > mc.player.getInventory().size() || slot < 0) handler.setCursorStack(ItemStack.EMPTY);
        else if (mc.player.getInventory().getStack(slot).isEmpty()) handler.setCursorStack(ItemStack.EMPTY);
        else handler.setCursorStack(mc.player.getInventory().getStack(slot));

        assert mc.interactionManager != null;
        mc.interactionManager.clickSlot(
            handler.syncId,
            slot,
            button,
            action,
            mc.player);
    }

    /**
     * Gets offhand.
     *
     * @return the offhand
     */
    public static Item getOffhand() {
        assert mc.player != null;
        return mc.player.getOffHandStack().getItem();
    }

    /**
     * Gets main hand.
     *
     * @return the main hand
     */
    public static Item getMainHand() {
        assert mc.player != null;
        return mc.player.getInventory().getMainHandStack().getItem();
    }
}
