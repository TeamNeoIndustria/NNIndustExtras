package net.torchednova.nnindustextras.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.torchednova.nnindustextras.irs.IRS;

public class neouuid {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("neouuid").requires(source -> source.hasPermission(2))
				.then(Commands.argument("entity", EntityArgument.entity())
					.executes(context ->
					{
						Entity ent = EntityArgument.getEntity(context, "entity");
						context.getSource().sendSuccess(
							() -> Component.literal("").append(Component.literal(ent.getStringUUID()).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ent.getStringUUID())).withUnderlined(true).withColor(ChatFormatting.GREEN))).append(""),
							false
						);
						return 1;
					})
				)

		);
	}
}
