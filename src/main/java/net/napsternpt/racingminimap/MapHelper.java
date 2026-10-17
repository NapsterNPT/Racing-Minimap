package net.napsternpt.racingminimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
//? if >=1.21.2 {
import net.minecraft.entity.vehicle.AbstractBoatEntity;
//?} else {
/*import net.minecraft.entity.vehicle.BoatEntity;
*///?}
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MapHelper {
	private static final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

	private static final float REFERENCE_SCALE = 0.45f;

	public record PlayerMarker(String name, int spriteX, int spriteZ, Identifier head) {}

	private record BoatPlayer(PlayerEntity player, boolean ghost) {}

	public static final List<PlayerMarker> playerMarkers = new ArrayList<>();

	private static int darken(int color) {
		int a = color >>> 24;
		int r = (int) ((color >> 16 & 0xFF) * 0.7);
		int g = (int) ((color >> 8 & 0xFF) * 0.7);
		int b = (int) ((color & 0xFF) * 0.7);
		return a << 24 | r << 16 | g << 8 | b;
	}

	private static float dotZoom() {
		float current = MinimapRenderer.scale;
		if (current <= 0.0f) {
			current = REFERENCE_SCALE;
		}
		return REFERENCE_SCALE / current;
	}

	private static int getArgb(NativeImage image, int x, int y) {
		//? if <1.21.2 {
		/*return swapRedBlue(image.getColor(x, y));
		*///?} else {
		return image.getColorArgb(x, y);
		//?}
	}

	private static void setArgb(NativeImage image, int x, int y, int color) {
		//? if <1.21.2 {
		/*image.setColor(x, y, swapRedBlue(color));
		*///?} else {
		image.setColorArgb(x, y, color);
		//?}
	}

	//? if <1.21.2 {
	/*private static int swapRedBlue(int color) {
		return color & 0xFF00FF00 | (color & 0x00FF0000) >>> 16 | (color & 0x000000FF) << 16;
	}
	*///?}

	private static boolean isIceBlock(BlockState state) {
		return state.isOf(Blocks.ICE) || state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.BLUE_ICE);
	}

	private static void paintIce(int px, int py, int dy, int color_change) {
		int alpha = MathHelper.clamp(255 - dy * color_change, 0, 255);
		setArgb(RacingMinimap.image, px, py, alpha << 24 | RacingMinimap.CONFIG.main_color);
	}

	private static boolean isTransparent(BlockState state) {
		return !state.isOpaque() && !state.isOf(Blocks.WATER);
	}

	public static void updateTexture(MinecraftClient client) {
		add_shadows_minimap(client);
		applyCircularMask(RacingMinimap.rounded_image, client);
		RacingMinimap.texture.upload();
	}

	public static void updatePlayers(MinecraftClient client) {
		renderPlayers();
		applyCircularMask(RacingMinimap.image_with_players, client);
		RacingMinimap.players_texture.upload();
	}

	private static boolean isGhost(PlayerEntity player) {
		return player.getName().getString().endsWith(" Ghost");
	}

	private static Identifier headTexture(MinecraftClient client, PlayerEntity player) {
		if (!(player instanceof AbstractClientPlayerEntity clientPlayer)) {
			return null;
		}
		//? if >=1.21.11 {
		/*Identifier id = clientPlayer.getSkin().body().texturePath();
		*///?} else {
		Identifier id = clientPlayer.getSkinTextures().texture();
		//?}
		if (id == null || client.getTextureManager().getTexture(id) == null) {
			return null;
		}
		return id;
	}

	private static void paintPlayer(int spriteX, int spriteZ, int stroke_color, int base_color) {
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		float zoom = dotZoom();
		int max = Math.max(1, Math.round(4 * zoom));
        int halfNarrow = Math.clamp(max, 1, Math.round(2 * zoom));
		int strokeBand = Math.max(1, Math.round(zoom));
		int topBand = max * 3 / 4;
		for (int dy = -max; dy <= max; ++dy) {
			int vertical = Math.abs(dy);
			int halfWidth = vertical >= topBand ? halfNarrow : max;
			for (int dx = -halfWidth; dx <= halfWidth; ++dx) {
				int x = spriteX + dx;
				int y = spriteZ + dy;
				if (x < 0 || x >= width || y < 0 || y >= height) {
					continue;
				}
				int color = (Math.abs(dx) >= halfWidth - strokeBand || vertical >= topBand) ? stroke_color : base_color;
				setArgb(RacingMinimap.image_with_players, x, y, color);
			}
		}
	}

	private static void renderPlayers() {
		List<BoatPlayer> playersInBoats = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null) {
			for (PlayerEntity player : client.world.getPlayers()) {
				if (player == client.player) {
					continue;
				}
				boolean ghost = isGhost(player);
				if (!RacingMinimap.CONFIG.show_ghosts && ghost) {
					continue;
				}
				//? if >=1.21.2 {
				if (player.getVehicle() instanceof AbstractBoatEntity boat && boat.getControllingPassenger() == player) {
					playersInBoats.add(new BoatPlayer(player, ghost));
				}
				//?} else {
				/*if (player.getVehicle() instanceof BoatEntity boat && boat.getControllingPassenger() == player) {
					playersInBoats.add(new BoatPlayer(player, ghost));
				}
				*///?}
			}
		}
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				setArgb(RacingMinimap.image_with_players, x, y, 0);
			}
		}
		playerMarkers.clear();
		int mapX = RacingMinimap.mapBlockPos.getX();
		int mapZ = RacingMinimap.mapBlockPos.getZ();
		int chunkX_min = mapX + 10;
		int chunkX_max = mapX + 230;
		int chunkZ_min = mapZ + 10;
		int chunkZ_max = mapZ + 230;
		for (BoatPlayer entry : playersInBoats) {
			PlayerEntity player = entry.player();
			int playerX = player.getBlockPos().getX();
			int playerZ = player.getBlockPos().getZ();
			if (playerX <= chunkX_min || playerX >= chunkX_max || playerZ <= chunkZ_min || playerZ >= chunkZ_max) {
				continue;
			}
			int spriteX = playerX - chunkX_min + 10;
			int spriteZ = playerZ - chunkZ_min + 10;
			Identifier head = null;
			if (RacingMinimap.CONFIG.player_heads && !entry.ghost()) {
				head = headTexture(client, player);
			}
			playerMarkers.add(new PlayerMarker(player.getName().getString(), spriteX, spriteZ, head));
			if (head == null) {
				int base_color_int = 0xFF000000
					| (entry.ghost() ? RacingMinimap.CONFIG.ghost_color : RacingMinimap.CONFIG.player_color);
				int stroke_color = darken(base_color_int);
				paintPlayer(spriteX, spriteZ, stroke_color, base_color_int);
			}
		}
		boolean renderCrystals = RacingMinimap.CONFIG.render_silly_boxes;
		boolean renderGhosted = RacingMinimap.CONFIG.render_ghosted_players;
		if ((renderCrystals || renderGhosted) && client.world != null && client.player != null) {
			double range = RacingMinimap.render_distance * 8.0 + 16.0;
			Box box = client.player.getBoundingBox().expand(range);
			if (renderCrystals) {
				for (EndCrystalEntity crystal : client.world.getEntitiesByClass(EndCrystalEntity.class, box, entity -> true)) {
					int crystalX = MathHelper.floor(crystal.getX());
					int crystalZ = MathHelper.floor(crystal.getZ());
					int spriteX = crystalX - chunkX_min + 10;
					int spriteZ = crystalZ - chunkZ_min + 10;
					paintDiamond(spriteX, spriteZ);
				}
			}
			if (renderGhosted) {
				int base_color_int = 0xFF000000 | RacingMinimap.CONFIG.ghosted_color;
				int stroke_color = darken(base_color_int);
				for (DisplayEntity display : client.world.getEntitiesByClass(DisplayEntity.class, box, entity -> true)) {
					if (!isStruckLightningRod(display)) {
						continue;
					}
					int displayX = MathHelper.floor(display.getX());
					int displayZ = MathHelper.floor(display.getZ());
					int spriteX = displayX - chunkX_min + 10;
					int spriteZ = displayZ - chunkZ_min + 10;
					paintPlayer(spriteX, spriteZ, stroke_color, base_color_int);
				}
			}
		}
	}

	private static boolean isStruckLightningRod(DisplayEntity display) {
		if (display instanceof DisplayEntity.BlockDisplayEntity blockDisplay) {
			DisplayEntity.BlockDisplayEntity.Data data = blockDisplay.getData();
			if (data == null) {
				return false;
			}
			BlockState state = data.blockState();
			return state.isOf(Blocks.LIGHTNING_ROD) && state.get(LightningRodBlock.POWERED);
		}
		return false;
	}

	private static void paintDiamond(int spriteX, int spriteZ) {
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		int fill_color = 0xFFFF69B4;
		int outline_color = 0xFFAD1457;
		float zoom = dotZoom();
		int r = Math.max(1, Math.round(3 * zoom));
		int strokeBand = Math.max(1, Math.round(zoom));
		for (int dy = -r; dy <= r; ++dy) {
			int halfWidth = r - Math.abs(dy);
			for (int dx = -halfWidth; dx <= halfWidth; ++dx) {
				int x = spriteX + dx;
				int y = spriteZ + dy;
				if (x < 0 || x >= width || y < 0 || y >= height) {
					continue;
				}
				int color = (Math.abs(dx) >= halfWidth - (strokeBand - 1) || Math.abs(dy) == r) ? outline_color : fill_color;
				setArgb(RacingMinimap.image_with_players, x, y, color);
			}
		}
	}

	public static void blitMapToTextureChunk(MinecraftClient client, ChunkPos targetChunkPos, BlockPos playerPos) {
		ClientWorld world = client.world;
		if (world == null || RacingMinimap.texture == null || RacingMinimap.texture.getImage() == null) {
			return;
		}
		int startX = targetChunkPos.getStartX();
		int startZ = targetChunkPos.getStartZ();
		int playerY = playerPos.getY();
		int originX = (targetChunkPos.x - RacingMinimap.chunkLength) * 16;
		int originZ = (targetChunkPos.z - RacingMinimap.chunkLength) * 16;
		RacingMinimap.mapBlockPos = new BlockPos(originX, 0, originZ);
		for (int chunkX = -RacingMinimap.chunkLength; chunkX <= RacingMinimap.chunkLength; ++chunkX) {
			for (int chunkZ = -RacingMinimap.chunkLength; chunkZ <= RacingMinimap.chunkLength; ++chunkZ) {
				boolean foundIceBlockInSquare = true;
				for (int delta = 0; delta <= 7; ++delta) {
					if (foundIceBlockInSquare) {
						foundIceBlockInSquare = analyzeSquare(world, startX, startZ, playerY, chunkX, chunkZ, delta, delta);
					} else {
						paintEmptySquare(chunkX, chunkZ, delta, delta);
					}
				}
			}
		}
		updateTexture(client);
	}

	public static void blitMapToTextureY(MinecraftClient client, ChunkPos targetChunkPos, BlockPos playerPos) {
		ClientWorld world = client.world;
		if (world == null || RacingMinimap.texture == null || RacingMinimap.texture.getImage() == null) {
			return;
		}
		int startX = targetChunkPos.getStartX();
		int startZ = targetChunkPos.getStartZ();
		int playerY = playerPos.getY();
		int originX = (targetChunkPos.x - RacingMinimap.chunkLength) * 16;
		int originZ = (targetChunkPos.z - RacingMinimap.chunkLength) * 16;
		RacingMinimap.mapBlockPos = new BlockPos(originX, 0, originZ);
		for (int chunkX = -RacingMinimap.chunkLength; chunkX <= RacingMinimap.chunkLength; ++chunkX) {
			for (int chunkZ = -RacingMinimap.chunkLength; chunkZ <= RacingMinimap.chunkLength; ++chunkZ) {
				boolean foundIceBlockInSquare = true;
				for (int delta = 0; delta <= RacingMinimap.chunkLength; ++delta) {
					if (foundIceBlockInSquare) {
						foundIceBlockInSquare = analyzeSquare(world, startX, startZ, playerY, chunkX, chunkZ, delta, delta);
					} else {
						paintEmptySquare(chunkX, chunkZ, delta, delta);
					}
				}
			}
		}
		updateTexture(client);
	}

	private static void add_shadows_minimap(MinecraftClient client) {
		float radius = 45.0f * RacingMinimap.CONFIG.ring_size / 100.0f / MinimapRenderer.scale;
		int width = RacingMinimap.rounded_image.getWidth();
		int height = RacingMinimap.rounded_image.getHeight();
		if (client.player == null) {
			return;
		}
		double playerX = client.player.getX();
		double playerZ = client.player.getZ();
		double chunkOffsetX = (playerX % 16.0 + 16.0) % 16.0;
		double chunkOffsetZ = (playerZ % 16.0 + 16.0) % 16.0;
		int centerX = width / 2 - 8 + (int) chunkOffsetX;
		int centerY = height / 2 - 8 + (int) chunkOffsetZ;
		int color_change = (int) Math.ceil(255.0 / RacingMinimap.CONFIG.map_depth);
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int color = getArgb(RacingMinimap.image, x, y);
				setArgb(RacingMinimap.rounded_image, x, y, color);
			}
		}
		float radiusSq = radius * radius;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int color = getArgb(RacingMinimap.image, x, y);
				int color_alpha = color >> 24 & 0xFF;
				if (color_alpha == 0) {
					continue;
				}
				for (int dy = -2; dy <= 2; ++dy) {
					for (int dx = -2; dx <= 2; ++dx) {
						int nx = x + dx;
						int ny = y + dy;
						if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
							continue;
						}
						int distanceX = nx - centerX;
						int distanceY = ny - centerY;
						float distanceSquared = distanceX * distanceX + distanceY * distanceY;
						if (distanceSquared > radiusSq) {
							continue;
						}
						int new_color = getArgb(RacingMinimap.rounded_image, nx, ny);
						int new_color_alpha = new_color >> 24 & 0xFF;
						if (color_alpha - new_color_alpha <= color_change && new_color_alpha != 0) {
							continue;
						}
						int pixel_color = color_alpha << 24 | RacingMinimap.CONFIG.outline_color;
						setArgb(RacingMinimap.rounded_image, nx, ny, pixel_color);
					}
				}
			}
		}
	}

	private static void applyCircularMask(NativeImage image, MinecraftClient client) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (client.player == null) {
			return;
		}
		double playerX = client.player.getX();
		double playerZ = client.player.getZ();
		double chunkOffsetX = (playerX % 16.0 + 16.0) % 16.0;
		double chunkOffsetZ = (playerZ % 16.0 + 16.0) % 16.0;
		int centerX = width / 2 - 8 + (int) chunkOffsetX;
		int centerY = height / 2 - 8 + (int) chunkOffsetZ;
		float ringFactor = RacingMinimap.CONFIG.ring_size / 100.0f;
		float innerRadius = 38.0f * ringFactor / MinimapRenderer.scale;
		float outerRadius = 45.0f * ringFactor / MinimapRenderer.scale;
		float innerRadiusSq = innerRadius * innerRadius;
		float outerRadiusSq = outerRadius * outerRadius;
		float fadeRange = outerRadius - innerRadius;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int dx = x - centerX;
				int dy = y - centerY;
				float distanceSquared = dx * dx + dy * dy;
				if (distanceSquared <= innerRadiusSq) {
					continue;
				}
				if (distanceSquared <= outerRadiusSq) {
					int color = getArgb(image, x, y);
					int alpha = color >> 24 & 0xFF;
					float distance = (float) Math.sqrt(distanceSquared);
					int transparencyFactor = (int) ((distance - innerRadius) / fadeRange * 255.0f);
					int newAlpha = MathHelper.clamp(alpha - transparencyFactor, 0, 255);
					setArgb(image, x, y, newAlpha << 24 | color & 0xFFFFFF);
					continue;
				}
				setArgb(image, x, y, 0);
			}
		}
	}

	private static boolean analyzeXZ(World world, int x, int z, int playerY, int px, int py) {
		boolean foundIceBlock = false;
		int map_depth = RacingMinimap.CONFIG.map_depth;
		int color_change = (int) Math.ceil(255.0 / map_depth);

		mutablePos.set(x, playerY, z);
		BlockState surfaceState = world.getBlockState(mutablePos);
		if (isIceBlock(surfaceState)) {
			mutablePos.set(x, playerY + 1, z);
			if (isTransparent(world.getBlockState(mutablePos))) {
				paintIce(px, py, 0, 0);
				foundIceBlock = true;
			}
		}

		if (!foundIceBlock) {
			for (int dy = 1; dy <= map_depth; ++dy) {
				int currentY = playerY - dy;
				mutablePos.set(x, currentY, z);
				BlockState foundState = world.getBlockState(mutablePos);
				if (!isIceBlock(foundState)) {
					continue;
				}
				mutablePos.set(x, currentY + 1, z);
				if (!isTransparent(world.getBlockState(mutablePos))) {
					continue;
				}
				paintIce(px, py, dy, color_change);
				foundIceBlock = true;
				break;
			}
		}

		if (!foundIceBlock) {
			for (int dy = 1; dy <= map_depth; ++dy) {
				int currentY = playerY + dy;
				mutablePos.set(x, currentY, z);
				BlockState foundState = world.getBlockState(mutablePos);
				if (!isIceBlock(foundState)) {
					continue;
				}
				mutablePos.set(x, currentY + 1, z);
				if (!isTransparent(world.getBlockState(mutablePos))) {
					continue;
				}
				paintIce(px, py, dy, color_change);
				foundIceBlock = true;
				break;
			}
		}

		if (!foundIceBlock) {
			setArgb(RacingMinimap.image, px, py, 0);
		}
		return foundIceBlock;
	}

	private static void paintEmptyXZ(int px, int py) {
		setArgb(RacingMinimap.image, px, py, 0);
	}

	private static boolean analyzeSquare(World world, int playerX, int playerZ, int playerY, int chunkX, int chunkZ, int blockX, int blockZ) {
		boolean foundIceBlockInSquare = false;
		int sideLength = 16 - blockX * 2;
		for (int deltaX = 0; deltaX < sideLength; ++deltaX) {
			int x = playerX + (chunkX * 16 + blockX + deltaX);
			int z1 = playerZ + (chunkZ * 16 + blockZ);
			int z2 = playerZ + (chunkZ * 16 + (15 - blockZ));
			int px = (chunkX + RacingMinimap.chunkLength) * 16 + blockX + deltaX;
			int py1 = (chunkZ + RacingMinimap.chunkLength) * 16 + blockZ;
			int py2 = (chunkZ + RacingMinimap.chunkLength) * 16 + (15 - blockZ);
			boolean foundIceBlockInXZ1 = analyzeXZ(world, x, z1, playerY, px, py1);
			boolean foundIceBlockInXZ2 = analyzeXZ(world, x, z2, playerY, px, py2);
			if (foundIceBlockInXZ1 || foundIceBlockInXZ2) {
				foundIceBlockInSquare = true;
			}
		}
		for (int deltaZ = 0; deltaZ < sideLength; ++deltaZ) {
			int z = playerZ + (chunkZ * 16 + blockZ + deltaZ);
			int x1 = playerX + (chunkX * 16 + blockX);
			int x2 = playerX + (chunkX * 16 + (15 - blockX));
			int py = (chunkZ + RacingMinimap.chunkLength) * 16 + blockZ + deltaZ;
			int px1 = (chunkX + RacingMinimap.chunkLength) * 16 + blockX;
			int px2 = (chunkX + RacingMinimap.chunkLength) * 16 + (15 - blockX);
			boolean foundIceBlockInXZ1 = analyzeXZ(world, x1, z, playerY, px1, py);
			boolean foundIceBlockInXZ2 = analyzeXZ(world, x2, z, playerY, px2, py);
			if (foundIceBlockInXZ1 || foundIceBlockInXZ2) {
				foundIceBlockInSquare = true;
			}
		}
		return foundIceBlockInSquare;
	}

	private static void paintEmptySquare(int chunkX, int chunkZ, int blockX, int blockZ) {
		int sideLength = 16 - blockX * 2;
		for (int deltaX = 0; deltaX < sideLength; ++deltaX) {
			int px = (chunkX + RacingMinimap.chunkLength) * 16 + blockX + deltaX;
			int py1 = (chunkZ + RacingMinimap.chunkLength) * 16 + blockZ;
			int py2 = (chunkZ + RacingMinimap.chunkLength) * 16 + (15 - blockZ);
			paintEmptyXZ(px, py1);
			paintEmptyXZ(px, py2);
		}
		for (int deltaZ = 0; deltaZ < sideLength; ++deltaZ) {
			int py = (chunkZ + RacingMinimap.chunkLength) * 16 + blockZ + deltaZ;
			int px1 = (chunkX + RacingMinimap.chunkLength) * 16 + blockX;
			int px2 = (chunkX + RacingMinimap.chunkLength) * 16 + (15 - blockX);
			paintEmptyXZ(px1, py);
			paintEmptyXZ(px2, py);
		}
	}
}
