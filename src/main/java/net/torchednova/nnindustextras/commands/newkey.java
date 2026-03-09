package net.torchednova.nnindustextras.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.torchednova.nnindustextras.irs.IRS;

public class newkey {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("neonewkey").requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context ->
                                {
                                    Player p = EntityArgument.getPlayer(context, "player");
                                    if (p instanceof ServerPlayer) {

                                        String json = IRS.newKey(p);
                                        JsonObject repJSON = JsonParser.parseString(json).getAsJsonObject();
                                        String key = repJSON.get("data").getAsJsonObject().get("apikey").getAsString();

                                        p.createCommandSourceStack().sendSuccess(
                                                () -> Component.literal("Your key is: ").append(Component.literal(key).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, key)).withUnderlined(true).withColor(ChatFormatting.GREEN))).append(". Click to copy."),
                                                false
                                        );
                                    }

                                    return 1;
                                })
                        )

        );
    }
}
