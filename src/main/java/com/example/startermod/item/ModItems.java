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

	public static boolean giveKnowledgeScroll(ServerPlayer player, String technology, String profession, String rank) {
		return give(player, KNOWLEDGE_SCROLL, technology + " " + profession + " " + rank + " Scroll");
	}

	public static boolean giveTranslatedKnowledgeScroll(ServerPlayer player, String technology, String profession, String rank) {
		return give(player, TRANSLATED_KNOWLEDGE_SCROLL, "Translated " + technology + " " + profession + " " + rank + " Scroll");
	}

	private static boolean give(ServerPlayer player, Item item, String name) {
		ItemStack stack = new ItemStack(item);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
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
