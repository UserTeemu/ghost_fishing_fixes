package dev.userteemu.fakeplayerfishingfixes.mixin;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.userteemu.fakeplayerfishingfixes.FishingRodOwnerPos;
import dev.userteemu.fakeplayerfishingfixes.interfaces.FishingHookOwnerPosInterface;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.FishingHook;

// Renders the fishing hook using the owner position.
@Mixin(FishingHookRenderer.class)
public abstract class FishingHookRendererMixin extends EntityRenderer<FishingHook> {
	protected FishingHookRendererMixin(EntityRendererProvider.Context context) {
		super(context);
	}

	@Final
	@Shadow
	private static RenderType RENDER_TYPE;

	@Shadow
	private static float fraction(int numerator, int denominator) { return 0f; }

	@Shadow
	private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int lightmapUV, float x, int y, int u, int v) {}

	@Shadow
	private static void stringVertex(float x, float y, float z, VertexConsumer consumer, PoseStack.Pose pose, float startPercent, float endPercent) {}

	@Shadow
	public ResourceLocation getTextureLocation(FishingHook entity) { return null; }

	@Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
	public void a(FishingHook entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if (entity.getPlayerOwner() != null) return; // Run only in the case that the normal code doesn't run.

		FishingRodOwnerPos ownerPos = ((FishingHookOwnerPosInterface)entity).getOwnerPos();
		if (ownerPos == null) return;

		// Modified version of the vanilla code to render the bobber and the fishing line but using the positions provided by ownerPos.

		poseStack.pushPose();

		// Render bobber

		poseStack.pushPose();
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix4f = pose.pose();
		Matrix3f matrix3f = pose.normal();
		VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
		vertex(vertexConsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
		vertex(vertexConsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
		vertex(vertexConsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
		vertex(vertexConsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
		poseStack.popPose();

		// Render fishing line

		float x = (float)(ownerPos.x - Mth.lerp(partialTicks, entity.xo, entity.getX()));
		float y = (float)(ownerPos.y - Mth.lerp(partialTicks, entity.yo, entity.getY()) - 0.25);
		float z = (float)(ownerPos.z - Mth.lerp(partialTicks, entity.zo, entity.getZ()));
		VertexConsumer vertexConsumer2 = buffer.getBuffer(RenderType.lineStrip());
		PoseStack.Pose pose2 = poseStack.last();
		for (int w = 0; w <= 16; ++w) {
			stringVertex(x, y, z, vertexConsumer2, pose2, fraction(w, 16), fraction(w + 1, 16));
		}
		poseStack.popPose();

		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
