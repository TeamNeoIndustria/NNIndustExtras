package net.torchednova.nnindustextras;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ownertracker {

	void nnindust$setOwner(String owner);
	String nnindust$getOwner();

	void nnindust$setLevel(ResourceKey<Level> level);
	Level nnindust$getLevel();

	void nnindust$setPos(BlockPos pos);
	BlockPos nnindust$getPos();
}
