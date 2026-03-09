package net.torchednova.nnindustextras.freeze;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;
import static net.torchednova.nnindustextras.NNIndustExtras.LOGGER;

import java.util.ArrayList;
import java.util.UUID;

public class FreezePlayer {
    public static ArrayList<FreezePlayer> frozen;

    public static void remove(UUID player)
    {
        for (int i = 0; i < frozen.size(); i++)
        {
            if (frozen.get(i).player.equals(player))
            {
                frozen.remove(i);
                LOGGER.info(String.valueOf(frozen.size()));
                return;
            }
        }
    }

    public static int getPlayerIn(UUID player)
    {
        for (int i = 0; i < frozen.size(); i++)
        {
            if (frozen.get(i).player.equals(player))
            {
                return i;
            }
        }
        return -1;
    }

    public static void init()
    {
        frozen = new ArrayList<>();
    }





    public FreezePlayer(UUID player, Vec3 pos, ServerLevel sl)
    {
        this.player = player;
        this.pos = pos;
        this.sl = sl;
    }

    public UUID player;
    public Vec3 pos;
    public ServerLevel sl;

}
