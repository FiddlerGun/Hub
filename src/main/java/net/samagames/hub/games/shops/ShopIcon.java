package net.samagames.hub.games.shops;

import net.samagames.hub.Hub;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ShopIcon
{
    protected final Hub hub;
    private final String actionName;
    private final ItemStack icon;
    private final int slot;

    public ShopIcon(Hub hub, String actionName, String displayName, ItemStack icon, int slot)
    {
        this.hub = hub;
        this.actionName = actionName;
        this.icon = icon;
        this.slot = slot;

        if(icon != null)
        {
            ItemMeta meta = this.icon.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + displayName);
            this.icon.setItemMeta(meta);
        }
    }

    public abstract void execute(Player player, ClickType clickType);
    public abstract ItemStack getFormattedIcon(Player player);

    public String getActionName()
    {
        return this.actionName;
    }

    public ItemStack getIcon()
    {
        return this.icon;
    }

    public int getSlot()
    {
        return this.slot;
    }
}
