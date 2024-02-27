package dev.userteemu.ghostfishingfixes.interfaces;

import dev.userteemu.ghostfishingfixes.FishingRodOwnerPos;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public interface FishingHookOwnerPosInterface {
	@Nullable
	default FishingRodOwnerPos getOwnerPos() {
		throw new NotImplementedException("FishingHookOwnerPosInterface#getOwnerPos not implemented");
	}

	default void setOwnerPos(@Nullable FishingRodOwnerPos ownerPos) {
		throw new NotImplementedException("FishingHookOwnerPosInterface#setOwnerPos not implemented");
	}
}
