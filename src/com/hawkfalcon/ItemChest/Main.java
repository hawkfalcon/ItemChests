package com.hawkfalcon.ItemChest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.Listener;

public class Main extends JavaPlugin {
	public boolean infinite = true;
	public Integer limit = 1;
	public CommandExecutor Commands = new Commands(this);
	public Listener InventoryListener = new InventoryListener(this);
	public HashMap<String, Integer> playerLimit = new HashMap<String, Integer>();

	public void onEnable(){
		final File f = new File(getDataFolder(), "config.yml");
		if (!f.exists()){
			saveDefaultConfig();
		}
		try {
			MetricsLite metrics = new MetricsLite(this); metrics.start();
		} catch (IOException e) {
			System.out.println("Error Submitting stats!");
		}
		getServer().getPluginManager().registerEvents(InventoryListener, this);
		getCommand("ic").setExecutor(Commands);
		infinite = getConfig().getBoolean("infinite");
		limit = getConfig().getInt("limit");
		startTimer();
	}
	private void startTimer() {
		@SuppressWarnings("unused")
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			public void run(){
				playerLimit.clear();
			}          
		},0,24*60*60*20);		
	}	
}