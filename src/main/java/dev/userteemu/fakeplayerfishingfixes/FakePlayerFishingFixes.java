package dev.userteemu.fakeplayerfishingfixes;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.projectile.FishingHook;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.userteemu.fakeplayerfishingfixes.interfaces.FishingHookOwnerPosInterface;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class FakePlayerFishingFixes implements ModInitializer, ClientModInitializer {
	public static final String ID = "fake_player_fishing_fixes";
	public static final String NAME = "Fake Player Fishing Fixes";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static boolean PORTING_LIB_FOUND = false;

	public static final ResourceLocation FISHING_ROD_OWNER_POS_PACKET = new ResourceLocation(ID, "owner_pos");

	@Override
	public void onInitialize() {
		try {
			Class.forName("io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer");
			PORTING_LIB_FOUND = true;
		} catch (ClassNotFoundException e) {
			LOGGER.info("Create Porting Lib not found.");
			PORTING_LIB_FOUND = false;
		}
	}

	@Override
	public void onInitializeClient() {
		// Register packet receiver
		ClientPlayNetworking.registerGlobalReceiver(FISHING_ROD_OWNER_POS_PACKET, this::setRodOwnerPos);

		// Initialize config
		AutoConfig.register(FakePlayerFishingFixesConfig.class, JanksonConfigSerializer::new);
		FakePlayerFishingFixesConfig.INSTANCE = AutoConfig.getConfigHolder(FakePlayerFishingFixesConfig.class).getConfig();
	}

	public static boolean isFakePlayer(ServerPlayer player) {
		return (
				player instanceof net.fabricmc.fabric.api.entity.FakePlayer ||
				(PORTING_LIB_FOUND && player instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer)
		);
	}

	public void setRodOwnerPos(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		if (client.level == null) return;

		Tuple<Integer, @Nullable FishingRodOwnerPos> parsed = FishingRodOwnerPos.fromBuf(buf);

		Entity entity = client.level.getEntity(parsed.getA());
		if (!(entity instanceof FishingHookOwnerPosInterface)) {
			return;
		}

		((FishingHookOwnerPosInterface)entity).setOwnerPos(parsed.getB());
	}

	public static void updateAndNotifyClients(ServerPlayer fakePlayer, FishingHook fishingHook, ServerPlayer... clients) {
		if (clients.length == 0) return;

		FishingRodOwnerPos ownerPos = ((FishingHookOwnerPosInterface)fishingHook).getOwnerPos();

		if (ownerPos != null && ownerPos.isUpToDate(fakePlayer)) {
			return; // Clients are aware of everything already.
		}

		// Update the owner position
		((FishingHookOwnerPosInterface)fishingHook).setOwnerPos(ownerPos = FishingRodOwnerPos.fromPlayer(fakePlayer));

		// Notify about the change.
		for (ServerPlayer client : clients) {
			ServerPlayNetworking.send(client, FISHING_ROD_OWNER_POS_PACKET, ownerPos.toBuf(fishingHook.getId()));
		}
	}

	public static void unloadFromClients(FishingHook fishingHook, ServerPlayer... clients) {
		for (ServerPlayer client : clients) {
			ServerPlayNetworking.send(client, FISHING_ROD_OWNER_POS_PACKET, FishingRodOwnerPos.unknownToBuf(fishingHook.getId()));
		}
	}
}
