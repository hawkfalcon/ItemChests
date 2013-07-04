package com.hawkfalcon.ItemChests;


import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hawkfalcon.ItemChests.API.PlayerItemChestOpenEvent;
import com.hawkfalcon.ItemChests.API.PlayerItemChestReceiveItemEvent;


public class InventoryListener implements Listener {
    public ItemChests p;

    public InventoryListener(ItemChests m) {
        this.p = m;
    }

    InventoryAction[] iaa = {InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.NOTHING };
    public ArrayList<InventoryAction> ias = new ArrayList<InventoryAction>(Arrays.asList(iaa));

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST && ((Chest) event.getClickedBlock().getState()).getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            PlayerItemChestOpenEvent e = new PlayerItemChestOpenEvent(event.getPlayer(), event.getClickedBlock().getLocation());
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        /**
         * ItemChest
         */
        if (event.getInventory().getType() == InventoryType.CHEST && event.getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        /**
         * ItemChest
         */
        if (event.getBlock() instanceof Chest) {
            Chest chest = (Chest) event.getBlock();
            if (chest.getInventory().getType() == InventoryType.CHEST && chest.getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
                if (!event.getPlayer().hasPermission("ic.add")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String name = player.getName();
        if (event.getInventory().getType() == InventoryType.CHEST && event.getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            if (event.getSlotType() != SlotType.CONTAINER)
                return;
            InventoryAction ia = event.getAction();
            if (event.getRawSlot() < 27 && event.getRawSlot() > -1) {
                if (!player.hasPermission("ic.recieve")) {
                    event.setCancelled(true);
                    return;
                }
                if (ia == InventoryAction.PLACE_ALL || ia == InventoryAction.PLACE_ONE || ia == InventoryAction.PLACE_SOME) {
                    if (player.hasPermission("ic.add")) {
                        return;
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                } else if (ia == InventoryAction.PICKUP_ONE || ia == InventoryAction.PICKUP_SOME || ia == InventoryAction.PICKUP_ALL || ia == InventoryAction.PICKUP_HALF) {
                    if (event.getClick() == ClickType.LEFT && player.hasPermission("ic.add")) {
                        return;
                    } else {
                        event.setCancelled(true);
                        giveItem(name, event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(true);
                }
            } else {
                if (ias.contains(ia))
                    return;
                event.setCancelled(true);
            }
        }
    }

    /**
     * Check config, whether to give item
     * 
     * @param name
     * @param item
     */
    public void giveItem(String name, ItemStack item) {
        if (p.infinite) {
            recieveItem(name, item);
        } else {
            if (!p.playerLimit.containsKey(name)) {
                p.playerLimit.put(name, p.limit);
            } else {
                if (p.playerLimit.get(name) > 0) {
                    p.playerLimit.put(name, p.playerLimit.get(name) - 1);
                    recieveItem(name, item);
                } else {
                    message("You have reached the max items for today", name);
                }
            }
        }
    }

    public void message(String message, String sender) {
        p.getServer().getPlayer(sender).sendMessage("[" + ChatColor.GREEN + "ItemChest" + ChatColor.WHITE + "] " + message);
    }

    /**
     * Actually give item
     * 
     * @param name
     * @param item
     */
    public void recieveItem(String name, ItemStack item) {
        PlayerItemChestReceiveItemEvent e = new PlayerItemChestReceiveItemEvent(p.getServer().getPlayerExact(name), item);
        Bukkit.getPluginManager().callEvent(e);
        p.getServer().getPlayer(name).getInventory().addItem(item);
        message(ChatColor.translateAlternateColorCodes('&', p.getConfig().getString("receivedmessage").replace("{amount}", item.getAmount() + "").replace("{item}", item.getType().toString())), name);
    }
}
