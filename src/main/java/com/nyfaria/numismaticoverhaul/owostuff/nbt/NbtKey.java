package com.nyfaria.numismaticoverhaul.owostuff.nbt;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A utility class for serializing data into {@link CompoundTag}
 * instances. {@link Type} instances are used for holding the
 * actual serializer functions while the key itself carries information
 * about what string key to use.
 * <p>
 * In order to conveniently use instances of this class, employ the methods
 * defined on {@link NbtCarrier} - this is interface-injected onto
 * {@link ItemStack} and {@link CompoundTag} by default
 *
 * @param <T> The type of data a given instance can serialize
 */
public class NbtKey<T> {

    protected final String key;
    protected final Type<T> type;

    /**
     * Creates a new key instance used for storing data of type
     * {@code T} into NBT compounds with the given string as key
     *
     * @param key  The string key to use as index into the NBT compound
     * @param type The type object that holds the serializer implementations
     */
    public NbtKey(String key, Type<T> type) {
        this.key = key;
        this.type = type;
    }

    /**
     * @deprecated Use {@link NbtCarrier#get(NbtKey)} instead
     */
    @Deprecated
    public T get(@NotNull CompoundTag nbt) {
        return this.type.getter.apply(nbt, this.key);
    }

    /**
     * @deprecated Use {@link NbtCarrier#put(NbtKey, T)} instead
     */
    @Deprecated
    public void put(@NotNull CompoundTag nbt, T value) {
        this.type.setter.accept(nbt, this.key, value);
    }

    /**
     * @deprecated Use {@link NbtCarrier#delete(NbtKey)} instead
     */
    @Deprecated
    public void delete(@NotNull CompoundTag nbt) {
        nbt.remove(this.key);
    }

    /**
     * @deprecated Use {@link NbtCarrier#has(NbtKey)} instead
     */
    @Deprecated
    public boolean isIn(@NotNull CompoundTag nbt) {
        return nbt.contains(this.key, this.type.nbtEquivalent);
    }

    /**
     * A {@link NbtKey} used for serializing a {@link ListTag} of
     * the given type
     *
     * @param <T> The type of elements in the list
     */
    public static final class ListKey<T> extends NbtKey<ListTag> {

        private final Type<T> elementType;

        public ListKey(String key, Type<T> elementType) {
            super(key, null);
            this.elementType = elementType;
        }

        @Override
        public ListTag get(@NotNull CompoundTag nbt) {
            return nbt.getList(this.key, this.elementType.nbtEquivalent);
        }

        @Override
        public void put(@NotNull CompoundTag nbt, ListTag value) {
            nbt.put(this.key, value);
        }

        @Override
        public boolean isIn(@NotNull CompoundTag nbt) {
            return nbt.contains(this.key, Tag.TAG_LIST);
        }
    }

    /**
     * A container type holding serialization functions,
     * used for creating {@link NbtKey} instances
     */
    public static final class Type<T> {
        public static final Type<Byte> BYTE = new Type<>(Tag.TAG_BYTE, CompoundTag::getByte, CompoundTag::putByte);
        public static final Type<Short> SHORT = new Type<>(Tag.TAG_SHORT, CompoundTag::getShort, CompoundTag::putShort);
        public static final Type<Integer> INT = new Type<>(Tag.TAG_INT, CompoundTag::getInt, CompoundTag::putInt);
        public static final Type<Long> LONG = new Type<>(Tag.TAG_LONG, CompoundTag::getLong, CompoundTag::putLong);
        public static final Type<Float> FLOAT = new Type<>(Tag.TAG_FLOAT, CompoundTag::getFloat, CompoundTag::putFloat);
        public static final Type<Double> DOUBLE = new Type<>(Tag.TAG_DOUBLE, CompoundTag::getDouble, CompoundTag::putDouble);
        public static final Type<byte[]> BYTE_ARRAY = new Type<>(Tag.TAG_BYTE_ARRAY, CompoundTag::getByteArray, CompoundTag::putByteArray);
        public static final Type<String> STRING = new Type<>(Tag.TAG_STRING, CompoundTag::getString, CompoundTag::putString);
        public static final Type<CompoundTag> COMPOUND = new Type<>(Tag.TAG_COMPOUND, CompoundTag::getCompound, CompoundTag::put);
        public static final Type<int[]> INT_ARRAY = new Type<>(Tag.TAG_INT_ARRAY, CompoundTag::getIntArray, CompoundTag::putIntArray);
        public static final Type<long[]> LONG_ARRAY = new Type<>(Tag.TAG_LONG_ARRAY, CompoundTag::getLongArray, CompoundTag::putLongArray);
        public static final Type<ItemStack> ITEM_STACK = new Type<>(Tag.TAG_COMPOUND, Type::readItemStack, Type::writeItemStack);
        public static final Type<ResourceLocation> IDENTIFIER = new Type<>(Tag.TAG_STRING, Type::readIdentifier, Type::writeIdentifier);
        public static final Type<Boolean> BOOLEAN = new Type<>(Tag.TAG_BYTE, CompoundTag::getBoolean, CompoundTag::putBoolean);

