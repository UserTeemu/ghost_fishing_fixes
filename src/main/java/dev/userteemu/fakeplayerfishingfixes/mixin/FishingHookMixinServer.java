package dev.userteemu.fakeplayerfishingfixes.mixin;

import java.util.HashSet;
import java.util.Set;

import dev.userteemu.fakeplayerfishingfixes.interfaces.FishingHookOwnerPosInterface;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.userteemu.fakeplayerfishingfixes.FakePlayerFishingFixes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

// Handles notifying relevant clients about the rod's owner.
@Mixin(FishingHook.class)
public abstract class FishingHookMixinServer extends Entity {
	// Players that should be notified about changes to the owner position of this fishing hook.
	@Unique
	private Set<ServerPlayer> subscriberPlayers = null;

	@Unique
	private boolean isOwnedByFakePlayer = false;

	public FishingHookMixinServer(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "setOwner", at = @At("HEAD"))
	private void setOwner(Entity owner, CallbackInfo ci) {
		if (owner.level().isClientSide()) return;

		if (owner instanceof ServerPlayer && FakePlayerFishingFixes.isFakePlayer((ServerPlayer) owner)) {
			subscriberPlayers = new HashSet<>();
			isOwnedByFakePlayer = true;
		} else {
			isOwnedByFakePlayer = false;
			((FishingHookOwnerPosInterface) this).setOwnerPos(null);
			FakePlayerFishingFixes.unloadFromClients((FishingHook)(Object) this, subscriberPlayers.toArray(new ServerPlayer[0]));
			subscriberPlayers.clear();
		}

	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void tick(CallbackInfo ci) {
		if (!isOwnedByFakePlayer) return;

		FishingHook instance = (FishingHook)(Object) this;

		if (instance.level().isClientSide()) return; // This code should run only server-side.

		FakePlayerFishingFixes.updateAndNotifyClients((ServerPlayer) instance.getPlayerOwner(), instance, subscriberPlayers.toArray(new ServerPlayer[0]));
	}

	@Override
	public void startSeenByPlayer(ServerPlayer serverPlayer) {
		super.startSeenByPlayer(serverPlayer);
		if (isOwnedByFakePlayer) {
			subscriberPlayers.add(serverPlayer);
		}
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer serverPlayer) {
		super.stopSeenByPlayer(serverPlayer);
		if (isOwnedByFakePlayer) {
			subscriberPlayers.remove(serverPlayer);
			FakePlayerFishingFixes.unloadFromClients((FishingHook)(Object) this, serverPlayer);
		}
	}
}
