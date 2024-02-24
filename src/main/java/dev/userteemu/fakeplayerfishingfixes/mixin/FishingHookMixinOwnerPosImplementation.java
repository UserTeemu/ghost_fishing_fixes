package dev.userteemu.fakeplayerfishingfixes.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import dev.userteemu.fakeplayerfishingfixes.FishingRodOwnerPos;
import dev.userteemu.fakeplayerfishingfixes.interfaces.FishingHookOwnerPosInterface;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Unique;

@Mixin(FishingHook.class)
public class FishingHookMixinOwnerPosImplementation implements FishingHookOwnerPosInterface {
	@Unique
	private FishingRodOwnerPos ownerPos = null;

	@Override
	@Nullable
	public FishingRodOwnerPos getOwnerPos() {
		return this.ownerPos;
	}

	@Override
	public void setOwnerPos(FishingRodOwnerPos ownerPos) {
		this.ownerPos = ownerPos;
	}
}
