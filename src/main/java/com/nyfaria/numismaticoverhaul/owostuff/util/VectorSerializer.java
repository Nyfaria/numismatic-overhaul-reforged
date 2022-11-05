package com.nyfaria.numismaticoverhaul.owostuff.util;

import com.mojang.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for reading and storing {@link Vec3} and
 * {@link Vector3f} from and into {@link CompoundTag}
 */
public class VectorSerializer {

    /**
     * Stores the given vector  as an array at the
     * given key in the given nbt compound
     *
     * @param nbt   The nbt compound to serialize into
     * @param key   The key to use
     * @param vec3d The vector to serialize
     * @return {@code nbt}
     */
    public static CompoundTag put(CompoundTag nbt, String key, Vec3 vec3d) {

        ListTag vectorArray = new ListTag();
        vectorArray.add(DoubleTag.valueOf(vec3d.x));
        vectorArray.add(DoubleTag.valueOf(vec3d.y));
        vectorArray.add(DoubleTag.valueOf(vec3d.z));

        nbt.put(key, vectorArray);

        return nbt;
    }

    /**
     * Stores the given vector  as an array at the
     * given key in the given nbt compound
     *
     * @param vec3f The vector to serialize
     * @param nbt   The nbt compound to serialize into
     * @param key   The key to use
     * @return {@code nbt}
     */
    public static CompoundTag putf(CompoundTag nbt, String key, Vector3f vec3f) {

        ListTag vectorArray = new ListTag();
        vectorArray.add(FloatTag.valueOf(vec3f.x()));
        vectorArray.add(FloatTag.valueOf(vec3f.y()));
        vectorArray.add(FloatTag.valueOf(vec3f.z()));

        nbt.put(key, vectorArray);

        return nbt;
    }

    /**
     * Gets the vector stored at the given key in the
     * given nbt compound
     *
     * @param nbt The nbt compound to read from
     * @param key The key the read from
     * @return The deserialized vector
     */
    public static Vec3 get(CompoundTag nbt, String key) {

        ListTag vectorArray = nbt.getList(key, Tag.TAG_DOUBLE);
        double x = vectorArray.getDouble(0);
        double y = vectorArray.getDouble(1);
        double z = vectorArray.getDouble(2);

        return new Vec3(x, y, z);
    }

    /**
     * Gets the vector stored at the given key in the
     * given nbt compound
     *
     * @param nbt The nbt compound to read from
     * @param key The key the read from
     * @return The deserialized vector
     */
    public static Vector3f getf(CompoundTag nbt, String key) {

        ListTag vectorArray = nbt.getList(key, Tag.TAG_FLOAT);
        float x = vectorArray.getFloat(0);
        float y = vectorArray.getFloat(1);
        float z = vectorArray.getFloat(2);

        return new Vector3f(x, y, z);
    }

    /**
     * Writes the given vector into the given packet buffer
     *
     * @param vec3d  The vector to write
     * @param buffer The packet buffer to write into
     */
    public static void write(FriendlyByteBuf buffer, Vec3 vec3d) {
        buffer.writeDouble(vec3d.x);
        buffer.writeDouble(vec3d.y);
        buffer.writeDouble(vec3d.z);
    }

    /**
     * Writes the given vector into the given packet buffer
     *
     * @param vec3f  The vector to write
     * @param buffer The packet buffer to write into
     */
    public static void writef(FriendlyByteBuf buffer, Vector3f vec3f) {
        buffer.writeFloat(vec3f.x());
        buffer.writeFloat(vec3f.y());
        buffer.writeFloat(vec3f.z());
    }

    /**
     * Reads one vector from the given packet buffer
     *
     * @param buffer The buffer to read from
     * @return The deserialized vector
     */
    public static Vec3 read(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    /**
     * Reads one vector from the given packet buffer
     *
     * @param buffer The buffer to read from
     * @return The deserialized vector
     */
    public static Vector3f readf(FriendlyByteBuf buffer) {
        return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

}
