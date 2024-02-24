package dev.userteemu.fakeplayerfishingfixes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

@Mixin(FishingHook.class)
public class FishingHookMixinClient {
	// Skips the check for a non-null owner.
	@WrapOperation(method = "recreateFromPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;getPlayerOwner()Lnet/minecraft/world/entity/player/Player;"))
	private Player shouldIgnore(FishingHook instance, Operation<Player> original) {
		return Minecraft.getInstance().player; // returns some player (the client player) that is not null
	}

	// Prevents the fishing hook rom being discarded by clients.
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;getPlayerOwner()Lnet/minecraft/world/entity/player/Player;"))
	private Player modifyPlayer(FishingHook instance, Operation<Player> original) {
		if (instance.level().isClientSide()) {
			// The first if branch should be skipped always client-side.
			// If the player is null, we set it to something not null (the client player), so it won't match.
			Player p = original.call(instance);
			return p != null ? p : Minecraft.getInstance().player;
		} else {
			return original.call(instance);
		}
	}
}
