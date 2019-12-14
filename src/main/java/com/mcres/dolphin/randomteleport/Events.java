package com.mcres.dolphin.randomteleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author dolphin
 * @Description:
 * @date 2019/12/14
 */
public class Events implements Listener
{
    @EventHandler
    public void actionEntityToggleGlide(@NotNull EntityToggleGlideEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (!event.isGliding())
            {
                Player player = (Player) event.getEntity();
                replaceChestPlace(player);
            }
        }
    }

    @EventHandler
    public void actionPlayerQuit(@NotNull PlayerQuitEvent event)
    {
        replaceChestPlace(event.getPlayer());
    }

    @EventHandler
    public void actionPlayerDeath(@NotNull PlayerDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            replaceChestPlace(player);
        }
    }

    public static void replaceChestPlace(@NotNull Player player)
    {
        ItemStack chestPlace;
        if ((chestPlace = player.getInventory().getChestplate()) != null)
        {
            if (chestPlace.isSimilar(Commands.elytra))
            {
                player.getInventory().setChestplate(Commands.chestplaces.remove(player));
            }
        }
    }
}