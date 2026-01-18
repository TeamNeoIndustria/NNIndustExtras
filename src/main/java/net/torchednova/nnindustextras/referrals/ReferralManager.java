package net.torchednova.nnindustextras.referrals;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ReferralManager {
    public static ArrayList<Referral> rs;

    public static void init(MinecraftServer server)
    {
        rs = TargetDataStorage.load(server);

    }

    public static String getCode(Entity player)
    {
        UUID uuid = player.getUUID();

        for (int i = 0; i < rs.size(); i++) {
            if (Objects.equals(rs.get(i).uuid.toString(), uuid.toString()))
            {
                return rs.get(i).code;
            }
        }

        return newReferrer(player);

    }

    public static void banUser(String name)
    {
        for (int i = 0; i < rs.size(); i++)
        {
            if (Objects.equals(rs.get(i).name, name))
            {
                rs.get(i).banned = true;
            }
        }
    }


    public static String newReferrer(Entity player)
    {
        rs.add(new Referral(rs.size(), player.getUUID(), player.getScoreboardName(), -1));

        TargetDataStorage.save(player.getServer());

        return rs.getLast().code;
    }

    public static boolean addReferry(Entity player, String code)
    {

        Referral rf = getReferral(code);
        if(rf == null || rf.banned == true)
        {
            return false;
        }
        Referral ry = getReferral(player.getUUID());
        if (ry != null || ry.banned == true)
        {
            return false;
        }
        ry = newReffered(player, rf.id);
        if (ry == null || ry.banned == true)
        {
            return false;
        }

        rf.referred.add(ry.id);

        return true;


    }

    public static boolean delReferral(String code)
    {
        for (int i = 0; i < rs.size(); i++)
        {
            if (Objects.equals(rs.get(i).code, code))
            {
                rs.remove(i);
                return true;
            }
        }

        return false;
    }

    public static boolean delReferralName(String name)
    {
        for (int i = 0; i < rs.size(); i++)
        {
            if (Objects.equals(rs.get(i).name, name))
            {
                rs.remove(i);
                return true;
            }
        }

        return false;
    }


    public static Referral newReffered(Entity player, int referredBy)
    {
        rs.add(new Referral(rs.size(), player.getUUID(), player.getScoreboardName(), referredBy));


        return null;
    }
    public static Referral getReferral(String code)
    {
        for (int i = 0; i < rs.size(); i++)
        {
            if (Objects.equals(rs.get(i).code, code))
            {
                if (rs.get(i).banned == true) return null;
                return rs.get(i);
            }
        }

        return null;
    }

    public static Referral getReferral(UUID uuid)
    {
        for (int i = 0; i < rs.size(); i++)
        {
            if (Objects.equals(rs.get(i).uuid.toString(), uuid.toString()))
            {
                return rs.get(i);
            }
        }

        return null;
    }





}
