package net.torchednova.nnindustextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.torchednova.nnindustextras.referrals.GivesManager;
import net.torchednova.nnindustextras.referrals.Referral;
import net.torchednova.nnindustextras.referrals.ReferralManager;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;

import static net.torchednova.nnindustextras.NNIndustExtras.LOGGER;

public class adminrefer {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("adminrefer").requires(source -> source.hasPermission(2))
                        // /referral get
                        .then(
                                Commands.literal("getall")
                                        .executes(context -> {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("All Referral Codes\n");
                                            for (int i = 0; i <  ReferralManager.rs.size(); i++)
                                            {
                                                sb.append("Name: ");
                                                sb.append(ReferralManager.rs.get(i).name);
                                                sb.append("\n");
                                                sb.append("Code: ");
                                                sb.append(ReferralManager.rs.get(i).code);
                                                sb.append("\n");
                                                sb.append("Referred: ");
                                                sb.append(ReferralManager.rs.get(i).referred.size());
                                                sb.append("\n");
                                                sb.append("Banned: ");
                                                sb.append(ReferralManager.rs.get(i).banned);
                                            }

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(sb.toString()),
                                                    false
                                            );
                                            return 1;
                                        })
                        )
                        // /referral enter <code>
                        .then(
                                Commands.literal("del")
                                        .then(
                                                Commands.argument("code", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            int code = IntegerArgumentType.getInteger(context, "code");

                                                            Boolean worked = ReferralManager.delReferral(String.valueOf(code));
                                                            if (worked)
                                                            {
                                                                TargetDataStorage.save(context.getSource().getServer());
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal("That code has been deleted"),
                                                                        false
                                                                );
                                                            }
                                                            else
                                                            {
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal("Failed to delete code"),
                                                                        false
                                                                );
                                                            }

                                                            return 1;
                                                        })
                                        ).then(
                                                Commands.argument("name", StringArgumentType.greedyString())
                                                        .executes(context -> {
                                                            String code = StringArgumentType.getString(context, "name");

                                                            Boolean worked = ReferralManager.delReferralName(code);
                                                            if (worked)
                                                            {
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal("That code has been deleted"),
                                                                        false
                                                                );
                                                            }
                                                            else
                                                            {
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal("Failed to delete code"),
                                                                        false
                                                                );
                                                            }

                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                Commands.literal("ban")
                                        .then(
                                                Commands.argument("name", StringArgumentType.greedyString())
                                                        .executes(context -> {
                                                            String name = StringArgumentType.getString(context, "name");
                                                            MinecraftServer server = context.getSource().getServer();
                                                            ServerPlayer sPlayer = server.getPlayerList().getPlayerByName(name);
                                                            Referral worked = ReferralManager.getReferral(sPlayer.getUUID());
                                                            if (worked != null)
                                                            {
                                                                ReferralManager.banUser(name);
                                                                TargetDataStorage.save(context.getSource().getServer());
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal(name + " has been banned for using and having people use there code"),
                                                                        false
                                                                );
                                                            }
                                                            else
                                                            {
                                                                context.getSource().sendSuccess(
                                                                        () -> Component.literal("Failed to ban " + name),
                                                                        false
                                                                );
                                                            }

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}