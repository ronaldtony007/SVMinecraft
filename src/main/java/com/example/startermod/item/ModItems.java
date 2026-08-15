package com.example.startermod.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

import com.example.startermod.StarterMod;

public final class ModItems {
	public static final ResourceKey<Item> KNOWLEDGE_SCROLL_KEY = create("knowledge_scroll");
	public static final Item KNOWLEDGE_SCROLL = register(KNOWLEDGE_SCROLL_KEY, Item::new, new Item.Properties().stacksTo(1));
	public static final ResourceKey<Item> TRANSLATED_KNOWLEDGE_SCROLL_KEY = create("translated_knowledge_scroll");
	public static final Item TRANSLATED_KNOWLEDGE_SCROLL = register(TRANSLATED_KNOWLEDGE_SCROLL_KEY, Item::new, new Item.Properties().stacksTo(1));

	private ModItems() {
	}

	private static ResourceKey<Item> create(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(StarterMod.MOD_ID, name));
	}

	private static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties properties) {
		Item item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static boolean giveTranslatedKnowledgeScroll(ServerPlayer player, String technology, String profession, String rank) {
		return give(player, translatedKnowledgeScroll(technology, profession, rank));
	}

	public static ItemStack knowledgeScroll(Identifier technology, String profession, String rank) {
		return scroll(KNOWLEDGE_SCROLL, technology.toString(), displayName(technology.toString()), profession, rank, false);
	}

	public static ItemStack translatedKnowledgeScroll(String technology, String profession, String rank) {
		return scroll(TRANSLATED_KNOWLEDGE_SCROLL, technology, displayName(technology), profession, rank, true);
	}

	public static String scrollTechnology(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
				.copyTag().getStringOr("technology", "");
	}

	public static String scrollProfession(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
				.copyTag().getStringOr("profession", "");
	}

	private static ItemStack scroll(Item item, String technology, String technologyLabel, String profession, String rank,
			boolean translated) {
		ItemStack stack = new ItemStack(item);
		String prefix = translated ? "Translated " : "";
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(prefix + technologyLabel + " " + displayName(profession) + " " + rank + " Scroll"));
		CompoundTag data = new CompoundTag();
		data.putString("technology", technology);
		data.putString("profession", profession);
		data.putString("rank", rank);
		data.putBoolean("translated", translated);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
		return stack;
	}

	private static String displayName(String value) {
		String path = value.contains(":") ? value.substring(value.indexOf(':') + 1) : value;
		return java.util.Arrays.stream(path.split("_"))
				.map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
				.collect(java.util.stream.Collectors.joining(" "));
	}

	private static boolean give(ServerPlayer player, ItemStack stack) {
		if (player.getInventory().add(stack)) {
			return true;
		}

		player.drop(stack, false);
		return true;
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(KNOWLEDGE_SCROLL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(TRANSLATED_KNOWLEDGE_SCROLL));
	}
}
