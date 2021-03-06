package net.samagames.hub;

import de.slikey.effectlib.EffectLib;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.api.games.Status;
import net.samagames.hub.commands.CommandManager;
import net.samagames.hub.common.refresh.HubRefresher;
import net.samagames.hub.common.hydroangeas.HydroangeasManager;
import net.samagames.hub.common.managers.EntityManager;
import net.samagames.hub.common.managers.EventBus;
import net.samagames.hub.common.players.ChatManager;
import net.samagames.hub.common.players.PlayerManager;
import net.samagames.hub.common.receivers.*;
import net.samagames.hub.common.tasks.TaskManager;
import net.samagames.hub.cosmetics.CosmeticManager;
import net.samagames.hub.events.*;
import net.samagames.hub.events.protection.EntityEditionListener;
import net.samagames.hub.events.protection.InventoryEditionListener;
import net.samagames.hub.events.protection.PlayerProtectionListener;
import net.samagames.hub.events.protection.WorldEditionListener;
import net.samagames.hub.games.GameManager;
import net.samagames.hub.games.leaderboards.HubLeaderboard;
import net.samagames.hub.games.leaderboards.RotatingLeaderboard;
import net.samagames.hub.games.signs.SignManager;
import net.samagames.hub.gui.GuiManager;
import net.samagames.hub.gui.achievements.GuiAchievements;
import net.samagames.hub.hostgame.HostGameManager;
import net.samagames.hub.interactions.InteractionManager;
import net.samagames.hub.npcs.NPCManager;
import net.samagames.hub.parkours.ParkourManager;
import net.samagames.hub.scoreboards.ScoreboardManager;
import net.samagames.hub.utils.ServerStatus;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
 * This file is part of Hub.
 *
 * Hub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hub.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Hub extends JavaPlugin
{
    private static Hub instance;

    private World world;

    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    private HubRefresher hubRefresher;
    private BukkitTask hubRefresherTask;

    private EventBus eventBus;
    private HydroangeasManager hydroangeasManager;
    private TaskManager taskManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private ChatManager chatManager;
    private GameManager gameManager;
    private SignManager signManager;
    private ScoreboardManager scoreboardManager;
    private GuiManager guiManager;
    private ParkourManager parkourManager;
    private NPCManager npcManager;
    private CommandManager commandManager;
    private CosmeticManager cosmeticManager;
    private InteractionManager interactionManager;
    private HostGameManager hostGameManager;

    private EventListener eventListener;
    private ScheduledFuture hydroangeasSynchronization;

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveDefaultConfig();

        this.world = this.getServer().getWorlds().get(0);
        this.world.setGameRuleValue("randomTickSpeed", "0");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(this.getConfig().getLong("time", 6000L));

        this.scheduledExecutorService = Executors.newScheduledThreadPool(16);
        this.executorMonoThread = Executors.newScheduledThreadPool(1);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (!this.getConfig().getBoolean("disconnected", false))
        {
            this.hubRefresher = new HubRefresher(this);
            this.hubRefresherTask = this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.hubRefresher, 20L * 3, 20L * 3);
            SamaGamesAPI.get().getPubSub().subscribe("hub-status", this.hubRefresher);
        }

        this.eventBus = new EventBus();
        this.hydroangeasManager = new HydroangeasManager(this);
        this.taskManager = new TaskManager(this);
        this.entityManager = new EntityManager(this);
        this.playerManager = new PlayerManager(this);
        this.chatManager = new ChatManager(this);
        this.gameManager = new GameManager(this);
        this.signManager = new SignManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.guiManager = new GuiManager(this);
        this.parkourManager = new ParkourManager(this);
        this.npcManager = new NPCManager(this);
        this.commandManager = new CommandManager(this);

        this.cosmeticManager = new CosmeticManager(this);
        this.cosmeticManager.init();

        this.interactionManager = new InteractionManager(this);
        this.hostGameManager = new HostGameManager(this);

        GuiAchievements.createCache();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ParkourListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DevelopperListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PetListener(), this);

        this.getServer().getPluginManager().registerEvents(new EntityEditionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryEditionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WorldEditionListener(this), this);

        this.hydroangeasSynchronization = this.getScheduledExecutorService().scheduleAtFixedRate(() -> new ServerStatus(SamaGamesAPI.get().getServerName(), "Hub", "Map", Status.IN_GAME, this.getServer().getOnlinePlayers().size(), this.getServer().getMaxPlayers()).sendToHydro(), 0, 1, TimeUnit.MINUTES);

        SamaGamesAPI.get().getPubSub().subscribe("cheat", new SamaritanListener(this));
        //SamaGamesAPI.get().getPubSub().subscribe("", new InteractionListener(this));
        SamaGamesAPI.get().getPubSub().subscribe("maintenanceSignChannel", new MaintenanceListener(this));
        SamaGamesAPI.get().getPubSub().subscribe("soonSignChannel", new SoonListener(this));

        this.eventListener = new EventListener(this);
        SamaGamesAPI.get().getPubSub().subscribe("eventChannel", this.eventListener);
        SamaGamesAPI.get().getPubSub().subscribe("servers", this.eventListener);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () ->
                {
                    this.getGameManager().getGames().values().stream()
                            .filter(abstractGame -> abstractGame.getLeaderBoards() != null)
                            .forEach(abstractGame -> abstractGame.getLeaderBoards().forEach(HubLeaderboard::refresh));

                    RotatingLeaderboard.increment();
                }
                , 0L, 1200L
        );

        SamaGamesAPI.get().getStatsManager().setStatsToLoad(GamesNames.GLOBAL, true);
    }

    @Override
    public void onDisable()
    {
        if (this.hubRefresher != null)
            this.hubRefresherTask.cancel();

        this.hydroangeasSynchronization.cancel(true);
        this.eventBus.onDisable();
    }

    public World getWorld()
    {
        return this.world;
    }

    public ScheduledExecutorService getExecutorMonoThread()
    {
        return this.executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService()
    {
        return this.scheduledExecutorService;
    }

    public HubRefresher getHubRefresher()
    {
        return this.hubRefresher;
    }

    public EventBus getEventBus()
    {
        return this.eventBus;
    }

    public HydroangeasManager getHydroangeasManager() { return this.hydroangeasManager; }
    public TaskManager getTaskManager() { return this.taskManager; }
    public EntityManager getEntityManager() { return this.entityManager; }
    public PlayerManager getPlayerManager() { return this.playerManager; }
    public ChatManager getChatManager() { return this.chatManager; }
    public GameManager getGameManager() { return this.gameManager; }
    public SignManager getSignManager() { return this.signManager; }
    public ScoreboardManager getScoreboardManager() { return this.scoreboardManager; }
    public GuiManager getGuiManager() { return this.guiManager; }
    public ParkourManager getParkourManager() { return this.parkourManager; }
    public NPCManager getNPCManager() { return this.npcManager; }
    public CommandManager getCommandManager() { return this.commandManager; }
    public CosmeticManager getCosmeticManager() { return this.cosmeticManager; }
    public InteractionManager getInteractionManager() { return this.interactionManager; }
    public HostGameManager getHostGameManager() { return this.hostGameManager; }

    public EventListener getEventListener()
    {
        return this.eventListener;
    }

    public EffectLib getEffectLib()
    {
        Plugin effectLib = this.getServer().getPluginManager().getPlugin("EffectLib");

        if (effectLib == null || !(effectLib instanceof EffectLib))
            return null;

        return (EffectLib) effectLib;
    }

    public static Hub getInstance() {
        return instance;
    }
}
