package com.silvermage.silversvillagers.command;

import java.util.Comparator;
import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.core.registries.BuiltInRegistries;

import com.silvermage.silversvillagers.interaction.VillagerLocator;
import com.silvermage.silversvillagers.profession.BlacksmithEligibility;
import com.silvermage.silversvillagers.progression.ProgressionService;
import com.silvermage.silversvillagers.progression.ProgressionStep;
import com.silvermage.silversvillagers.progression.VillagerProgress;
import com.silvermage.silversvillagers.progression.VillagerRankRequirement;

public final class VillagerProgressCommand {
	private static final double COMMAND_RADIUS = 8.0D;

	private VillagerProgressCommand() {
	}

	public static void initialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		var root = Commands.literal("villagerprogress")
				.requires(source -> source.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR));
		root.then(Commands.literal("info").executes(VillagerProgressCommand::info));
		root.then(Commands.literal("setrank").then(Commands.argument("rank", StringArgumentType.word())
				.suggests((context, builder) -> {
					builder.suggest("novice");
					builder.suggest("apprentice");
					builder.suggest("journeyman");
					builder.suggest("expert");
					builder.suggest("master");
					return builder.buildFuture();
				})
				.executes(VillagerProgressCommand::setRank)));
		root.then(Commands.literal("setresource").then(Commands.argument("resource", StringArgumentType.word())
				.then(Commands.argument("amount", IntegerArgumentType.integer(0))
						.executes(VillagerProgressCommand::setResource))));
		root.then(Commands.literal("giveknowledge").then(Commands.argument("technology", StringArgumentType.word())
				.executes(VillagerProgressCommand::giveKnowledge)));
		root.then(Commands.literal("unlock").then(Commands.argument("technology", StringArgumentType.word())
				.executes(VillagerProgressCommand::unlock)));
		root.then(Commands.literal("reset").executes(VillagerProgressCommand::reset));
		dispatcher.register(root);
	}

	private static int info(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Villager villager = requireVillager(context);
		if (villager == null) return 0;

		VillagerProgress progress = ProgressionService.getVillagerProgress(villager);
		context.getSource().sendSuccess(() -> Component.literal("Profession: " + BlacksmithEligibility.professionName(villager)), false);
		context.getSource().sendSuccess(() -> Component.literal("Rank: " + VillagerRankRequirement.levelName(villager.getVillagerData().level())), false);
		context.getSource().sendSuccess(() -> Component.literal("Trade tier unlocked: "
				+ VillagerRankRequirement.levelName(progress.unlockedTradeLevel())), false);
		ProgressionService.nextStep(villager).ifPresent(step -> {
			context.getSource().sendSuccess(() -> Component.literal("Next technology: "
					+ ProgressionService.displayName(step.technologyId())), false);
			step.requirements().forEach((item, required) -> {
				Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
				int contribution = progress.resourceContributions().containsKey(itemId)
						? progress.contribution(itemId)
						: item == step.requiredMaterial() ? progress.resourceContribution() : 0;
				context.getSource().sendSuccess(() -> Component.literal(ProgressionService.displayName(itemId) + ": "
						+ contribution + " of " + required), false);
			});
		});
		return 1;
	}

	private static int setRank(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		Villager villager = requireVillager(context);
		if (villager == null) return 0;
		String rank = StringArgumentType.getString(context, "rank").toLowerCase(java.util.Locale.ROOT);
		int level = switch (rank) {
			case "novice" -> 1;
			case "apprentice" -> 2;
			case "journeyman" -> 3;
			case "expert" -> 4;
			case "master" -> 5;
			default -> -1;
		};
		if (level < 0) return fail(context, "Unknown rank: " + rank);
		ProgressionService.debugSetRank(player, villager, level);
		ProgressionService.requestNextResource(player, villager);
		return success(context, "Set villager rank to " + VillagerRankRequirement.levelName(level) + ".");
	}

	private static int setResource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		Villager villager = requireVillager(context);
		if (villager == null) return 0;
		var step = ProgressionService.nextStep(villager);
		String resource = StringArgumentType.getString(context, "resource").toLowerCase(java.util.Locale.ROOT);
		var requiredMaterial = step.flatMap(value -> value.requirements().keySet().stream()
				.filter(item -> BuiltInRegistries.ITEM.getKey(item).getPath().equals(resource)).findFirst());
		if (requiredMaterial.isEmpty()) {
			return fail(context, "The current request is " + step.map(ProgressionStep::materialName).orElse("none") + ".");
		}
		int amount = IntegerArgumentType.getInteger(context, "amount");
		ProgressionService.setResourceContribution(player, villager, requiredMaterial.get(), amount);
		return success(context, "Set " + ProgressionService.displayName(BuiltInRegistries.ITEM.getKey(requiredMaterial.get())) + " to " + amount + " of "
				+ step.get().requirements().get(requiredMaterial.get()) + ".");
	}

	private static int giveKnowledge(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Villager villager = requireVillager(context);
		if (villager == null) return 0;
		Identifier knowledge = Identifier.fromNamespaceAndPath("silvers_villagers", StringArgumentType.getString(context, "technology"));
		return success(context, ProgressionService.teachKnowledge(villager, knowledge)
				? "Granted " + ProgressionService.displayName(knowledge) + " knowledge."
				: "Knowledge was already known or the villager is not eligible.");
	}

	private static int unlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		Villager villager = requireVillager(context);
		if (villager == null) return 0;
		Identifier technology = Identifier.fromNamespaceAndPath("silvers_villagers", StringArgumentType.getString(context, "technology"));
		var step = ProgressionService.nextStep(villager);
		if (!ProgressionService.unlockTechnology(villager, technology) || step.isEmpty()) {
			return fail(context, "That technology is not the current progression step.");
		}
		ProgressionService.completeProgression(player, villager);
		return success(context, "Unlocked " + ProgressionService.displayName(technology) + ".");
	}

	private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		Villager villager = requireVillager(context);
		if (villager == null) return 0;
		ProgressionService.reset(villager, player);
		return success(context, "Reset villager and player progression.");
	}

	private static Villager requireVillager(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Villager villager = findNearestVillager(context.getSource().getPlayerOrException()).orElse(null);
		if (villager == null) fail(context, "No villager found within " + COMMAND_RADIUS + " blocks.");
		return villager;
	}

	private static Optional<Villager> findNearestVillager(ServerPlayer player) {
		return ((net.minecraft.server.level.ServerLevel) player.level()).getEntitiesOfClass(Villager.class,
				player.getBoundingBox().inflate(COMMAND_RADIUS)).stream()
				.min(Comparator.comparingDouble((Villager villager) -> player.distanceToSqr(villager))
						.thenComparing(villager -> villager.getUUID().toString()));
	}

	private static int success(CommandContext<CommandSourceStack> context, String message) {
		context.getSource().sendSuccess(() -> Component.literal(message), false);
		return 1;
	}

	private static int fail(CommandContext<CommandSourceStack> context, String message) {
		context.getSource().sendFailure(Component.literal(message));
		return 0;
	}
}
