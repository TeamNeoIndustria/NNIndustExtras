package net.torchednova.nnindustextras.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(appeng.menu.AEBaseMenu.class)
public interface AEBaseAccessor {

    @Accessor("playerInventory")
    Inventory ae2$getPlayerInventory();
}
