package dev.userteemu.ghostfishingfixes;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class FishingRodOwnerPos {
	public double x;
	public double y;
	public double z;

	public FishingRodOwnerPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean isUpToDate(Player player) {
		Vec3 pos = player.position();
		return new Vec3(this.x, this.y, this.z).distanceTo(pos) < 0.001F; // Refresh the position if it has moved more than 0.1 (arbitrary number) blocks.
	}

	public FriendlyByteBuf toBuf(int hookId) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(hookId);
		buf.writeBoolean(true); // Indicates whether the pos is known
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		return buf;
	}

	public static FriendlyByteBuf unknownToBuf(int hookId) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(hookId);
		buf.writeBoolean(false); // Indicates whether the pos is known
		return buf;
	}

	public static Tuple<Integer, @Nullable FishingRodOwnerPos> fromBuf(FriendlyByteBuf buf) {
		int entity = buf.readInt();
		FishingRodOwnerPos ownerPos = buf.readBoolean() ? new FishingRodOwnerPos(buf.readDouble(), buf.readDouble(), buf.readDouble()) : null;
		return new Tuple<>(entity, ownerPos);
	}

	public static FishingRodOwnerPos fromPlayer(Player player) {
		Vec3 pos = player.position();
		return new FishingRodOwnerPos(pos.x, pos.y, pos.z);
	}
}
