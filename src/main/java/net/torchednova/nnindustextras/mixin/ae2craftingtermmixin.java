package net.torchednova.nnindustextras.mixin;

import appeng.api.inventories.ISegmentedInventory;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.menu.slot.CraftingTermSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.torchednova.nnindustextras.mixin.AEBaseAccessor;
import net.torchednova.nnindustextras.ItemsStageController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import appeng.menu.me.items.CraftingTermMenu;

import java.rmi.registry.Registry;

import static net.torchednova.nnindustextras.NNIndustExtras.LOGGER;

@Mixin(appeng.menu.me.items.CraftingTermMenu.class)
public abstract class ae2craftingtermmixin {


    @Shadow
    @Final
    private CraftingTermSlot outputSlot;


    @Shadow
    public abstract RecipeHolder<CraftingRecipe> getCurrentRecipe();

    @Inject(
            method = "updateCurrentRecipeAndOutput",
            at = @At("TAIL"),
            cancellable = true
    )
    private void nnindustextras$updateCurrentRecipeAndOutput(
            boolean forceUpdate,
            CallbackInfo ci
    ) {
        CraftingTermSlot out = this.outputSlot;
        Player p = ((AEBaseAccessor) this).ae2$getPlayerInventory().player;

        String id = out.getItem().getItem().builtInRegistryHolder().key().location().toString();
        if (ItemsStageController.unlocked(id, p))
        {
            this.outputSlot.set(ItemStack.EMPTY);
        }
    }
}
