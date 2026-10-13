package net.napsternpt.racingminimap;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.napsternpt.racingminimap.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RacingMinimap implements ClientModInitializer {
	public static final String MOD_ID = "racingminimap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ModConfig CONFIG;

	public static NativeImage image;
	public static NativeImage image_with_players;
	public static NativeImage rounded_image;
	public static NativeImageBackedTexture texture;
	public static NativeImageBackedTexture players_texture;
	public static Identifier MINIMAP_TEXTURE;
	public static Identifier MINIMAP_PLAYERS;
	public static int render_distance = 15;
	public static int chunkLength = 7;
	public static boolean isMinimapLoaded = false;
	public static BlockPos mapBlockPos;

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player == null || client.world == null) {
				return;
			}
			if (!CONFIG.render_map) {
				return;
			}
			if (!CONFIG.render_out_of_boat && !(client.player.getVehicle() instanceof BoatEntity)) {
				return;
			}
			MinimapRenderer.render(graphics);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (MINIMAP_TEXTURE != null) {
				client.getTextureManager().destroyTexture(MINIMAP_TEXTURE);
			}
			if (MINIMAP_PLAYERS != null) {
				client.getTextureManager().destroyTexture(MINIMAP_PLAYERS);
			}
			if (image != null) {
				image.close();
				image = null;
			}
			if (image_with_players != null) {
				image_with_players.close();
				image_with_players = null;
			}
			if (rounded_image != null) {
				rounded_image.close();
				rounded_image = null;
			}
			texture = null;
			players_texture = null;
			MINIMAP_TEXTURE = null;
			MINIMAP_PLAYERS = null;
			isMinimapLoaded = false;
		});
	}
}
