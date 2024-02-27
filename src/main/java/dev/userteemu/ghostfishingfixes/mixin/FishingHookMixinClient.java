package dev.userteemu.ghostfishingfixes.mixin;

import dev.userteemu.ghostfishingfixes.GhostFishingFixesConfig;
import dev.userteemu.ghostfishingfixes.FishingRodOwnerPos;
import dev.userteemu.ghostfishingfixes.interfaces.FishingHookOwnerPosInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	// If vanilla code does not pull the entity because there is no owner, this code does it using the owner position.
	// This only works client-side because the server should never have to resort to this
	// because it always knows who the owner is, even if it's a ghost player, and might have more accurate position data, too.
	@Inject(method = "pullEntity", at = @At("HEAD"))
	private void pullEntity(Entity entity, CallbackInfo ci) {
		if (!entity.level().isClientSide() || !GhostFishingFixesConfig.INSTANCE.allowPullingPlayers) return;

		FishingHook instance = ((FishingHook)((Object)this));
		FishingRodOwnerPos ownerPos = ((FishingHookOwnerPosInterface) instance).getOwnerPos();
		if (instance.getOwner() == null && ownerPos != null) {
			// Run same code as vanilla but using ownerPos.
			Vec3 vec3 = new Vec3(ownerPos.x - instance.getX(), ownerPos.y - instance.getY(), ownerPos.z - instance.getZ()).scale(0.1);
			entity.setDeltaMovement(entity.getDeltaMovement().add(vec3));
		}
	}
}
