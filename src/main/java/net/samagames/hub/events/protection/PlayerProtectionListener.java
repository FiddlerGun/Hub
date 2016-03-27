package net.samagames.hub.events.protection;

import net.samagames.api.SamaGamesAPI;
import net.samagames.hub.Hub;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class PlayerProtectionListener implements Listener
{
    private final Hub hub;

    public PlayerProtectionListener(Hub hub)
    {
        this.hub = hub;
    }

    @EventHandler
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
    {
        if (!this.canDoAction(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(final PlayerInteractEvent event)
    {
        if (!this.canDoAction(event.getPlayer()))
            event.setCancelled(true);

        this.hub.getServer().getScheduler().runTaskAsynchronously(this.hub, () ->
        {
            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(event.getPlayer(), "hub.sign.selection"))
            {
                if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE)
                {
                    Action act = event.getAction();

                    if (act == Action.LEFT_CLICK_BLOCK)
                    {
                        this.hub.getPlayerManager().setSelection(event.getPlayer(), event.getClickedBlock().getLocation());
                        event.getPlayer().sendMessage(ChatColor.GOLD + "Point sélectionné !");
                    }
                }
            }
        });
    }

    private boolean canDoAction(Player player)
    {
        return this.hub.getPlayerManager().canBuild() && player.isOp();
    }
}
