package com.nyfaria.numismaticoverhaul.currency;

import com.nyfaria.numismaticoverhaul.item.CoinItem;
import com.nyfaria.numismaticoverhaul.item.CurrencyItem;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

;

public class CurrencyHelper {

    /**
     * Checks how much money a player has as coin items in their inventory
     *
     * @param player The player to operate on (duh)
     * @param remove Whether to remove all coins from the player in the process
     * @return The amount of currency contained in the player's inventory
     */
    public static long getMoneyInInventory(Player player, boolean remove) {

        long value = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isCombined(stack)) continue;
            if (!(stack.getItem() instanceof CurrencyItem currencyItem)) continue;

            value += currencyItem.getValue(stack);

            if (remove) player.getInventory().removeItem(stack);
        }

        return value;
    }

    public static int getValue(List<ItemStack> stacks) {
        return stacks.stream().mapToInt(stack -> {
            if (stack == null) return 0;

            if (isCombined(stack)) return 0;
            if (!(stack.getItem() instanceof CurrencyItem currencyItem)) return 0;
            return (int) currencyItem.getValue(stack);
        }).sum();
    }

    public static void offerAsCoins(Player player, long value) {
        for (ItemStack itemStack : CurrencyConverter.getAsValidStacks(value)) {
            player.getInventory().placeItemBackInInventory(itemStack);
        }
    }

    public static boolean deduceFromInventory(Player player, long value) {
        long presentInInventory = getMoneyInInventory(player, false);
        if (presentInInventory < value) return false;

        getMoneyInInventory(player, true);

        offerAsCoins(player, presentInInventory - value);
        return true;
    }

    /**
     * Converts an amount of currency to a list of {@link ItemStack},
     * prefers {@link CoinItem} but may fall back to {@link MoneyBagItem}
     *
     * @param value     The currency value to convert
     * @param maxStacks The maximum amount of stacks the result may have
     * @return The List of {@link ItemStack}
     */
    public static List<ItemStack> getAsStacks(long value, int maxStacks) {

        List<ItemStack> stacks = new ArrayList<>();
        List<ItemStack> rawStacks = CurrencyConverter.getAsValidStacks(value);

        if (rawStacks.size() <= maxStacks) {
            stacks.addAll(rawStacks);
        } else {
            stacks.add(MoneyBagItem.create(value));
        }

        return stacks;
    }

    public static ItemStack getClosest(long value) {
        long[] values = CurrencyResolver.splitValues(value);

        for (int i = 0; i < 2; i++) {
            if (values[i + 1] == 0) break;
            values[i + 1] += Math.round(values[i] / 100f);
            values[i] = 0;
        }

        return CurrencyConverter.getAsItemStackList(CurrencyResolver.combineValues(values)).get(0);
    }

    public static long[] getFromNbt(CompoundTag nbt, String key) {
        if (nbt.contains(key, Tag.TAG_LONG_ARRAY)) return nbt.getLongArray(key);
        if (!nbt.contains(key, Tag.TAG_INT_ARRAY)) return new long[0];

        var intArray = nbt.getIntArray(key);
        var longArray = new long[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            longArray[i] = intArray[i];
        }

        return longArray;
    }

    private static boolean isCombined(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Combined", Tag.TAG_BYTE);
    }

}
