package net.torchednova.nnindustextras;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import dev.ftb.mods.ftbchunks.FTBChunks;
import dev.ftb.mods.ftbchunks.api.ChunkTeamData;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.torchednova.nnindustextras.Players.PlayerInfoController;
import net.torchednova.nnindustextras.commands.*;
import net.torchednova.nnindustextras.freeze.FreezePlayer;
import net.torchednova.nnindustextras.referrals.GivesManager;
import net.torchednova.nnindustextras.referrals.ReferralManager;
import net.torchednova.nnindustextras.savedata.TargetDataStorage;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Optional;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NNIndustExtras.MODID)
public class NNIndustExtras {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nnindustextras";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private static MinecraftServer server;

    public static MinecraftServer getServer()
    {
        return server;
    }

    private static TagKey<Block> crafter =
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("create", "mechanical_crafter"));
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public NNIndustExtras(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.addListener(AEStageCheck::onPatternWrite);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ItemsStageController.init();
    }


    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
        GivesManager.init(event.getServer());
        ReferralManager.init(event.getServer());
        FreezePlayer.init();
        PlayerInfoController.init(event.getServer());
        this.server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event)
    {
        TargetDataStorage.save(event.getServer());
        TargetDataStorage.saveGives(event.getServer());
        TargetDataStorage.PlayerSave(event.getServer());
    }

    @SubscribeEvent
    public void onServerPostTick(ServerTickEvent.Post event)
    {
        if (event.getServer().getPlayerList().getPlayerCount() == 0) return;
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        ServerPlayer sp;
        for (int i = 0; i < FreezePlayer.frozen.size(); i++)
        {
            sp = event.getServer().getPlayerList().getPlayer(FreezePlayer.frozen.get(i).player);

            if (event.getServer().getPlayerList().getPlayers().contains(sp))
            {
                sp.teleportTo(FreezePlayer.frozen.get(i).sl, FreezePlayer.frozen.get(i).pos.x, FreezePlayer.frozen.get(i).pos.y, FreezePlayer.frozen.get(i).pos.z, sp.yHeadRot, sp.getXRot());
            }
        }
    }


    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getPlayer() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getEntity() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        if(!event.getPlacedBlock().getBlock().builtInRegistryHolder().getKey().location().toString().equals("create:mechanical_crafter")) {
            return;
        }
        if (((ServerPlayer) event.getEntity()).gameMode.isCreative()) return;

        Level level = (Level)event.getLevel();
        if (level.isClientSide()) return;

        BlockPos pos = event.getPos();
        ChunkPos chunkpos = new ChunkPos(pos);

        Team playerTeam = FTBTeamsAPI.api().getManager().getTeamForPlayer(player).get();

        ChunkDimPos chunkDimPos = new ChunkDimPos(level.dimension(), chunkpos);
        if (chunkDimPos == null) return;

        ClaimedChunk chunkTeam = FTBChunksAPI.api().getManager().getChunk(chunkDimPos);

        if (chunkTeam == null) {
            event.setCanceled(true);
            player.displayClientMessage(
                Component.literal("You can only place this block in your team's claimed chunks!"),
                true
            );
            return;
        }

        Team team = chunkTeam.getTeamData().getTeam();

        if (!playerTeam.equals(team))
        {
            event.setCanceled(true);
            player.displayClientMessage(
                Component.literal("You can only place this block in your team's claimed chunks!"),
                true
            );
            return;
        }

        BlockEntity be = level.getBlockEntity(event.getPos());
        CompoundTag ct = new CompoundTag();
        ct.putString("Owner", event.getEntity().getStringUUID());
        if (be instanceof ownertracker mcbe)
        {
            mcbe.nnindust$setOwner(event.getEntity().getStringUUID());
            mcbe.nnindust$setPos(event.getPos());
            mcbe.nnindust$setLevel(event.getEntity().level().dimension());
        }
        be.setChanged();

    }

        @SubscribeEvent
    public void onMultiPlace(BlockEvent.EntityMultiPlaceEvent event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getEntity() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlockPlace(PlayerInteractEvent.RightClickBlock  event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getEntity() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItemPlace(PlayerInteractEvent.RightClickItem  event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getEntity() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamaged(LivingIncomingDamageEvent event)
    {
        if (FreezePlayer.frozen == null || FreezePlayer.frozen.isEmpty()) return;

        if (event.getSource().getEntity() instanceof Player player)
        {
            int pos = FreezePlayer.getPlayerIn(player.getUUID());
            if (pos != -1)
            {
                event.setCanceled(true);
            }
        }
    }



    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        refer.register(event.getDispatcher());
        adminrefer.register(event.getDispatcher());
        coinflip.register(event.getDispatcher());
        NeoFreeze.register(event.getDispatcher());
        neouuid.register(event.getDispatcher());
        BuyStore.register(event.getDispatcher());
        StoreManagement.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        GivesManager.onPlayerJoin(event.getEntity());
        PlayerInfoController.checkIfChangedName(event.getEntity());
        PlayerInfoController.checkIfStoreTimedOut(event.getEntity());

    }
}
