package com.nyfaria.numismaticoverhaul.owostuff.util;

import com.google.common.collect.ForwardingMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A simple utility for inserting values into Tags at runtime
 */
public class TagInjector {

    @ApiStatus.Internal
    public static final HashMap<TagLocation, Set<TagEntry>> ADDITIONS = new HashMap<>();


    private static final Map<TagLocation, Set<TagEntry>> ADDITIONS_VIEW = new ForwardingMap<>() {
        @Override
        protected @NotNull Map<TagLocation, Set<TagEntry>> delegate() {
            return Collections.unmodifiableMap(ADDITIONS);
        }

        @Override
        public Set<TagEntry> get(@Nullable Object key) {
            return Collections.unmodifiableSet(delegate().get(key));
        }
    };

    /**
     * Retrieves an unmodifiable map of all planned tag injections.
     *
     * @return An immutable view of the planned tag injections.
     */
    public static Map<TagLocation, Set<TagEntry>> getInjections() {
        return ADDITIONS_VIEW;
    }

    /**
     * Injects the given Identifiers into the given Tag.
     * If the Identifiers don't correspond to an entry in the
     * given Registry, you <i>will</i> break the Tag.
     * If the Tag does not exist, it will be created.
     *
     * @param registry   The registry for which the injected tags should apply
     * @param tag        The tag to insert into, this could contain all kinds of values
     * @param entryMaker The function to use for creating tag entries from the given identifiers
     * @param values     The values to insert
     */
    public static void injectRaw(Registry<?> registry, ResourceLocation tag, Function<ResourceLocation, TagEntry> entryMaker, Collection<ResourceLocation> values) {
        ADDITIONS.computeIfAbsent(new TagLocation(TagManager.getTagDir(registry.key()), tag), identifier -> new HashSet<>())
                .addAll(values.stream().map(entryMaker).toList());
    }

    public static void injectRaw(Registry<?> registry, ResourceLocation tag, Function<ResourceLocation, TagEntry> entryMaker, ResourceLocation... values) {
        injectRaw(registry, tag, entryMaker, Arrays.asList(values));
    }

    // -------

    /**
     * Injects the given values into the given tag, obtaining
     * their identifiers from the given registry
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     * @param <T>      The type of the target registry
     */
    public static <T> void inject(Registry<T> registry, ResourceLocation tag, Collection<T> values) {
        injectDirectReference(registry, tag, values.stream().map(registry::getKey).toList());
    }

    @SafeVarargs
    public static <T> void inject(Registry<T> registry, ResourceLocation tag, T... values) {
        inject(registry, tag, Arrays.asList(values));
    }

    // -------

    /**
     * Injects the given identifiers into the given tag
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     */
    public static void injectDirectReference(Registry<?> registry, ResourceLocation tag, Collection<ResourceLocation> values) {
        injectRaw(registry, tag, TagEntry::element, values);
    }

    public static void injectDirectReference(Registry<?> registry, ResourceLocation tag, ResourceLocation... values) {
        injectDirectReference(registry, tag, Arrays.asList(values));
    }

    // -------

    /**
     * Injects the given tags into the given tag,
     * effectively nesting them. This is equivalent to
     * prefixing an entry in the tag JSON's {@code values} array
     * with a {@code #}
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     */
    public static void injectTagReference(Registry<?> registry, ResourceLocation tag, Collection<ResourceLocation> values) {
        injectRaw(registry, tag, TagEntry::tag, values);
    }

    public static void injectTagReference(Registry<?> registry, ResourceLocation tag, ResourceLocation... values) {
        injectTagReference(registry, tag, Arrays.asList(values));
    }

    public record TagLocation(String type, ResourceLocation tagId) {}

}
