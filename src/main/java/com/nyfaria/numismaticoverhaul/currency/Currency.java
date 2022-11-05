package com.nyfaria.numismaticoverhaul.currency;

import com.nyfaria.numismaticoverhaul.init.ItemInit;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public enum Currency implements ItemLike {
    BRONZE {
        @Override
        public int getNameColor() {
            return 0xae5b3c;
        }

        @Override
        public long getRawValue(long amount) {
            return amount;
        }

        @Override
        public Item asItem() {
            return ItemInit.BRONZE_COIN.get();
        }
    }, SILVER {
        @Override
        public int getNameColor() {
            return 0x617174;
        }

        @Override
        public long getRawValue(long amount) {
            return amount * 100;
        }

        @Override
        public Item asItem() {
            return ItemInit.SILVER_COIN.get();
        }
    }, GOLD {
        @Override
        public int getNameColor() {
            return 0xbd9838;
        }

        @Override
        public long getRawValue(long amount) {
            return amount * 10000;
        }

        @Override
        public Item asItem() {
            return ItemInit.GOLD_COIN.get();
        }
    };

    public abstract int getNameColor();

    public abstract long getRawValue(long amount);
}
