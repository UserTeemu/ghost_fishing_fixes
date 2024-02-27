package dev.userteemu.ghostfishingfixes;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ghost_fishing_fixes")
public class GhostFishingFixesConfig implements ConfigData {
	@ConfigEntry.Gui.Excluded
	public static GhostFishingFixesConfig INSTANCE;

	@ConfigEntry.Gui.Tooltip(count = 2)
	public boolean allowPullingPlayers = true;
}
