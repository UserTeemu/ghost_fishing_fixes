package dev.userteemu.ghostfishingfixes;

import net.fabricmc.loader.api.FabricLoader;

public class GhostFishingFixesCompatibilityUtil {
	public static Class<?> PORTING_LIB_FAKE_PLAYER_CLASS = null;
	public static boolean USING_IRIS = false;

	public static void init() {
		try {
			PORTING_LIB_FAKE_PLAYER_CLASS = Class.forName("io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer");
		} catch (ClassNotFoundException e) {
			GhostFishingFixes.LOGGER.info("Create Porting Lib not found.");
			PORTING_LIB_FAKE_PLAYER_CLASS = null;
		}

		USING_IRIS = FabricLoader.getInstance().isModLoaded("iris");
	}
}
