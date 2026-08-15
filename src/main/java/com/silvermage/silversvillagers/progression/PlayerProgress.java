package com.silvermage.silversvillagers.progression;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;

public record PlayerProgress(boolean firstScrollGranted, List<Identifier> unlockedPlayerFeatures) {
	// firstScrollGranted is retained so existing worlds can still deserialize their saved data.
	public static final Codec<PlayerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("first_scroll_granted", false).forGetter(PlayerProgress::firstScrollGranted),
			Identifier.CODEC.listOf().optionalFieldOf("unlocked_player_features", List.of()).forGetter(PlayerProgress::unlockedPlayerFeatures)
	).apply(instance, PlayerProgress::new));

	public PlayerProgress {
		unlockedPlayerFeatures = List.copyOf(unlockedPlayerFeatures);
	}

	public static PlayerProgress empty() {
		return new PlayerProgress(false, List.of());
	}

	public PlayerProgress withFeature(Identifier id) {
		if (this.unlockedPlayerFeatures.contains(id)) {
			return this;
		}

		return new PlayerProgress(this.firstScrollGranted, java.util.stream.Stream.concat(
				this.unlockedPlayerFeatures.stream(), java.util.stream.Stream.of(id)
		).toList());
	}

	public boolean hasFeature(Identifier id) {
		return this.unlockedPlayerFeatures.contains(id);
	}
}
