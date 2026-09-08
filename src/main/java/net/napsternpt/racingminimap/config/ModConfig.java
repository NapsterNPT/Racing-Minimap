package net.napsternpt.racingminimap.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.napsternpt.racingminimap.RacingMinimap;

@Config(name = RacingMinimap.MOD_ID)
public class ModConfig implements ConfigData {

	public boolean render_map = true;

	public boolean render_out_of_boat = false;

	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int map_x = 0;

	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int map_y = 0;

	@ConfigEntry.BoundedDiscrete(min = 25, max = 100)
	public int map_scale = 45;

	@ConfigEntry.Gui.Tooltip(count = 2)
	@ConfigEntry.BoundedDiscrete(min = 1, max = 32)
	public int map_depth = 10;
}