package net.napsternpt.racingminimap;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.napsternpt.racingminimap.config.ModConfig;
//? if >=1.21.5 {
/*import net.minecraft.client.gl.RenderPipelines;
*///?}
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

//? if >=1.21.5 {
/*import org.joml.Matrix3x2fStack;
*///?}
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
	private static long lastMapUpdate = 0L;
 	private static double previousChunkOffsetX = 0.0;
	private static double previousChunkOffsetZ = 0.0;

	public static void render(DrawContext graphics) {
		var client = net.minecraft.client.MinecraftClient.getInstance();
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();

		//? if <1.21.5 {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		//?}

		if (client.player != null) {
			if (!RacingMinimap.isMinimapLoaded) {
				if ((RacingMinimap.render_distance & 1) == 0) {
					--RacingMinimap.render_distance;
				}
				RacingMinimap.image = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.image_with_players = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				RacingMinimap.rounded_image = new NativeImage(RacingMinimap.render_distance * 16, RacingMinimap.render_distance * 16, false);
				//? if >=1.21.5 {
				/*RacingMinimap.texture = new NativeImageBackedTexture(() -> "racingminimap_minimap", RacingMinimap.rounded_image);
				RacingMinimap.players_texture = new NativeImageBackedTexture(() -> "racingminimap_players", RacingMinimap.image_with_players);
				*///?} else {
				RacingMinimap.texture = new NativeImageBackedTexture(RacingMinimap.rounded_image);
				RacingMinimap.players_texture = new NativeImageBackedTexture(RacingMinimap.image_with_players);
				//?}
				RacingMinimap.MINIMAP_TEXTURE = Identifier.of(RacingMinimap.MOD_ID, "textures/minimap");
				RacingMinimap.MINIMAP_PLAYERS = Identifier.of(RacingMinimap.MOD_ID, "textures/minimap_players");
				client.getTextureManager().registerTexture(RacingMinimap.MINIMAP_TEXTURE, RacingMinimap.texture);
				client.getTextureManager().registerTexture(RacingMinimap.MINIMAP_PLAYERS, RacingMinimap.players_texture);
				RacingMinimap.isMinimapLoaded = true;
			}
			scale = RacingMinimap.CONFIG.map_scale / 100.0f;

			ChunkPos currentChunkPos = client.player.getChunkPos();
			BlockPos currentPlayerPos = client.player.getBlockPos();
			double playerX = client.player.getX();
			double playerZ = client.player.getZ();
			double chunkOffsetX = (playerX % 16.0 + 16.0) % 16.0;
			double chunkOffsetZ = (playerZ % 16.0 + 16.0) % 16.0;
			int fps = RacingMinimap.CONFIG.map_fps;
			long now = System.currentTimeMillis();
			if (fps <= 0 || now - lastMapUpdate >= (long) (1000.0 / fps)) {
				lastMapUpdate = now;
				previousChunkOffsetX = chunkOffsetX;
				previousChunkOffsetZ = chunkOffsetZ;
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
			}

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

			renderMap(graphics, map_x + 49.5, map_y + 49.5, (float) Math.toRadians(client.player.getYaw() * -1.0f + 180.0f), previousChunkOffsetX, previousChunkOffsetZ);
		}
		//? if <1.21.5 {
		RenderSystem.disableBlend();
		//?}
	}

	private static void renderMap(DrawContext graphics, double center_x, double center_y, float angle_map, double chunkOffsetX, double chunkOffsetZ) {
		//? if >=1.21.5 {
		/*Matrix3x2fStack matrices = graphics.getMatrices();
		*///?} else {
		MatrixStack matrices = graphics.getMatrices();
		//?}
		if (RacingMinimap.texture != null && RacingMinimap.texture.getImage() != null) {
			//? if >=1.21.5 {
			/*matrices.pushMatrix();
			matrices.translate((float) center_x, (float) center_y);
			matrices.scale(scale, scale);
			*///?} else {
			matrices.push();
			matrices.translate(center_x, center_y, 0.0);
			matrices.scale(scale, scale, 1.0f);
			//?}
			float delta = angle_map - previousAngleMap;
			delta = (delta + (float) Math.PI) % (float) (2.0 * Math.PI);
			if (delta < 0.0f) {
				delta += (float) (2.0 * Math.PI);
			}
			delta -= (float) Math.PI;
			float interpolatedAngle = previousAngleMap + delta * 0.5f;
			previousAngleMap = interpolatedAngle;
			//? if >=1.21.5 {
			/*matrices.rotate(interpolatedAngle);
			matrices.translate((float) (-chunkOffsetX + 8.0), (float) (-chunkOffsetZ + 8.0));
			matrices.translate((float) -center_x, (float) -center_y);
			*///?} else {
			rotation.set(0, 0, 0, 1).rotateZ(interpolatedAngle);
			matrices.multiply(rotation);
			matrices.translate(-chunkOffsetX + 8.0, -chunkOffsetZ + 8.0, 0.0);
			matrices.translate(-center_x, -center_y, 0.0);
			//?}

			int texSize = RacingMinimap.render_distance * 16;
			int texOffset = RacingMinimap.render_distance * 8;
			float opacity = RacingMinimap.CONFIG.map_opacity / 100.0f;
			int hudColor = (Math.round(opacity * 255.0f) << 24) | 0xFFFFFF;
			//? if <1.21.2 {
			/*RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
			*///?}
			drawGuiTexture(graphics, RacingMinimap.MINIMAP_TEXTURE, (int) (center_x - texOffset), (int) (center_y - texOffset), texSize, texSize, texSize, texSize, hudColor);
			if (RacingMinimap.players_texture != null && RacingMinimap.players_texture.getImage() != null) {
				drawGuiTexture(graphics, RacingMinimap.MINIMAP_PLAYERS, (int) (center_x - texOffset), (int) (center_y - texOffset), texSize, texSize, texSize, texSize, hudColor);
			}
			//? if >=1.21.5 {
			/*matrices.popMatrix();
			*///?} else {
			matrices.pop();
			//?}
			drawPlayerNametags(graphics, center_x, center_y, scale, interpolatedAngle, chunkOffsetX, chunkOffsetZ, texOffset);
			drawPlayerHeads(graphics, center_x, center_y, scale, interpolatedAngle, chunkOffsetX, chunkOffsetZ, texOffset);
		}
		//? if <1.21.2 {
		/*RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, RacingMinimap.CONFIG.map_opacity / 100.0f);
		*///?}
		int pointerColor = (Math.round(RacingMinimap.CONFIG.map_opacity / 100.0f * 255.0f) << 24) | 0xFFFFFF;
		drawGuiTexture(graphics, POINTER_TEXTURE, MinimapRenderer.pointer_map[0], MinimapRenderer.pointer_map[1], MinimapRenderer.pointer_map[2], MinimapRenderer.pointer_map[3], POINTER_WIDTH, POINTER_HEIGHT, pointerColor);
		//? if <1.21.2 {
		/*RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		*///?}
	}

	private static void drawPlayerNametags(DrawContext graphics, double center_x, double center_y, float scale, float angle, double chunkOffsetX, double chunkOffsetZ, int texOffset) {
		ModConfig config = RacingMinimap.CONFIG;
		if (!config.show_player_nametags || MapHelper.playerMarkers.isEmpty()) {
			return;
		}
		float size = config.nametag_scale / 200.0f;
		if (size <= 0.0f) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;
		if (textRenderer == null) {
			return;
		}
		float cos = MathHelper.cos(angle);
		float sin = MathHelper.sin(angle);
		boolean top = config.nametag_anchor == ModConfig.NametagAnchor.TOP;
		double ringRadius = 45.0 * config.ring_size / 100.0;
		double visibleRadiusSq = ringRadius * ringRadius;
		for (MapHelper.PlayerMarker marker : MapHelper.playerMarkers) {
			double tx = marker.spriteX() - texOffset - chunkOffsetX + 8.0;
			double tz = marker.spriteZ() - texOffset - chunkOffsetZ + 8.0;
			double rx = tx * cos - tz * sin;
			double rz = tx * sin + tz * cos;
			double screenX = center_x + scale * rx;
			double screenY = center_y + scale * rz;
			double dx = screenX - center_x;
			double dy = screenY - center_y;
			if (dx * dx + dy * dy > visibleRadiusSq) {
				continue;
			}
			String name = marker.name();
			float width = textRenderer.getWidth(name) * size;
			float height = textRenderer.fontHeight * size;
			float gap = 4.0f;
			float textX = (float) (screenX - width / 2.0f);
			float textY = top ? (float) (screenY - height - gap) : (float) (screenY + gap);
			//? if >=1.21.5 {
			/*Matrix3x2fStack textMatrices = graphics.getMatrices();
			textMatrices.pushMatrix();
			textMatrices.translate(textX, textY);
			textMatrices.scale(size, size);
			graphics.drawText(textRenderer, name, 0, 0, 0xFFFFFF, true);
			textMatrices.popMatrix();
			*///?} else {
			MatrixStack textMatrices = graphics.getMatrices();
			textMatrices.push();
			textMatrices.translate(textX, textY, 0.0);
			textMatrices.scale(size, size, 1.0f);
			graphics.drawText(textRenderer, name, 0, 0, 0xFFFFFF, true);
			textMatrices.pop();
			//?}
		}
	}

	private static void drawPlayerHeads(DrawContext graphics, double center_x, double center_y, float scale, float angle, double chunkOffsetX, double chunkOffsetZ, int texOffset) {
		ModConfig config = RacingMinimap.CONFIG;
		if (!config.player_heads || MapHelper.playerMarkers.isEmpty()) {
			return;
		}
		float cos = MathHelper.cos(angle);
		float sin = MathHelper.sin(angle);
		double ringRadius = 45.0 * config.ring_size / 100.0;
		double visibleRadiusSq = ringRadius * ringRadius;
		int size = 8;
		int color = (Math.round(RacingMinimap.CONFIG.map_opacity / 100.0f * 255.0f) << 24) | 0xFFFFFF;
		for (MapHelper.PlayerMarker marker : MapHelper.playerMarkers) {
			Identifier texture = marker.head();
			if (texture == null) {
				continue;
			}
			double tx = marker.spriteX() - texOffset - chunkOffsetX + 8.0;
			double tz = marker.spriteZ() - texOffset - chunkOffsetZ + 8.0;
			double rx = tx * cos - tz * sin;
			double rz = tx * sin + tz * cos;
			double screenX = center_x + scale * rx;
			double screenY = center_y + scale * rz;
			double dx = screenX - center_x;
			double dy = screenY - center_y;
			if (dx * dx + dy * dy > visibleRadiusSq) {
				continue;
			}
			int x = (int) Math.round(screenX) - size / 2;
			int y = (int) Math.round(screenY) - size / 2;
			drawHead(graphics, texture, x, y, size, color);
		}
	}

	private static void drawHead(DrawContext graphics, Identifier texture, int x, int y, int size, int color) {
		//? if <1.21.2 {
		/*RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, (color >>> 24) / 255.0f);
		graphics.drawTexture(texture, x, y, 8.0F, 8.0F, size, size, 64, 64);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		*///?} elif <1.21.5 {
		graphics.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 8.0F, 8.0F, size, size, 64, 64, color);
		//?} else {
		/*graphics.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 8.0F, 8.0F, size, size, 64, 64, color);
		*///?}
	}

	private static void drawGuiTexture(DrawContext graphics, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight, int color) {
		//? if <1.21.2 {
		/*graphics.drawTexture(texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
		*///?} elif <1.21.5 {
		graphics.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight, color);
		//?} else {
		/*graphics.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight, color);
		*///?}
	}
}