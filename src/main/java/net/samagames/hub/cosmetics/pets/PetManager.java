package net.samagames.hub.cosmetics.pets;

import com.dsh105.echopet.api.EchoPetAPI;
import com.dsh105.echopet.compat.api.entity.IPet;
import net.samagames.hub.Hub;
import net.samagames.hub.common.players.PlayerManager;
import net.samagames.hub.cosmetics.common.AbstractCosmeticManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.lang.model.type.NullType;

public class PetManager extends AbstractCosmeticManager<PetCosmetic>
{
    private static final String KEY = "pet";

    public PetManager(Hub hub)
    {
        super(hub, new PetRegistry(hub));
    }

    @Override
    public void enableCosmetic(Player player, PetCosmetic cosmetic, NullType useless)
    {
        IPet pet = EchoPetAPI.getAPI().givePet(player, cosmetic.getPetType(), false);
        pet.setPetName(player.getName(), false);

        cosmetic.applyCustomization(pet);

        this.cosmeticManager.setCurrentLevel(player, KEY, cosmetic.getKey());
        player.sendMessage(PlayerManager.COSMETICS_TAG + ChatColor.GREEN + "Votre animal vient de sortir de l'écurie !");
    }

    @Override
    public void disableCosmetic(Player player, boolean logout, NullType useless)
    {
        if (EchoPetAPI.getAPI().hasPet(player))
        {
            EchoPetAPI.getAPI().removePet(player, false, false);

            if (!logout)
            {
                this.cosmeticManager.resetLevel(player, KEY);
                player.sendMessage(PlayerManager.COSMETICS_TAG + ChatColor.GREEN + "Votre animal disparait dans l'ombre...");
            }
        }
    }

    @Override
    public void restoreCosmetic(Player player)
    {
        String value = this.cosmeticManager.getItemLevelForPlayer(player, KEY);

        if(value != null && !value.isEmpty())
            this.enableCosmetic(player, this.getRegistry().getElementByStorageName(value));
    }

    @Override
    public void update() {}
}