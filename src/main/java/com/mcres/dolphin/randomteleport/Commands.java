package com.mcres.dolphin.randomteleport;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;

/**
 * @author dolphin
 * @Description:
 * @date 2019/12/14
 */
public class Commands implements CommandExecutor
{
    public static final HashMap<Player, Long> coolDowns = Maps.newHashMap();
    public static final HashMap<Player, ItemStack> chestplaces = Maps.newHashMap();
    public static final ItemStack elytra = new ItemStack(Material.ELYTRA);
    protected static void init()
    {
        elytra.addEnchantment(Enchantment.BINDING_CURSE, 1);
        elytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        ItemMeta meta = elytra.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName(Main.getLanguage().getConfiguration().getString("elytra"));
        elytra.setItemMeta(meta);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        YamlConfiguration language = Main.getLanguage().getConfiguration();
        if (args.length >= 1)
        {
            if (sender.hasPermission("randomteleport.command.randomteleport.others"))
            {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isOnline())
                {
                    randomTeleport(target, true);
                }
                else
                {
                    sender.sendMessage(language.getString("player-not-exists").replaceAll("%player%", args[0]));
                }
            }
            else
            {
                sender.sendMessage(language.getString("no-permission"));
            }
        }
        else
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                randomTeleport(player);
            }
            else
            {
                sender.sendMessage("/" + label + " [player]");
            }
        }
        return true;
    }

    public static void randomTeleport(@NotNull Player player)
    {
        randomTeleport(player, false);
    }

    public static void randomTeleport(@NotNull Player player, boolean bypassCounter)
    {
        YamlConfiguration language = Main.getLanguage().getConfiguration();
        YamlConfiguration counter = Main.getCounter().getConfiguration();
        FileConfiguration configuration = Main.getInstance().getConfig();
        String key = configuration.getBoolean("online-mode") ? player.getUniqueId().toString() : player.getName();
        int count = 0;
        if (bypassCounter || player.hasPermission("randomteleport.counter.bypass") || (count = counter.contains(key) ? counter.getInt(key) : configuration.getInt("default-count")) > 0)
        {
            long coolDown = getCoolDown(player);
            if (coolDown == 0 || coolDown > configuration.getLong("cooldown"))
            {
                if (isAllowRandomTeleport(player))
                {
                    ItemStack chestplate = player.getInventory().getChestplate();
                    if (!elytra.isSimilar(chestplate))
                    {
                        chestplaces.put(player, chestplate);
                        player.getInventory().setChestplate(elytra);
                        int size = (int) ((player.getWorld().getWorldBorder().getSize() / 2) - configuration.getDouble("reduce"));
                        int x = getRandomInt(size);
                        int z = getRandomInt(size);
                        coolDowns.put(player, System.currentTimeMillis());
                        if (!(bypassCounter || player.hasPermission("randomteleport.counter.bypass")))
                        {
                            counter.set(key, (counter.contains(key) ? count : configuration.getInt("default-count")) - 1);
                            Main.getCounter().save();
                        }
                        player.teleport(new Location(player.getWorld(), x, player.getWorld().getMaxHeight(), z));
                        player.sendTitle(language.getString("title"), language.getString("subtitle"), configuration.getInt("title.fade-in"), configuration.getInt("title.stay"), configuration.getInt("title.fade-out"));
                    }
                    else
                    {
                        player.sendMessage(language.getString("now-random-teleporting"));
                    }
                }
                else
                {
                    player.sendMessage(language.getString("world-not-allow"));
                }
            }
            else
            {
                player.sendMessage(language.getString("cooling-down").replaceAll("%seccond%", String.valueOf(coolDown)));
            }
        }
        else
        {
            player.sendMessage(language.getString("count-insufficient"));
        }
    }

    public static long getCoolDown(@NotNull Player player)
    {
        if (player.hasPermission("randomteleport.cooldown.bypass") || !coolDowns.containsKey(player)) return 0;
        long ms = System.currentTimeMillis();
        return (ms - coolDowns.get(player)) / 1000;
    }

    public static boolean isAllowRandomTeleport(@NotNull Player player)
    {
        return player.hasPermission("randomteleport.world.limit.bypass") || Main.getInstance().getConfig().getStringList("allow-worlds").contains(player.getWorld().getName());
    }

    public static int getRandomInt(int max)
    {
        int[] temp=new int[new Random(System.nanoTime()).nextInt(101)+1];
        for(int i=0;i<temp.length;i++)
          temp[i]=new Random(System.nanoTime()+i+new Random(System.nanoTime()).nextInt(101)).nextInt(max+1);
        return (new Random(System.nanoTime()+temp[new Random(System.nanoTime()).nextInt(temp.length)]).nextBoolean()?1:-1)*temp[new Random(System.nanoTime()).nextInt(temp.length)];

    }
}
