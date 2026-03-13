package net.torchednova.nnindustextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.torchednova.nnindustextras.freeze.FreezePlayer;

public class NeoFreeze {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("neofreeze").requires(source -> source.hasPermission(2))
				.then(Commands.argument("player", EntityArgument.player())
					.then(Commands.argument("do", BoolArgumentType.bool())
						.executes(context ->
						{
							boolean freezethaw = BoolArgumentType.getBool(context, "do");
							Player p = EntityArgument.getPlayer(context, "player");
							//true freeze / false thaw
							if (freezethaw)
							{
								FreezePlayer.frozen.add(new FreezePlayer(p.getUUID(), p.getPosition(0), (ServerLevel) p.level()));

								context.getSource().sendSuccess(() -> Component.literal(p.getName().getString() + " has been frozen"), false);
								p.createCommandSourceStack().sendSuccess(() -> Component.literal("You have been frozen"), false);
							}
							else
							{
								FreezePlayer.remove(p.getUUID());
								context.getSource().sendSuccess(() -> Component.literal(p.getName().getString() + " has been thawed"), false);
								p.createCommandSourceStack().sendSuccess(() -> Component.literal("You have been thawed and can now move"), false);
							}

							return 1;
						})
					)
				)

		);
	}
}