        private final byte nbtEquivalent;
        private final BiFunction<CompoundTag, String, T> getter;
        private final TriConsumer<CompoundTag, String, T> setter;

        private Type(byte nbtEquivalent, BiFunction<CompoundTag, String, T> getter, TriConsumer<CompoundTag, String, T> setter) {
            this.nbtEquivalent = nbtEquivalent;
            this.getter = getter;
            this.setter = setter;
        }

        /**
         * Creates a new type that applies the given functions on top of
         * this type. This allows easily composing types by abstracting away
         * the underlying NBT compound
         *
         * @param getter The getter function to convert from this type's value type to the new one
         * @param setter The setter function to convert from the new value type to this type's one
         * @param <R>    The value type of the created type
         * @return The new key
         */
        public <R> Type<R> then(Function<T, R> getter, Function<R, T> setter) {
            return new Type<>(this.nbtEquivalent,
                    (compound, s) -> getter.apply(this.getter.apply(compound, s)),
                    (compound, s, r) -> this.setter.accept(compound, s, setter.apply(r)));
        }

        /**
         * Creates a new {@link Type} that supports reading and writing data of type {@code T}
         * into {@link CompoundTag} instances. Use this if you want to store data that is
         * not supported by the default provided types
         *
         * @param nbtType The type of NBT element that is used to represent the data,
         *                see {@link Tag} for the relevant constants
         * @param getter  The function used for writing objects to an {@code NbtCompound}
         * @param setter  The function used for reading objects from an {@code NbtCompound}
         * @param <T>     The type of data the created key can serialize
         * @return The created Type instance
         */
        public static <T> Type<T> of(byte nbtType, BiFunction<CompoundTag, String, T> getter, TriConsumer<CompoundTag, String, T> setter) {
            return new Type<>(nbtType, getter, setter);
        }

        /**
         * Creates a new type that serializes registry entries of the given
         * registry using their ID in string form
         *
         * @param registry The registry of which to serialize entries
         * @param <T>      The type of registry entry to serialize
         * @return The created type
         */
        public static <T> Type<T> ofRegistry(Registry<T> registry) {
            return new Type<>(Tag.TAG_STRING,
                    (compound, s) -> registry.get(new ResourceLocation(compound.getString(s))),
                    (compound, s, t) -> compound.putString(s, registry.getKey(t).toString()));
        }

        private static void writeItemStack(CompoundTag nbt, String key, ItemStack stack) {
            nbt.put(key, stack.save(new CompoundTag()));
        }

        private static ItemStack readItemStack(CompoundTag nbt, String key) {
            return nbt.contains(key, Tag.TAG_COMPOUND) ? ItemStack.of(nbt.getCompound(key)) : ItemStack.EMPTY;
        }

        private static void writeIdentifier(CompoundTag nbt, String key, ResourceLocation identifier) {
            nbt.putString(key, identifier.toString());
        }

        private static ResourceLocation readIdentifier(CompoundTag nbt, String key) {
            return nbt.contains(key, Tag.TAG_STRING) ? new ResourceLocation(nbt.getString(key)) : null;
        }
    }

}