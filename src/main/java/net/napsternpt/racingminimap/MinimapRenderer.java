package net.napsternpt.racingminimap;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import org.joml.Quaternionf;

public class MinimapRenderer {
	public static final Identifier POINTER_TEXTURE = Identifier.of(RacingMinimap.MOD_ID, "textures/pointer.png");
	public static final int POINTER_WIDTH = 5;
	public static final int POINTER_HEIGHT = 6;
	public static float scale = 0.45f;

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

		if (RacingMinimap.CONFIG.render_map && client.player != null) {
			if (!RacingMinimap.isMinimapLoaded) {
				if ((RacingMinimap.render_distance & 1) == 0) {
					--RacingMinimap.render_distance;
				}
				RacingMinimap.image = new net.minecraft.client.texture.NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.image_with_players = new net.minecraft.client.texture.NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.rounded_image = new net.minecraft.client.texture.NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.texture = new net.minecraft.client.texture.NativeImageBackedTexture(RacingMinimap.rounded_image);
				RacingMinimap.players_texture = new net.minecraft.client.texture.NativeImageBackedTexture(RacingMinimap.image_with_players);
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

			int map_size = 99;
			int map_screen_x = scaledWidth - map_size - 20;
			int map_screen_y = scaledHeight - map_size - 20;
			double map_offset_x = RacingMinimap.CONFIG.map_x / 100.0 * map_screen_x;
			double map_offset_y = RacingMinimap.CONFIG.map_y / 100.0 * map_screen_y;
			int map_x = (int) Math.round(map_offset_x) + 10;
			int map_y = (int) Math.round(map_offset_y) + 10;
			int[] pointer_map = new int[]{map_x + 47, map_y + 47, POINTER_WIDTH, POINTER_HEIGHT};
			double chunkOffsetX = (playerX % 16.0 + 16.0) % 16.0;
			double chunkOffsetZ = (playerZ % 16.0 + 16.0) % 16.0;

			renderMap(graphics, map_x + 49.5, map_y + 49.5, pointer_map, (float) Math.toRadians(client.player.getYaw() * -1.0f + 180.0f), chunkOffsetX, chunkOffsetZ);
		}
		RenderSystem.disableBlend();
	}

	private static void renderMap(DrawContext graphics, double center_x, double center_y, int[] pointer,
			float angle_map, double chunkOffsetX, double chunkOffsetZ) {
		MatrixStack matrices = graphics.getMatrices();
		if (RacingMinimap.texture != null && RacingMinimap.texture.getImage() != null) {
			matrices.push();
			matrices.translate(center_x, center_y, 0.0);
			matrices.scale(scale, scale, 1.0f);
			float interpolatedAngle = angle_map;
			if (previousAngleMap == 0.0f) {
				interpolatedAngle = MathHelper.lerp(previousAngleMap, angle_map, 0.5f);
			}
			previousAngleMap = interpolatedAngle;
			Quaternionf rotation = new Quaternionf().rotateZ(interpolatedAngle);
			matrices.multiply(rotation);
			matrices.translate(-chunkOffsetX + 8.0, -chunkOffsetZ + 8.0, 0.0);
			matrices.translate(-center_x, -center_y, 0.0);

			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 100.0f);
			graphics.drawTexture(RenderLayer::getGuiTextured, RacingMinimap.MINIMAP_TEXTURE,
					(int) (center_x - RacingMinimap.render_distance * 8.0), (int) (center_y - RacingMinimap.render_distance * 8.0),
					0.0f, 0.0f, RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16,
					RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16);
			if (RacingMinimap.players_texture != null && RacingMinimap.players_texture.getImage() != null) {
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				graphics.drawTexture(RenderLayer::getGuiTextured, RacingMinimap.MINIMAP_PLAYERS,
						(int) (center_x - RacingMinimap.render_distance * 8.0),
						(int) (center_y - RacingMinimap.render_distance * 8.0),
						0.0f, 0.0f, RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16,
						RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16);
			}
			matrices.pop();
		}
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		graphics.drawTexture(RenderLayer::getGuiTextured, POINTER_TEXTURE,
				pointer[0], pointer[1], 0.0f, 0.0f, pointer[2], pointer[3], POINTER_WIDTH, POINTER_HEIGHT);
	}
}