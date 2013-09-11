package com.hawkfalcon.ItemChests;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Commands implements CommandExecutor {
    public ItemChests p;

    public Commands(ItemChests m) {
        this.p = m;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            String n = sender.getName();
            if ((cmd.getName().equalsIgnoreCase("ic")) || (cmd.getName().equalsIgnoreCase("itemchest"))) {
                if (args.length == 0) {
                    message("Create an ItemChest with /ic create", n);
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create") && ((sender.hasPermission("ic.create")))) {
                        ItemStack c = new ItemStack(Material.CHEST, 1);
                        ItemMeta im = c.getItemMeta();
                        im.setDisplayName(ChatColor.RESET + "ItemChest");
                        ArrayList<String> lore = new ArrayList<String>();
                        lore.add("Distribute infinite items!");
                        im.setLore(lore);
                        c.setItemMeta(im);
                        p.getServer().getPlayer(n).getInventory().addItem(c);
                        message("You have received an ItemChest!", n);
                    }
                    if (args.length == 2) {
                        String q = args[1];
                        if (args[0].equalsIgnoreCase("limit") && ((sender.hasPermission("ic.limit")))) {
                            p.setLimit(Integer.parseInt(q));
                            message("Limit changed to " + p.getLimit() + " items per day.", n);
                            p.getConfig().set("limit", p.getLimit());
                            p.saveConfig();

                        } else if (args[0].equalsIgnoreCase("mode") && ((sender.hasPermission("ic.mode")))) {
                            p.setChestType(ChestType.valueOf(args[1]));
                        } else {
                            sender.sendMessage("You do not have permission to do this!");
                        }
                    }
                }
            }
        }
        return false;
    }

    public void message(String message, String sender) {
        p.getServer().getPlayer(sender).sendMessage("[" + ChatColor.GREEN + "ItemChest" + ChatColor.WHITE + "] " + message);
    }
}
