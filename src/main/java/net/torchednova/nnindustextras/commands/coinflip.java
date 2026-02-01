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
import net.torchednova.nnindustextras.referrals.ReferralManager;

import java.util.Random;

public class coinflip {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("coinflip").requires(source -> source.hasPermission(2))
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

                                 Item mons = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("numismatics","spur"));
                                 ItemStack is = new ItemStack(mons, amount);

                                 if (!(sp.getInventory().getItem(sp.getInventory().findSlotMatchingItem(is)).getCount() >= amount))
                                 {
                                     PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(
                                             sp.getUUID(),
                                             "Come back when you have more spurs to gamble!"
                                     );
                                     CommandSourceStack source = sp.createCommandSourceStack();
                                     source.sendChatMessage(new OutgoingChatMessage.Player(chatMessage),
                                             false,
                                             ChatType.bind(ChatType.CHAT, sp));
                                     return 1;
                                 }

                                 sp.getInventory().removeItem(sp.getInventory().findSlotMatchingItem(is), amount);
                                 Random rand = new Random();
                                 int result = rand.nextInt(100);
                                 if ((side == true && result > (100 - chance)) || (side == false && result < chance))
                                 {
                                     PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(
                                             sp.getUUID(),
                                             "You won!"
                                     );
                                     CommandSourceStack source = sp.createCommandSourceStack();
                                     source.sendChatMessage(new OutgoingChatMessage.Player(chatMessage),
                                             false,
                                             ChatType.bind(ChatType.CHAT, sp));
                                     is = new ItemStack(mons, amount * 2);
                                     sp.getInventory().add(is);
                                 }
                                 else
                                 {
                                     PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(
                                             sp.getUUID(),
                                             "You Lost better luck next time!"
                                     );
                                     CommandSourceStack source = sp.createCommandSourceStack();
                                     source.sendChatMessage(new OutgoingChatMessage.Player(chatMessage),
                                             false,
                                             ChatType.bind(ChatType.CHAT, sp));
                                 }

                                    return 1;
                                })
                        )
                                ))));
    }
}
