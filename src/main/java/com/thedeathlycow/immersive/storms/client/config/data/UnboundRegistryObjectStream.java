package com.thedeathlycow.immersive.storms.client.config.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.stream.Stream;

public sealed interface UnboundRegistryObjectStream<T> permits UnboundRegistryObjectStream.Key, UnboundRegistryObjectStream.Tag {
    Stream<Holder<T>> listHolders(final HolderLookup<T> lookup);

    Either<TagKey<T>, ResourceKey<T>> toEither();

    static <T> UnboundRegistryObjectStream<T> fromEither(Either<TagKey<T>, ResourceKey<T>> either) {
        return either.map(Tag::new, Key::new);
    }

    static <T> Stream<Holder<T>> bindToHolders(
            final Stream<UnboundRegistryObjectStream<T>> objects,
            final HolderLookup<T> lookup
    ) {
        return objects.flatMap(o -> o.listHolders(lookup));
    }

    static <T> Codec<UnboundRegistryObjectStream<T>> codec(final ResourceKey<? extends Registry<T>> registryName) {
        return Codec.either(TagKey.hashedCodec(registryName), ResourceKey.codec(registryName))
                .xmap(UnboundRegistryObjectStream::fromEither, UnboundRegistryObjectStream::toEither);
    }

    record Key<T>(ResourceKey<T> key) implements UnboundRegistryObjectStream<T> {
        @Override
        public Stream<Holder<T>> listHolders(HolderLookup<T> lookup) {
            return Stream.of(lookup.getOrThrow(this.key()));
        }

        @Override
        public Either<TagKey<T>, ResourceKey<T>> toEither() {
            return Either.right(this.key);
        }
    }

    record Tag<T>(TagKey<T> tag) implements UnboundRegistryObjectStream<T> {
        @Override
        public Stream<Holder<T>> listHolders(HolderLookup<T> lookup) {
            return lookup.getOrThrow(this.tag).stream();
        }

        @Override
        public Either<TagKey<T>, ResourceKey<T>> toEither() {
            return Either.left(this.tag);
        }
    }
}