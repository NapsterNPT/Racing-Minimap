package net.napsternpt.racingminimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MapHelper {
	private static final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

	private static boolean isIceBlock(BlockState state) {
		return state.isOf(Blocks.ICE) || state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.BLUE_ICE);
	}

	private static void paintIce(int px, int py, int dy, int color_change) {
		int alpha = MathHelper.clamp(255 - dy * color_change, 0, 255);
		RacingMinimap.image.setColorArgb(px, py, alpha << 24);
	}

	private static boolean isTransparent(World world, BlockState state) {
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

	private static void paintPlayer(int spriteX, int spriteZ, int stroke_color, int base_color, int detail_color) {
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		for (int dy = -4; dy <= 4; ++dy) {
			int halfWidth = (dy <= -3 || dy >= 3) ? 2 : 4;
			for (int dx = -halfWidth; dx <= halfWidth; ++dx) {
				int x = spriteX + dx;
				int y = spriteZ + dy;
				if (x < 0 || x >= width || y < 0 || y >= height) {
					continue;
				}
				int color = (Math.abs(dx) >= halfWidth - 1 || Math.abs(dy) >= 3) ? stroke_color : base_color;
				RacingMinimap.image_with_players.setColorArgb(x, y, color);
			}
		}
	}

	private static void renderPlayers() {
		List<PlayerEntity> playersInBoats = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null) {
			for (PlayerEntity player : client.world.getPlayers()) {
				if (player == client.player) {
					continue;
				}
				if (player.getVehicle() instanceof BoatEntity boat && boat.getControllingPassenger() == player) {
					playersInBoats.add(player);
				}
			}
		}
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				RacingMinimap.image_with_players.setColorArgb(x, y, 0);
			}
		}
		int mapX = RacingMinimap.mapBlockPos.getX();
		int mapZ = RacingMinimap.mapBlockPos.getZ();
		int chunkX_min = mapX + 10;
		int chunkX_max = mapX + 230;
		int chunkZ_min = mapZ + 10;
		int chunkZ_max = mapZ + 230;
		for (PlayerEntity player : playersInBoats) {
			int playerX = player.getBlockPos().getX();
			int playerZ = player.getBlockPos().getZ();
			if (playerX <= chunkX_min || playerX >= chunkX_max || playerZ <= chunkZ_min || playerZ >= chunkZ_max) {
				continue;
			}
			int spriteX = playerX - chunkX_min + 10;
			int spriteZ = playerZ - chunkZ_min + 10;
			int base_color_int = 0xFFFF0000;
			int r = base_color_int >> 16 & 0xFF;
            int r1 = (int) (r * 0.7);
            int b1 = 0;
			int stroke_color = 0xFF000000 | r1 << 16 | b1;
            paintPlayer(spriteX, spriteZ, stroke_color, base_color_int, base_color_int);
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
		float radius = 45.0f / MinimapRenderer.scale;
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
				int color = RacingMinimap.image.getColorArgb(x, y);
				RacingMinimap.rounded_image.setColorArgb(x, y, color);
			}
		}
		float radiusSq = radius * radius;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int color = RacingMinimap.image.getColorArgb(x, y);
				int color_alpha = color >> 24 & 0xFF;
				int color_without_alpha = color & 0xFFFFFF;
				if (color_without_alpha != 0) {
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
						int new_color = RacingMinimap.rounded_image.getColorArgb(nx, ny);
						int new_color_alpha = new_color >> 24 & 0xFF;
						if (color_alpha - new_color_alpha <= color_change && new_color_alpha != 0) {
							continue;
						}
						int pixel_color = color_alpha << 24 | 0xFFFFFF;
						RacingMinimap.rounded_image.setColorArgb(nx, ny, pixel_color);
					}
				}
			}
		}
	}

	private static void applyCircularMask(net.minecraft.client.texture.NativeImage image, MinecraftClient client) {
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
		float innerRadius = 38.0f / MinimapRenderer.scale;
		float outerRadius = 45.0f / MinimapRenderer.scale;
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
					int color = image.getColorArgb(x, y);
					int alpha = color >> 24 & 0xFF;
					float distance = (float) Math.sqrt(distanceSquared);
					int transparencyFactor = (int) ((distance - innerRadius) / fadeRange * 255.0f);
					int newAlpha = MathHelper.clamp(alpha - transparencyFactor, 0, 255);
					image.setColorArgb(x, y, newAlpha << 24 | color & 0xFFFFFF);
					continue;
				}
				image.setColorArgb(x, y, 0);
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
			if (isTransparent(world, world.getBlockState(mutablePos))) {
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
				if (!isTransparent(world, world.getBlockState(mutablePos))) {
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
				if (!isTransparent(world, world.getBlockState(mutablePos))) {
					continue;
				}
				paintIce(px, py, dy, color_change);
				foundIceBlock = true;
				break;
			}
		}

		if (!foundIceBlock) {
			RacingMinimap.image.setColorArgb(px, py, 0);
		}
		return foundIceBlock;
	}

	private static void paintEmptyXZ(int px, int py) {
		RacingMinimap.image.setColorArgb(px, py, 0);
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
