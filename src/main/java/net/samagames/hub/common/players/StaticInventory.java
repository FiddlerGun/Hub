package net.samagames.hub.common.players;

import net.samagames.hub.Hub;
import net.samagames.hub.gui.main.GuiMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;

public class StaticInventory
{
    private final Hub hub;
    private final HashMap<Integer, ItemStack> items;

    public StaticInventory(Hub hub)
    {
        this.hub = hub;

        this.items = new HashMap<>();
        this.items.put(0, buildItemStack(Material.COMPASS, 1, 0, createTitle("Menu principal"), null));
        this.items.put(1, buildItemStack(Material.GOLD_INGOT, 1, 0, createTitle("Boutique"), null));
        this.items.put(4, buildItemStack(Material.SKULL_ITEM, 1, 3, createTitle("Profil"), null));
        this.items.put(7, buildItemStack(Material.ENDER_CHEST, 1, 0, createTitle("Caverne aux trésors"), null));
        this.items.put(8, buildItemStack(Material.BOW, 1, 0, createTitle("Stalker 2000"), null));
    }

    public void doInteraction(Player player, ItemStack stack)
    {
        if(stack.getType() == Material.COMPASS)
            this.hub.getGuiManager().openGui(player, new GuiMain(this.hub));
        /**else if(stack.getType() == Material.GOLD_INGOT)
            this.guiManager.getGuiManager().openGui(player, new GuiShop());
        else if(stack.getType() == Material.SKULL_ITEM)
            this.guiManager.getGuiManager().openGui(player, new GuiProfile());
        else if(stack.getType() == Material.ENDER_CHEST)
            this.guiManager.getGuiManager().openGui(player, new GuiCosmetics());
        else if(stack.getType() == Material.BOW)
            this.guiManager.getGuiManager().openGui(player, new GuiStalker2000(1));**/
    }

    public void setInventoryToPlayer(Player player)
    {
        for(int slot : this.items.keySet())
        {
            if(this.items.get(slot).getType() == Material.SKULL_ITEM)
            {
                SkullMeta meta = (SkullMeta) this.items.get(slot).getItemMeta();
                meta.setOwner(player.getName());

                this.items.get(slot).setItemMeta(meta);
            }

            player.getInventory().setItem(slot, this.items.get(slot));
        }
    }

    private static String createTitle(String text)
    {
        return ChatColor.GOLD + "" + ChatColor.BOLD + text + ChatColor.RESET + "" + ChatColor.GRAY + " (Clic-droit)";
    }

    private static ItemStack buildItemStack(Material material, int quantity, int data, String name, String[] lores)
    {
        ItemStack stack = new ItemStack(material, quantity, (short) data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);

        if(lores != null)
            meta.setLore(Arrays.asList(lores));

        stack.setItemMeta(meta);

        return stack;
    }
}
