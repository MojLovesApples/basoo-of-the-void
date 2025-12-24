package com.mojaye.basooofthevoid;

import com.mojaye.basooofthevoid.classes.SaveManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import java.util.Random;

public class BasooOfTheVoidClient implements ClientModInitializer {
	/**
	 * The SaveManager object, its just a json with extra steps.
	 */
	private SaveManager config;

	/**
	 * Random seed.
	 */
	private final Random random = new Random();

	/**
	 * Used for delaying each click.
	 */
	private long nextClickTime = 0;

	/**
	 * Simulates the action of interacting with an item.
	 */
	public void rightClick() {

		MinecraftClient client = MinecraftClient.getInstance();

		if (client.interactionManager != null && client.player != null) {
			// Simulate the right click action with the item held in hand
			client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);

			client.player.swingHand(Hand.MAIN_HAND); // Visually swing the hand
		}
	}

	@Override
	public void onInitializeClient() {
		config = SaveManager.load();

		// Register /botv as a command to toggle the mod
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("botv")
					.executes(context -> {
						MinecraftClient client = MinecraftClient.getInstance();
						if (client.player == null) return 0;

                        config.enabled = !config.enabled;
						if (config.enabled) {
							client.player.sendMessage(Text.literal("§b[BOTV] §aOn"), false);
						} else {
							client.player.sendMessage(Text.literal("§b[BOTV] §cOff"), false);
						}
						config.save();
						return 1;
					}));
		});

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (config.enabled && client.player != null && client.options.useKey.isPressed()) {
				long currentTime = System.currentTimeMillis();

				if (currentTime >= nextClickTime) {
					client.execute(this::rightClick);

					// Generate random delay between 0.13 and 0.19 seconds
					double randomDelay = 0.12 + (random.nextDouble() * (0.20 - 0.12));

					// Convert seconds to milliseconds for the timer
					nextClickTime = currentTime + (long)(randomDelay * 1000);
				}
			}
		});
	}
}