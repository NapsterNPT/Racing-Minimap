package net.napsternpt.racingminimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MapHelper {
	private static boolean isIceBlock(BlockState state) {
		return state.isOf(Blocks.ICE) || state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.BLUE_ICE);
	}

	private static void paintIce(int px, int py, int dy, int color_change) {
		int alpha = MathHelper.clamp(255 - dy * color_change, 0, 255);
		RacingMinimap.image.setColorArgb(px, py, alpha << 24);
	}

	private static boolean isTransparent(World world, BlockState state, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (!state.isSideSolidFullSquare(world, pos, direction) || state.isOf(Blocks.WATER)) {
				continue;
			}
			return false;
		}
		return true;
	}

	public static List<PlayerEntity> getPlayersInBoats() {
		List<PlayerEntity> playersInBoats = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.world == null) {
			return playersInBoats;
		}
		for (PlayerEntity player : client.world.getPlayers()) {
			if (player == client.player) {
				continue;
			}
			Entity vehicle = player.getVehicle();
			if (!(vehicle instanceof BoatEntity boat)) {
				continue;
			}
			if (boat.getControllingPassenger() == player) {
				playersInBoats.add(player);
			}
		}
		return playersInBoats;
	}

	public static void updateTexture(MinecraftClient client) {
		add_shadows_minimap(client);
		round_minimap(client);
		RacingMinimap.texture.upload();
	}

	public static void updatePlayers(MinecraftClient client) {
		renderPlayers();
		round_minimap_players(client);
		RacingMinimap.players_texture.upload();
	}

	private static void paintPlayer(int spriteX, int spriteZ, int stroke_color, int base_color, int detail_color) {
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ - 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ - 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ - 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ - 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ - 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ - 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ - 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ - 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ - 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ - 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 4, spriteZ - 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 3, spriteZ - 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ - 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ - 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ - 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ - 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ - 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 3, spriteZ - 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 4, spriteZ - 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 4, spriteZ - 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 3, spriteZ - 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ - 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ - 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ - 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ - 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ - 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 3, spriteZ - 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 4, spriteZ - 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 4, spriteZ, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 3, spriteZ, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 3, spriteZ, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 4, spriteZ, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 4, spriteZ + 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 3, spriteZ + 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ + 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ + 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ + 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ + 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ + 1, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 3, spriteZ + 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 4, spriteZ + 1, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 4, spriteZ + 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 3, spriteZ + 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ + 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ + 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ + 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ + 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ + 2, base_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 3, spriteZ + 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 4, spriteZ + 2, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ + 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ + 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ + 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ + 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ + 3, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 2, spriteZ + 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX - 1, spriteZ + 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX, spriteZ + 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 1, spriteZ + 4, stroke_color);
		RacingMinimap.image_with_players.setColorArgb( spriteX + 2, spriteZ + 4, stroke_color);
	}

	private static void renderPlayers() {
		List<PlayerEntity> playersInBoats = getPlayersInBoats();
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				RacingMinimap.image_with_players.setColorArgb(x, y, 0);
			}
		}
		int chunkX_min = RacingMinimap.mapBlockPos.getX() + 10;
		int chunkX_max = RacingMinimap.mapBlockPos.getX() + 230;
		int chunkZ_min = RacingMinimap.mapBlockPos.getZ() + 10;
		int chunkZ_max = RacingMinimap.mapBlockPos.getZ() + 230;
		for (PlayerEntity player : playersInBoats) {
			int playerX = player.getBlockPos().getX();
			int playerZ = player.getBlockPos().getZ();
			if (playerX <= chunkX_min || playerX >= chunkX_max || playerZ <= chunkZ_min || playerZ >= chunkZ_max) {
				continue;
			}
			int spriteX = playerX - chunkX_min + 10;
			int spriteZ = playerZ - chunkZ_min + 10;
			String base_color = RacingMinimap.team_colors.get(player.getName().getString());
			int base_color_int;
			try {
				base_color_int = base_color != null ? 0xFF000000 | Integer.parseInt(base_color.substring(1), 16) : 0xFFFF0000;
			} catch (NumberFormatException e) {
				base_color_int = 0xFFFF0000;
			}
			int r = base_color_int >> 16 & 0xFF;
			int g = base_color_int >> 8 & 0xFF;
			int b = base_color_int & 0xFF;
			int r1 = (int) (r * 0.7);
			int g1 = (int) (g * 0.7);
			int b1 = (int) (b * 0.7);
			int stroke_color = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
			base_color_int = 0xFF000000 | r << 16 | g << 8 | b;
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
		ChunkPos newChunkPos = new ChunkPos(targetChunkPos.x - RacingMinimap.chunkLength, targetChunkPos.z - RacingMinimap.chunkLength);
		RacingMinimap.mapBlockPos = new BlockPos(newChunkPos.getStartX(), 0, newChunkPos.getStartZ());
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
		ChunkPos newChunkPos = new ChunkPos(targetChunkPos.x - RacingMinimap.chunkLength, targetChunkPos.z - RacingMinimap.chunkLength);
		RacingMinimap.mapBlockPos = new BlockPos(newChunkPos.getStartX(), 0, newChunkPos.getStartZ());
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
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int color = RacingMinimap.rounded_image.getColorArgb(x, y);
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
						if (distanceSquared > radius * radius) {
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

	private static void round_minimap(MinecraftClient client) {
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
		float innerRadius = 38.0f / MinimapRenderer.scale;
		float outerRadius = 45.0f / MinimapRenderer.scale;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int dx = x - centerX;
				int dy = y - centerY;
				float distanceSquared = dx * dx + dy * dy;
				if (distanceSquared <= innerRadius * innerRadius) {
					continue;
				}
				if (distanceSquared <= outerRadius * outerRadius) {
					int color = RacingMinimap.rounded_image.getColorArgb(x, y);
					int alpha = color >> 24 & 0xFF;
					float distance = (float) Math.sqrt(distanceSquared);
					int transparencyFactor = (int) ((distance - innerRadius) / (outerRadius - innerRadius) * 255.0f);
					int newAlpha = MathHelper.clamp(alpha - transparencyFactor, 0, 255);
					RacingMinimap.rounded_image.setColorArgb(x, y, newAlpha << 24 | color & 0xFFFFFF);
					continue;
				}
				RacingMinimap.rounded_image.setColorArgb(x, y, 0);
			}
		}
	}

	private static void round_minimap_players(MinecraftClient client) {
		int width = RacingMinimap.image_with_players.getWidth();
		int height = RacingMinimap.image_with_players.getHeight();
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
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int dx = x - centerX;
				int dy = y - centerY;
				float distanceSquared = dx * dx + dy * dy;
				if (distanceSquared <= innerRadius * innerRadius) {
					continue;
				}
				if (distanceSquared <= outerRadius * outerRadius) {
					int color = RacingMinimap.image_with_players.getColorArgb(x, y);
					int alpha = color >> 24 & 0xFF;
					float distance = (float) Math.sqrt(distanceSquared);
					int transparencyFactor = (int) ((distance - innerRadius) / (outerRadius - innerRadius) * 255.0f);
					int newAlpha = MathHelper.clamp(alpha - transparencyFactor, 0, 255);
					RacingMinimap.image_with_players.setColorArgb(x, y, newAlpha << 24 | color & 0xFFFFFF);
					continue;
				}
				RacingMinimap.image_with_players.setColorArgb(x, y, 0);
			}
		}
	}

	private static boolean analyzeXZ(World world, int x, int z, int playerY, int px, int py) {
		boolean foundIceBlock = false;
		BlockState surfaceState = world.getBlockState(new BlockPos(x, playerY, z));
		if (isIceBlock(surfaceState)
				&& isTransparent(world, world.getBlockState(new BlockPos(x, playerY + 1, z)), new BlockPos(x, playerY + 1, z))) {
			paintIce(px, py, 0, 0);
			foundIceBlock = true;
		}
		int map_depth = RacingMinimap.CONFIG.map_depth;
		int color_change = (int) Math.ceil(255.0 / map_depth);
		if (!foundIceBlock) {
			for (int dy = 1; dy <= map_depth; ++dy) {
				int currentY = playerY - dy;
				BlockState foundState = world.getBlockState(new BlockPos(x, currentY, z));
				if (!isIceBlock(foundState)
						|| !isTransparent(world, world.getBlockState(new BlockPos(x, currentY + 1, z)), new BlockPos(x, currentY + 1, z))) {
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
				BlockState foundState = world.getBlockState(new BlockPos(x, currentY, z));
				if (!isIceBlock(foundState)
						|| !isTransparent(world, world.getBlockState(new BlockPos(x, currentY + 1, z)), new BlockPos(x, currentY + 1, z))) {
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