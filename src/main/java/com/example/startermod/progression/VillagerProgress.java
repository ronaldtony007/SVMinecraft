package com.example.startermod.progression;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;

public record VillagerProgress(int resourceContribution, List<Identifier> knowledge, List<Identifier> unlockedTechnologies,
		int unlockedTradeLevel, int pendingScrollRank, Map<Identifier, Integer> resourceContributions) {
	// pendingScrollRank is retained so existing worlds can still deserialize their saved data.
	public static final Codec<VillagerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("resource_contribution", 0).forGetter(VillagerProgress::resourceContribution),
			Identifier.CODEC.listOf().optionalFieldOf("knowledge", List.of()).forGetter(VillagerProgress::knowledge),
			Identifier.CODEC.listOf().optionalFieldOf("unlocked_technologies", List.of()).forGetter(VillagerProgress::unlockedTechnologies),
			Codec.INT.optionalFieldOf("unlocked_trade_level", 1).forGetter(VillagerProgress::unlockedTradeLevel),
			Codec.INT.optionalFieldOf("pending_scroll_rank", 0).forGetter(VillagerProgress::pendingScrollRank),
			Codec.unboundedMap(Identifier.CODEC, Codec.INT).optionalFieldOf("resource_contributions", Map.of())
					.forGetter(VillagerProgress::resourceContributions)
	).apply(instance, VillagerProgress::new));

	public VillagerProgress(int resourceContribution, List<Identifier> knowledge, List<Identifier> unlockedTechnologies,
			int unlockedTradeLevel, int pendingScrollRank) {
		this(resourceContribution, knowledge, unlockedTechnologies, unlockedTradeLevel, pendingScrollRank, Map.of());
	}

	public VillagerProgress {
		resourceContribution = Math.max(0, resourceContribution);
		knowledge = List.copyOf(knowledge);
		unlockedTechnologies = List.copyOf(unlockedTechnologies);
		unlockedTradeLevel = Math.max(1, Math.min(5, unlockedTradeLevel));
		pendingScrollRank = Math.max(0, Math.min(5, pendingScrollRank));
		resourceContributions = resourceContributions.entrySet().stream()
				.collect(java.util.stream.Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> Math.max(0, entry.getValue())));
	}

	public static VillagerProgress empty() {
		return new VillagerProgress(0, List.of(), List.of(), 1, 0);
	}

	public VillagerProgress withResourceContribution(int amount) {
		return copy(amount, this.knowledge, this.unlockedTechnologies, this.unlockedTradeLevel, this.pendingScrollRank,
				Map.of());
	}

	public int contribution(Identifier itemId) {
		return resourceContributions.getOrDefault(itemId, 0);
	}

	public VillagerProgress withContribution(Identifier itemId, int amount) {
		Map<Identifier, Integer> updated = new java.util.HashMap<>(resourceContributions);
		updated.put(itemId, Math.max(0, amount));
		return copy(resourceContribution, knowledge, unlockedTechnologies, unlockedTradeLevel, pendingScrollRank, updated);
	}

	public VillagerProgress withKnowledge(Identifier id) {
		if (this.knowledge.contains(id)) {
			return this;
		}

		return copy(this.resourceContribution, append(this.knowledge, id), this.unlockedTechnologies, this.unlockedTradeLevel, this.pendingScrollRank,
				this.resourceContributions);
	}

	public VillagerProgress withTechnology(Identifier id) {
		if (this.unlockedTechnologies.contains(id)) {
			return this;
		}

		return copy(this.resourceContribution, this.knowledge, append(this.unlockedTechnologies, id), this.unlockedTradeLevel, this.pendingScrollRank,
				this.resourceContributions);
	}

	public VillagerProgress withTradeUnlock(int level) {
		return copy(0, this.knowledge, this.unlockedTechnologies, level, 0, Map.of());
	}

	public boolean knows(Identifier id) {
		return this.knowledge.contains(id);
	}

	public boolean hasTechnology(Identifier id) {
		return this.unlockedTechnologies.contains(id);
	}

	private VillagerProgress copy(int contribution, List<Identifier> knowledge, List<Identifier> technologies,
			int tradeLevel, int pendingRank, Map<Identifier, Integer> contributions) {
		return new VillagerProgress(contribution, knowledge, technologies, tradeLevel, pendingRank, contributions);
	}

	private static List<Identifier> append(List<Identifier> values, Identifier value) {
		return java.util.stream.Stream.concat(values.stream(), java.util.stream.Stream.of(value)).toList();
	}
}
