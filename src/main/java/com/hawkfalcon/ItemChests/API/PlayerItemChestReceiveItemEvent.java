package com.hawkfalcon.ItemChests.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerItemChestReceiveItemEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel = false;
    private Player player;
    private ItemStack item;

    public PlayerItemChestReceiveItemEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItem() {
        return this.item;
    }

}
