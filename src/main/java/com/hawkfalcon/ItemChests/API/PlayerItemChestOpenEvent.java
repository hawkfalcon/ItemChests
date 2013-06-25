package com.hawkfalcon.ItemChests.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerItemChestOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancel = false;
    private Player p;
    private Location chestLoc;

    public PlayerItemChestOpenEvent(Player p, Location chestLoc) {
        this.p = p;
        this.chestLoc = chestLoc;
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
        return this.p;
    }

    public Location getChestLocation() {
        return this.chestLoc;
    }

}
