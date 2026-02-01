package net.torchednova.nnindustextras.mixin;

import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



import java.lang.reflect.InvocationTargetException;

@Mixin(appeng.api.storage.StorageHelper.class)
public class ae2BlockInserts {

    @Inject(
            method = "poweredInsert(Lappeng/api/networking/energy/IEnergySource;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/AEKey;JLappeng/api/networking/security/IActionSource;)J",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void nnindustextras$addItems(
            IEnergySource energy,
            MEStorage inv,
            AEKey input,
            long amount,
            IActionSource src,
            CallbackInfoReturnable<Long> cir
    ){
        String id = input.getId().toString();


        if (id.equals("numismatics:spur") || id.equals("numismatics:bevel") || id.equals("numismatics:sprocket")  || id.equals("numismatics:cog") || id.equals("numismatics:crown") || id.equals("numismatics:sun"))
        {
            cir.setReturnValue(0L);
            //cir.cancel();
        }

    }


}
