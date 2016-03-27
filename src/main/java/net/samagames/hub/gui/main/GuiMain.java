package net.samagames.hub.gui.main;

import net.samagames.api.SamaGamesAPI;
import net.samagames.hub.Hub;
import net.samagames.hub.games.AbstractGame;
import net.samagames.hub.gui.AbstractGui;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiMain extends AbstractGui
{
    public GuiMain(Hub hub)
    {
        super(hub);
    }

    @Override
    public void display(Player player)
    {
        this.inventory = this.hub.getServer().createInventory(null, 45, "Menu Principal");

        this.setSlotData(ChatColor.GOLD + "Zone " + ChatColor.GREEN + "VIP", Material.DIAMOND, 9, makeButtonLore(new String[] { "Testez les jeux avant tout le monde !" }, false, true), "beta_vip");
        this.setSlotData(ChatColor.GOLD + "Spawn", Material.BED, 18, makeButtonLore(new String[] { "Retourner au spawn grace à la magie", "de la téléportation céleste !" }, false, true), "spawn");
        this.setSlotData(ChatColor.GOLD + "Parcours du ciel", Material.PACKED_ICE, 26, makeButtonLore(new String[] { "En espérant que vous atteignez le", "paradis..."}, false, true), "parkours");
        this.setSlotData(ChatColor.GOLD + "Informations", Material.EMPTY_MAP, 27, getInformationLore(), "none");
        this.setSlotData(ChatColor.GOLD + "Changer de hub", Material.ENDER_CHEST, 35, makeButtonLore(new String[] { "Clique pour ouvrir l'interface" }, true, false), "switch_hub");

        for(String gameIdentifier : this.hub.getGameManager().getGames().keySet())
        {
            AbstractGame game = this.hub.getGameManager().getGameByIdentifier(gameIdentifier);

            if(game.getSlotInMainMenu() >= 0)
                this.setSlotData(game.isLocked() ? ChatColor.GOLD + "" + ChatColor.MAGIC + "aaaaaaaa" : (game.isNew() ? ChatColor.GREEN + "" + ChatColor.BOLD + "NOUVEAU ! " + ChatColor.RESET : "") + ChatColor.GOLD + game.getName(), game.isLocked() ? new ItemStack(Material.IRON_FENCE, 1) : game.getIcon(), game.getSlotInMainMenu(), makeGameLore(game), "game_" + gameIdentifier);
        }

        player.openInventory(this.inventory);
    }

    @Override
    public void onClick(Player player, ItemStack stack, String action, ClickType clickType)
    {
        if(action.equals("beta_vip") && SamaGamesAPI.get().getPermissionsManager().hasPermission(player, "hub.beta.vip"))
        {
            player.teleport(this.hub.getGameManager().getGameByIdentifier("beta_vip").getLobbySpawn());
        }
        else if(action.equals("switch_hub"))
        {
            this.hub.getGuiManager().openGui(player, new GuiSwitchHub(this.hub, 1));
        }
        else if(action.equals("spawn"))
        {
            player.teleport(this.hub.getPlayerManager().getSpawn());
        }
        else if(action.equals("parkour"))
        {
            player.teleport(this.hub.getParkourManager().getParkours().get(0).getFail());
        }
        else if(action.startsWith("game"))
        {
            String[] actions = action.split("_");
            AbstractGame game = this.hub.getGameManager().getGameByIdentifier(actions[1]);

            if(!game.isLocked())
                player.teleport(game.getLobbySpawn());
            else
                player.sendMessage(ChatColor.RED + "Ce jeu n'est pas disponible.");
        }
    }

    private static String[] makeButtonLore(String[] description, boolean clickOpen, boolean clickTeleport)
    {
        List<String> lore = new ArrayList<>();
        String[] loreArray = new String[] {};

        if(description != null)
        {
            for (String string : description)
                lore.add(ChatColor.GRAY + string);

            if (clickOpen || clickTeleport)
                lore.add("");
        }

        if(clickOpen)
            lore.add(ChatColor.DARK_GRAY + "\u25B6 Clique pour ouvrir le menu");

        if(clickTeleport)
            lore.add(ChatColor.DARK_GRAY + "\u25B6 Clique pour être téléporté");

        return lore.toArray(loreArray);
    }

    private static String[] makeGameLore(AbstractGame game)
    {
        List<String> lore = new ArrayList<>();
        String[] loreArray = new String[] {};

        if (!game.isLocked())
        {
            lore.add(ChatColor.DARK_GRAY + game.getCategory());
            lore.add("");

            if (game.getDescription() != null)
            {
                for (String line : game.getDescription())
                    lore.add(ChatColor.GRAY + line);

                lore.add("");
            }

            if (game.getDeveloppers() != null)
            {
                lore.add(ChatColor.GOLD + "Développeur" + (game.getDeveloppers().length > 1 ? "s" : "") + " : ");
                lore.add(ChatColor.GRAY + StringUtils.join(game.getDeveloppers(), ", "));
                lore.add("");
            }

            lore.add(ChatColor.DARK_GRAY + "\u25B6 Clique gauche pour être téléporté");
            lore.add(ChatColor.DARK_GRAY + "\u25B6 Clique droit pour lire les règles");
            lore.add("");
            lore.add(ChatColor.GRAY + "Il y a actuellement " + ChatColor.GOLD + game.getOnlinePlayers() + ChatColor.GRAY + " joueurs");
            lore.add(ChatColor.GRAY + "sur ce jeu.");
        }
        else
        {
            lore.add(ChatColor.RED + "Prochainement...");
        }

        return lore.toArray(loreArray);
    }

    private static String[] getInformationLore()
    {
        return new String[] {
                ChatColor.DARK_GRAY + "Site internet : " + ChatColor.GRAY + "https://www.samagames.net",
                ChatColor.DARK_GRAY + "Forum : " + ChatColor.GRAY + "https://www.samagames.net/forum/",
                ChatColor.DARK_GRAY + "Boutique : " + ChatColor.GRAY + "http://shop.samagames.net/",
                "",
                ChatColor.DARK_GRAY + "TeamSpeak : " + ChatColor.GRAY + "ts.samagames.net"
        };
    }
}