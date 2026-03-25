package net.torchednova.nnindustextras;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class BlockingItemHandler implements IItemHandler {
	private final IItemHandler inner;

	public BlockingItemHandler(IItemHandler inner) {
		this.inner = inner;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		// ❌ Block all external insertion
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inner.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlots() {
		return inner.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return inner.getStackInSlot(slot);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inner.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return false;
	}
}
