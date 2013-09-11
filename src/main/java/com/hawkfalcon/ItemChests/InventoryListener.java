package com.hawkfalcon.ItemChests;


import com.hawkfalcon.ItemChests.API.PlayerItemChestOpenEvent;
import com.hawkfalcon.ItemChests.API.PlayerItemChestReceiveItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;


public class InventoryListener implements Listener {
    public ItemChests p;

    public InventoryListener(ItemChests m) {
        this.p = m;
    }

    InventoryAction[] iaa = {InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.NOTHING};
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
                        giveItem(player, event.getCurrentItem());
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
     * @param player
     * @param item
     */
    public void giveItem(Player player, ItemStack item) {
        if (p.getChestType() == ChestType.INFINITE) {
            recieveItem(player, item);
        } else {
            if (!p.playerHasLimitedUses(player)) {
                p.addLimitedUses(player);
            } else {
                if (p.canUse(player)) {
                    p.decreasePlayerUses(player);
                    recieveItem(player, item);
                } else {
                    message("You have reached the max items for today", player.getName());
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
     * @param player
     * @param item
     */
    public void recieveItem(Player player, ItemStack item) {
        PlayerItemChestReceiveItemEvent e = new PlayerItemChestReceiveItemEvent(player, item);
        Bukkit.getPluginManager().callEvent(e);
        player.getInventory().addItem(item);
        message(ChatColor.translateAlternateColorCodes('&', p.getConfig().getString("receivedmessage").replace("{amount}", item.getAmount() + "").replace("{item}", item.getType().toString())), player.getName());
    }
}
