package com.hawkfalcon.ItemChests;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hawkfalcon.ItemChests.API.PlayerItemChestOpenEvent;


public class InventoryListener implements Listener {
    public ItemChests p;

    public InventoryListener(ItemChests m) {
        this.p = m;
    }

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
    public void onPlayerInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String name = player.getName();
        /**
         * ItemChest
         */
        if (event.getInventory().getType() == InventoryType.CHEST && event.getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            if (event.getSlotType() == SlotType.CONTAINER) {
                InventoryAction ia = event.getAction();
                /**
                 * Top of inventory
                 */
                if (event.getRawSlot() < 27) {
                    /**
                     * if recieve permission is negated, cancel any event and
                     * break
                     */
                    if (!player.hasPermission("ic.recieve")) {
                        event.setCancelled(true);
                        return;
                    }
                    ItemStack item = event.getCurrentItem();
                    /**
                     * Placing an item
                     */
                    if (ia == InventoryAction.PLACE_ALL || ia == InventoryAction.PLACE_ONE || ia == InventoryAction.PLACE_SOME) {
                        /**
                         * Cancel placing if no perms
                         */
                        if (!player.hasPermission("ic.add")) {
                            event.setCancelled(true);
                        } else {
                            return;
                        }
                        /**
                         * Picking up an item
                         */
                    } else if (ia == InventoryAction.PICKUP_ONE || ia == InventoryAction.PICKUP_SOME || ia == InventoryAction.PICKUP_ONE || ia == InventoryAction.PICKUP_HALF) {
                        /**
                         * if its left, and leftclick is false, cancel the
                         * event
                         */
                        if (event.getClick() == ClickType.LEFT && !p.getConfig().getBoolean("leftclick")) {
                            event.setCancelled(true);
                            /**
                             * Right click, or left click and true, give item
                             */
                        } else {
                            event.setCancelled(true);
                            giveItem(name, item);
                        }
                    } else {
                        /**
                         * Cancel the rest
                         */
                        event.setCancelled(true);
                    }
                } else {
                    /**
                     * Prevent stealing from top inventory
                     */
                    if (ia == InventoryAction.COLLECT_TO_CURSOR || ia == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        event.setCancelled(true);
                    }
                }
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
        p.getServer().getPlayer(name).getInventory().addItem(item);
        message("Received " + item.getAmount() + " " + item.getType() + "!", name);
    }
}
