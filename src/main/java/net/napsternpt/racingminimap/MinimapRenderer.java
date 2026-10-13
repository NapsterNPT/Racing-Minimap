package net.napsternpt.racingminimap;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import org.joml.Quaternionf;

public class MinimapRenderer {
	public static final Identifier POINTER_TEXTURE = Identifier.of(RacingMinimap.MOD_ID, "textures/pointer.png");
	public static final int POINTER_WIDTH = 5;
	public static final int POINTER_HEIGHT = 6;
	public static float scale = 0.45f;

	private static final Quaternionf rotation = new Quaternionf();
	private static final int[] pointer_map = new int[4];

	private static ChunkPos previousChunkPos = null;
	private static int previousY = 0;
	private static double previousX = 0.0;
	private static double previousZ = 0.0;
	private static float previousAngleMap = 0.0f;

	public static void render(DrawContext graphics) {
		var client = net.minecraft.client.MinecraftClient.getInstance();
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		if (client.player != null) {
			if (!RacingMinimap.isMinimapLoaded) {
				if ((RacingMinimap.render_distance & 1) == 0) {
					--RacingMinimap.render_distance;
				}
				RacingMinimap.image = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.image_with_players = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.rounded_image = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.texture = new NativeImageBackedTexture(RacingMinimap.rounded_image);
				RacingMinimap.players_texture = new NativeImageBackedTexture(RacingMinimap.image_with_players);
				RacingMinimap.MINIMAP_TEXTURE = Identifier.of(RacingMinimap.MOD_ID, "textures/minimap");
				RacingMinimap.MINIMAP_PLAYERS = Identifier.of(RacingMinimap.MOD_ID, "textures/minimap_players");
				client.getTextureManager().registerTexture(RacingMinimap.MINIMAP_TEXTURE, RacingMinimap.texture);
				client.getTextureManager().registerTexture(RacingMinimap.MINIMAP_PLAYERS, RacingMinimap.players_texture);
				RacingMinimap.isMinimapLoaded = true;
			}
			ChunkPos currentChunkPos = client.player.getChunkPos();
			BlockPos currentPlayerPos = client.player.getBlockPos();
			double playerX = client.player.getX();
			double playerZ = client.player.getZ();
			if (currentPlayerPos.getY() != previousY) {
				MapHelper.blitMapToTextureY(client, currentChunkPos, currentPlayerPos);
				previousY = currentPlayerPos.getY();
				previousChunkPos = currentChunkPos;
			} else if (previousChunkPos == null || !previousChunkPos.equals(currentChunkPos)) {
				MapHelper.blitMapToTextureChunk(client, currentChunkPos, currentPlayerPos);
				previousY = currentPlayerPos.getY();
				previousChunkPos = currentChunkPos;
			} else if (playerX != previousX || playerZ != previousZ) {
				MapHelper.updateTexture(client);
				previousX = playerX;
				previousZ = playerZ;
			}
			MapHelper.updatePlayers(client);

			scale = RacingMinimap.CONFIG.map_scale / 100.0f;

			int map_size = 99;
			int map_screen_x = scaledWidth - map_size - 20;
			int map_screen_y = scaledHeight - map_size - 20;
			double map_offset_x = RacingMinimap.CONFIG.map_x / 100.0 * map_screen_x;
			double map_offset_y = RacingMinimap.CONFIG.map_y / 100.0 * map_screen_y;
			int map_x = (int) Math.round(map_offset_x) + 10;
			int map_y = (int) Math.round(map_offset_y) + 10;
			pointer_map[0] = map_x + 47;
			pointer_map[1] = map_y + 47;
			pointer_map[2] = POINTER_WIDTH;
			pointer_map[3] = POINTER_HEIGHT;
			double chunkOffsetX = (playerX % 16.0 + 16.0) % 16.0;
			double chunkOffsetZ = (playerZ % 16.0 + 16.0) % 16.0;

			renderMap(graphics, map_x + 49.5, map_y + 49.5, (float) Math.toRadians(client.player.getYaw() * -1.0f + 180.0f), chunkOffsetX, chunkOffsetZ);
		}
		RenderSystem.disableBlend();
	}

	private static void renderMap(DrawContext graphics, double center_x, double center_y,
								  float angle_map, double chunkOffsetX, double chunkOffsetZ) {
		MatrixStack matrices = graphics.getMatrices();
		if (RacingMinimap.texture != null && RacingMinimap.texture.getImage() != null) {
			matrices.push();
			matrices.translate(center_x, center_y, 0.0);
			matrices.scale(scale, scale, 1.0f);
			float delta = angle_map - previousAngleMap;
			delta = (delta + (float) Math.PI) % (float) (2.0 * Math.PI);
			if (delta < 0.0f) {
				delta += (float) (2.0 * Math.PI);
			}
			delta -= (float) Math.PI;
			float interpolatedAngle = previousAngleMap + delta * 0.5f;
			previousAngleMap = interpolatedAngle;
			rotation.set(0, 0, 0, 1).rotateZ(interpolatedAngle);
			matrices.multiply(rotation);
			matrices.translate(-chunkOffsetX + 8.0, -chunkOffsetZ + 8.0, 0.0);
			matrices.translate(-center_x, -center_y, 0.0);

			int texSize = RacingMinimap.render_distance * 16;
			int texOffset = RacingMinimap.render_distance * 8;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			graphics.drawTexture(RenderLayer::getGuiTextured, RacingMinimap.MINIMAP_TEXTURE,
					(int) (center_x - texOffset), (int) (center_y - texOffset),
					0.0f, 0.0f, texSize, texSize, texSize, texSize);
			if (RacingMinimap.players_texture != null && RacingMinimap.players_texture.getImage() != null) {
				graphics.drawTexture(RenderLayer::getGuiTextured, RacingMinimap.MINIMAP_PLAYERS,
						(int) (center_x - texOffset), (int) (center_y - texOffset),
						0.0f, 0.0f, texSize, texSize, texSize, texSize);
			}
			matrices.pop();
		}
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		graphics.drawTexture(RenderLayer::getGuiTextured, POINTER_TEXTURE, MinimapRenderer.pointer_map[0], MinimapRenderer.pointer_map[1], 0.0f, 0.0f, MinimapRenderer.pointer_map[2], MinimapRenderer.pointer_map[3], POINTER_WIDTH, POINTER_HEIGHT);
	}
}