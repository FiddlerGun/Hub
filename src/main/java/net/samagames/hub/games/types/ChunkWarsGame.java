package net.samagames.hub.games.types;

import net.samagames.hub.Hub;
import net.samagames.hub.games.AbstractGame;
import net.samagames.hub.games.shops.ShopCategory;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ChunkWarsGame extends AbstractGame
{
    public ChunkWarsGame(Hub hub)
    {
        super(hub);
    }

    @Override
    public String getCodeName()
    {
        return "chunkwars";
    }

    @Override
    public String getName()
    {
        return "ChunkWars";
    }

    @Override
    public String getCategory()
    {
        return "PvP";
    }

    @Override
    public ItemStack getIcon()
    {
        return null;
    }

    @Override
    public String[] getDescription()
    {
        return new String[0];
    }

    @Override
    public String[] getDeveloppers()
    {
        return new String[0];
    }

    @Override
    public int getSlotInMainMenu()
    {
        return 32;
    }

    @Override
    public ShopCategory getShopConfiguration()
    {
        return null;
    }

    @Override
    public Location getLobbySpawn()
    {
        return null;
    }

    @Override
    public boolean isLocked()
    {
        return true;
    }

    @Override
    public boolean isNew()
    {
        return false;
    }
}
