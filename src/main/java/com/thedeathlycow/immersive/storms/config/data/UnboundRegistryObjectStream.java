package com.thedeathlycow.immersive.storms.config.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.function.Function;
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

    static <B extends ByteBuf, T> StreamCodec<B, UnboundRegistryObjectStream<T>> streamCodec(final ResourceKey<? extends Registry<T>> registryName) {
        return ByteBufCodecs.<B, TagKey<T>, ResourceKey<T>>either(TagKey.streamCodec(registryName), ResourceKey.streamCodec(registryName))
                .map(UnboundRegistryObjectStream::fromEither, UnboundRegistryObjectStream::toEither);
    }

    static <T> Codec<UnboundRegistryObjectStream<T>> codec(final ResourceKey<? extends Registry<T>> registryName) {
        return Codec.either(TagKey.hashedCodec(registryName), ResourceKey.codec(registryName))
                .xmap(UnboundRegistryObjectStream::fromEither, UnboundRegistryObjectStream::toEither);
    }

    record Key<T>(ResourceKey<T> key) implements UnboundRegistryObjectStream<T> {
        @Override
        public Stream<Holder<T>> listHolders(HolderLookup<T> lookup) {
            return lookup.get(this.key()).stream().map(Function.identity());
        }

        @Override
        public Either<TagKey<T>, ResourceKey<T>> toEither() {
            return Either.right(this.key);
        }
    }

    record Tag<T>(TagKey<T> tag) implements UnboundRegistryObjectStream<T> {
        @Override
        public Stream<Holder<T>> listHolders(HolderLookup<T> lookup) {
            return lookup.get(this.tag).map(HolderSet.ListBacked::stream).orElseGet(Stream::empty);
        }

        @Override
        public Either<TagKey<T>, ResourceKey<T>> toEither() {
            return Either.left(this.tag);
        }
    }
}