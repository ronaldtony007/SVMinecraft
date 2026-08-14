package com.example.startermod.progression;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;

public record VillagerProgress(int resourceContribution, List<Identifier> knowledge, List<Identifier> unlockedTechnologies,
		int unlockedTradeLevel, int pendingScrollRank) {
	public static final Codec<VillagerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("resource_contribution", 0).forGetter(VillagerProgress::resourceContribution),
			Identifier.CODEC.listOf().optionalFieldOf("knowledge", List.of()).forGetter(VillagerProgress::knowledge),
			Identifier.CODEC.listOf().optionalFieldOf("unlocked_technologies", List.of()).forGetter(VillagerProgress::unlockedTechnologies),
			Codec.INT.optionalFieldOf("unlocked_trade_level", 1).forGetter(VillagerProgress::unlockedTradeLevel),
			Codec.INT.optionalFieldOf("pending_scroll_rank", 0).forGetter(VillagerProgress::pendingScrollRank)
	).apply(instance, VillagerProgress::new));

	public VillagerProgress {
		resourceContribution = Math.max(0, resourceContribution);
		knowledge = List.copyOf(knowledge);
		unlockedTechnologies = List.copyOf(unlockedTechnologies);
		unlockedTradeLevel = Math.max(1, Math.min(5, unlockedTradeLevel));
		pendingScrollRank = Math.max(0, Math.min(5, pendingScrollRank));
	}

	public static VillagerProgress empty() {
		return new VillagerProgress(0, List.of(), List.of(), 1, 0);
	}

	public VillagerProgress withResourceContribution(int amount) {
		return copy(amount, this.knowledge, this.unlockedTechnologies, this.unlockedTradeLevel, this.pendingScrollRank);
	}

	public VillagerProgress withKnowledge(Identifier id) {
		if (this.knowledge.contains(id)) {
			return this;
		}

		return copy(this.resourceContribution, append(this.knowledge, id), this.unlockedTechnologies, this.unlockedTradeLevel, this.pendingScrollRank);
	}

	public VillagerProgress withTechnology(Identifier id) {
		if (this.unlockedTechnologies.contains(id)) {
			return this;
		}

		return copy(this.resourceContribution, this.knowledge, append(this.unlockedTechnologies, id), this.unlockedTradeLevel, this.pendingScrollRank);
	}

	public VillagerProgress withTradeUnlock(int level) {
		return copy(0, this.knowledge, this.unlockedTechnologies, level, 0);
	}

	public VillagerProgress withPendingScroll(int rank) {
		return copy(this.resourceContribution, this.knowledge, this.unlockedTechnologies, this.unlockedTradeLevel, rank);
	}

	public boolean knows(Identifier id) {
		return this.knowledge.contains(id);
	}

	public boolean hasTechnology(Identifier id) {
		return this.unlockedTechnologies.contains(id);
	}

	private VillagerProgress copy(int iron, List<Identifier> knowledge, List<Identifier> technologies,
			int tradeLevel, int pendingRank) {
		return new VillagerProgress(iron, knowledge, technologies, tradeLevel, pendingRank);
	}

	private static List<Identifier> append(List<Identifier> values, Identifier value) {
		return java.util.stream.Stream.concat(values.stream(), java.util.stream.Stream.of(value)).toList();
	}
}
