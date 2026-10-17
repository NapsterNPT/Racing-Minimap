package net.napsternpt.racingminimap.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.napsternpt.racingminimap.RacingMinimap;
import org.jetbrains.annotations.NotNull;

@Config(name = RacingMinimap.MOD_ID)
public class ModConfig implements ConfigData {

	@ConfigEntry.Category("general")
	public boolean render_map = true;

	@ConfigEntry.Category("general")
	public boolean render_out_of_boat = false;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int map_x = 0;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int map_y = 0;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 25, max = 100)
	public int map_scale = 45;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 25, max = 200)
	public int ring_size = 100;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	public int map_opacity = 100;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.ColorPicker()
	public int main_color = 0x1E1E1E;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.ColorPicker()
	public int outline_color = 0xFFFFFF;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.ColorPicker()
	public int player_color = 0xFF0000;

	@ConfigEntry.Category("appearance")
	public boolean player_heads = false;

	@ConfigEntry.Category("appearance")
	public boolean show_player_nametags = true;

	@ConfigEntry.Category("appearance")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 200)
	public int nametag_scale = 100;

	@ConfigEntry.Category("appearance")
	public NametagAnchor nametag_anchor = NametagAnchor.TOP;

	public enum NametagAnchor implements SelectionListEntry.Translatable {
		TOP,
		BOTTOM;

		@Override
		public @NotNull String getKey() {
			return "text.autoconfig.racingminimap.option.nametag_anchor." + name();
		}
	}

	@ConfigEntry.Category("performance")
	@ConfigEntry.Gui.Tooltip(count = 2)
	@ConfigEntry.BoundedDiscrete(min = 1, max = 32)
	public int map_depth = 10;

	@ConfigEntry.Category("performance")
	@ConfigEntry.Gui.Tooltip(count = 2)
	public int map_fps = 30;

	@ConfigEntry.Category("boatlabs")
	public boolean show_ghosts = true;

	@ConfigEntry.Category("boatlabs")
	@ConfigEntry.ColorPicker()
	public int ghost_color = 0x808080;

	@ConfigEntry.Category("boatlabs")
	public boolean render_silly_boxes = true;
}