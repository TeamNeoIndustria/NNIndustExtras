package net.torchednova.nnindustextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.torchednova.nnindustextras.NNIndustExtras;
import net.torchednova.nnindustextras.Players.PlayerInfo;
import net.torchednova.nnindustextras.Players.PlayerInfoController;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;
import xyz.neonetwork.neobanking.api.IRS;
import xyz.neonetwork.neobanking.paymentprocessor.CurrencyHandler;
import xyz.neonetwork.neolib.utilities.NeoNotify;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class BuyStore {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("neostore").requires(source -> source.hasPermission(2))
				.then(Commands.argument("entity", EntityArgument.player())
					.then(Commands.argument("storenum", IntegerArgumentType.integer())
						.then(Commands.argument("cost", IntegerArgumentType.integer())
					.executes(context ->
					{
						int cost = IntegerArgumentType.getInteger(context, "cost");
						Player p = EntityArgument.getPlayer(context, "entity");


						int storenum = IntegerArgumentType.getInteger(context, "storenum");

						if (PlayerInfoController.storealreadyowned(storenum))
						{
							p.createCommandSourceStack().sendSuccess(
								() -> Component.literal("This store is already owned, this probably shouldn't show so please report this error to an admin"),
								false
							);
							return 1;
						}

						if (PlayerInfoController.ownShop(p))
						{
							p.createCommandSourceStack().sendSuccess(
								() -> Component.literal("Sorry, you may only own one Shop"),
								false
							);
							return 1;
						}


						Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
						PlayerTeam team = scoreboard.getPlayerTeam("ShopKeepers" + String.valueOf(storenum));
						if (team == null)
						{
							p.createCommandSourceStack().sendSuccess(
								() -> Component.literal("Failed to find team for store number " + String.valueOf(storenum) + " please screen shot this error and report to an admin"),
								false
							);

							NNIndustExtras.LOGGER.error("Failed to find team for store number " + String.valueOf(storenum));
						}
						else
						{
							if (!hasMoney(p, cost))
							{
								p.createCommandSourceStack().sendSuccess(() -> Component.literal("You don't have the " + cost + " to buy this store"), false);
								return 1;
							}

							scoreboard.addPlayerToTeam(p.getScoreboardName(), team);
							p.createCommandSourceStack().sendSuccess(
								() -> Component.literal("Congratulations on buying a store! To keep your store you need to log in once every 30 days. After 30 days of inactivity ownership resets"),
								false
							);

							NeoNotify.sendTitle((ServerPlayer) p, Component.literal("You have Bought Store " + storenum), null);
							((ServerPlayer)p).serverLevel().playSound(
							null,
							p.getX(),
							p.getY(),
							p.getZ(),
							SoundEvents.GOAT_HORN_PLAY,
							SoundSource.MASTER,
							10.0F,
							1.0F
							);
							((ServerLevel)context.getSource().getLevel()).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, context.getSource().getEntity().getX(), context.getSource().getEntity().getY() + 1, context.getSource().getEntity().getZ(), 20, 0, 0.5, 0.0, 0.05);

							((ServerPlayer)p).serverLevel().playSound(
								null,
								p.getX(),
								p.getY(),
								p.getZ(),
								SoundEvents.ARMOR_EQUIP_LEATHER,
								SoundSource.MASTER,
								8.0F,
								1.4F
							);


							PlayerInfoController.get(p);
							PlayerInfoController.setStore(p, storenum);
							TargetDataStorage.PlayerSave(context.getSource().getServer());

							context.getSource().getEntity().remove(Entity.RemovalReason.DISCARDED);
						}

						return 1;
					})))
				)
				.then(Commands.literal("remove")
					.then(Commands.argument("storenum", IntegerArgumentType.integer())
					.executes(context ->
					{
						int storenum = IntegerArgumentType.getInteger(context, "storenum");
						String storename = "ShopKeepers" + String.valueOf(storenum);

						Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
						PlayerTeam team = scoreboard.getPlayerTeam("ShopKeepers" + String.valueOf(storenum));
						if (team == null) {
							context.getSource().sendSuccess(
								() -> Component.literal("Sorry this shop doesn't not exist"),
								false
							);
							return 1;
						}


						for (String names : team.getPlayers())
						{
							for (int i = 0; i < PlayerInfoController.Players.size(); i++)
							{
								if (Objects.equals(PlayerInfoController.Players.get(i).name, names))
								{
									scoreboard.removePlayerFromTeam(names, team);
									PlayerInfoController.Players.get(i).ownStore = -1;
									TargetDataStorage.PlayerSave(context.getSource().getServer());

									return 1;
								}
							}
						}

						return 1;

					}))
				)
		);
	}

	private static boolean hasMoney(Player p, int cost)
	{
		if (CurrencyHandler.calculateSimpleInventoryValue(p) >= cost)
		{
			CurrencyHandler.removeValueFromInventory(p, cost);
			return true;
		}
		else if (IRS.getUserBalance(p.getStringUUID()) >= cost)
		{
			IRS.serverReceiveMoney(p.getStringUUID(), cost, "Bought Shop");
			return true;
		}

		return false;
	}


}
