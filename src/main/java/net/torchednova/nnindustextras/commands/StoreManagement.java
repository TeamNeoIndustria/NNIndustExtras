package net.torchednova.nnindustextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.torchednova.nnindustextras.Players.PlayerInfo;
import net.torchednova.nnindustextras.Players.PlayerInfoController;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;

public class StoreManagement {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("store").requires(source -> source.hasPermission(2))
				.then(Commands.literal("remove")
					.then(Commands.argument("player", EntityArgument.player())
						.executes(context ->
							{
								Player p = EntityArgument.getPlayer(context, "player");
								if (p == null) {
									context.getSource().sendSuccess(() -> Component.literal("That player does not exist"), false);
									return 1;
								}

								PlayerInfo pi = PlayerInfoController.get(p);
								if (pi == null) {
									context.getSource().sendSuccess(() -> Component.literal("Could find data on player"), false);
									return 1;
								}

								Player runner = context.getSource().getPlayer();
								if (runner == null) {
									context.getSource().sendSuccess(() -> Component.literal("Command ran from server needs to be ran by player"), false);
									return 1;
								}

								PlayerInfo rPI = PlayerInfoController.get(runner);
								if (rPI == null) {
									context.getSource().sendSuccess(() -> Component.literal("No data on runner of command"), false);
									return 1;
								}
								if (rPI.ownStore == -1) {
									context.getSource().sendSuccess(() -> Component.literal("You do not own a store"), false);
									return 1;
								}

								if (pi.otherStores.contains(rPI.ownStore))
								{
									Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
									PlayerTeam team = scoreboard.getPlayerTeam("ShopKeepers" + String.valueOf(rPI.ownStore));
									if (team == null) return 1;

									scoreboard.removePlayerFromTeam(p.getScoreboardName(), team);
									for (int i = 0; i < pi.otherStores.size(); i++)
									{
										if (pi.otherStores.get(i) == rPI.ownStore)
										{
											pi.otherStores.remove(i);
											context.getSource().sendSuccess(() -> Component.literal("Removed " + p.getScoreboardName() + " to your store"), false);
											TargetDataStorage.PlayerSave(context.getSource().getServer());
											return 1;
										}
									}
								}
								context.getSource().sendSuccess(() -> Component.literal("Failed to remove " + p.getScoreboardName() + " to your store"), false);
								return 1;
							}
						)
					)
				)
				.then(Commands.literal("add")
					.then(Commands.argument("player", EntityArgument.player())
						.executes(context ->
							{
								Player p = EntityArgument.getPlayer(context, "player");
								if (p == null) {
									context.getSource().sendSuccess(() -> Component.literal("That player does not exist"), false);
									return 1;
								}

								PlayerInfo pi = PlayerInfoController.get(p);
								if (pi == null) {
									context.getSource().sendSuccess(() -> Component.literal("Could find data on player"), false);
									return 1;
								}

								Player runner = context.getSource().getPlayer();
								if (runner == null) {
									context.getSource().sendSuccess(() -> Component.literal("Command ran from server needs to be ran by player"), false);
									return 1;
								}

								PlayerInfo rPI = PlayerInfoController.get(runner);
								if (rPI == null) {
									context.getSource().sendSuccess(() -> Component.literal("No data on runner of command"), false);
									return 1;
								}
								if (rPI.ownStore == -1) {
									context.getSource().sendSuccess(() -> Component.literal("You do not own a store"), false);
									return 1;
								}

								if (!pi.otherStores.contains(rPI.ownStore))
								{
									Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
									PlayerTeam team = scoreboard.getPlayerTeam("ShopKeepers" + String.valueOf(rPI.ownStore));
									if (team == null) return 1;

									scoreboard.addPlayerToTeam(p.getScoreboardName(), team);
									pi.otherStores.add(rPI.ownStore);

									context.getSource().sendSuccess(() -> Component.literal("Added " + p.getScoreboardName() + " to your store"), false);
									TargetDataStorage.PlayerSave(context.getSource().getServer());
									return 1;
								}
								context.getSource().sendSuccess(() -> Component.literal("Failed to add " + p.getScoreboardName() + " to your store"), false);
								return 1;
							}
						)
					)
				)
		);
	}
}
