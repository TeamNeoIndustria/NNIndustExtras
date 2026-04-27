package net.torchednova.nnindustextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.torchednova.nnindustextras.NNIndustExtras;
import net.torchednova.nnindustextras.referrals.ReferralManager;

import java.util.Random;

public class coinflip {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("neocoinflip").requires(source -> source.hasPermission(2))
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .then(Commands.argument("side", BoolArgumentType.bool())
                                .then(Commands.argument("chance", IntegerArgumentType.integer())
                                .then(Commands.argument("player", StringArgumentType.greedyString())
                                .executes(context -> {
                                 String playername = StringArgumentType.getString(context, "player");

                                 int amount = IntegerArgumentType.getInteger(context, "value");
                                 boolean side = BoolArgumentType.getBool(context, "side");
                                 int chance = IntegerArgumentType.getInteger(context, "chance");

                                 if (playername == null) return 1;

                                 ServerPlayer sp = context.getSource().getServer().getPlayerList().getPlayerByName(playername);

                                 Item mons = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("neobanking","wooden_coin"));
                                 ItemStack is = new ItemStack(mons, amount);

                                    //NNIndustExtras.LOGGER.info(String.valueOf(sp.getInventory().findSlotMatchingItem(is)) + "------------------------------------");

                                    CommandSourceStack source = sp.createCommandSourceStack();

                                 if (sp == null || sp.getInventory().findSlotMatchingItem(is) == -1)
                                 {
                                     source.sendSuccess(() ->
                                     Component.literal("Comeback when you have wooden coins"),
                                         false
                                     );
                                     return 1;
                                 }

                                 if (!(sp.getInventory().getItem(sp.getInventory().findSlotMatchingItem(is)).getCount() >= amount))
                                 {
                                     source.sendSuccess(() ->
                                             Component.literal("Comeback when you have more coins to gamble"),
                                         false
                                     );
                                     return 1;
                                 }

                                 sp.getInventory().removeItem(sp.getInventory().findSlotMatchingItem(is), amount);
                                 Random rand = new Random();
                                 int result = rand.nextInt(100);
                                 if ((side == true && result > (100 - chance)) || (side == false && result < chance))
                                 {
                                     source.sendSuccess(() ->
                                             Component.literal("You Won!"),
                                         false
                                     );
                                     is = new ItemStack(mons, amount * 2);
                                     sp.getInventory().add(is);
                                 }
                                 else
                                 {
                                     source.sendSuccess(() ->
                                             Component.literal("You Lost, Better luck next time"),
                                         false
                                     );
                                 }

                                    return 1;
                                })
                        )
                                ))));
    }
}
