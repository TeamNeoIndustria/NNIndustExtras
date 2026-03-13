package net.torchednova.nnindustextras.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.torchednova.nnindustextras.NNIndustExtras.LOGGER;

@Mixin(targets = "immersive_paintings.entity.ImmersivePaintingEntity")
public abstract class immersivepaintingmixin {

	@Shadow
	public abstract void addAdditionalSaveData(CompoundTag nbt);

	@Shadow
	public abstract void readAdditionalSaveData(CompoundTag nbt);

	@Unique
	private String owner = "";

	@Unique
	private CompoundTag ct;

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

		if (player.level().isClientSide() == true) return;

		if (owner.isEmpty())
		{
			owner = player.getStringUUID();
			LOGGER.info("new owner");
		}
		if (owner.isEmpty() && !owner.equals(player.getStringUUID()))
		{
			LOGGER.info("Canceled");
			cir.cancel();
		}

		LOGGER.info("Okay go ahead");
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void saveOwner(CompoundTag tag, CallbackInfo ci) {
		LOGGER.info("adding");
		if (owner != null) {
			LOGGER.info("adding2");
			tag.putString("Owner", owner);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void loadOwner(CompoundTag tag, CallbackInfo ci) {
		LOGGER.info("loadingshizzle");
		owner = tag.getString("Owner");
		LOGGER.info(tag.getString("Owner") + "KEYTOFIND");
		if (tag.hasUUID("Owner")) {
			LOGGER.info("loadingshizzle2");

		}
	}


}
