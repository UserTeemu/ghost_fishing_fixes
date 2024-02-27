package dev.userteemu.fakeplayerfishingfixes;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "fake_player_fishing_fixes")
public class FakePlayerFishingFixesConfig implements ConfigData {
	@ConfigEntry.Gui.Excluded
	public static FakePlayerFishingFixesConfig INSTANCE;

	@ConfigEntry.Gui.Tooltip(count = 2)
	public boolean allowPullingPlayers = true;
}
