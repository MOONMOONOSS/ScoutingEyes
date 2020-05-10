package io.github.jmh07.scoutingdevice;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class ScoutingDevice extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Scouting Device plugin has started!");
        this.getCommand("scoutdevice").setExecutor(this::onCommand);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack scoutingDevice = createScoutingDevice();
            player.getInventory().addItem(scoutingDevice);
        }

        return true;
    }

    private ItemStack createScoutingDevice() {
        ItemStack scoutDevice = new ItemStack(Material.ENDER_EYE);

        ItemMeta scoutDeviceMeta = scoutDevice.getItemMeta();

        scoutDeviceMeta.setDisplayName(ChatColor.GOLD + "Eye of the Lord");

        ArrayList<String> scoutDeviceLore = new ArrayList<>();

        scoutDeviceLore.add(ChatColor.BLUE + "The Lord blessed this Eye with the power of finding those who are not worthy of living.");

        scoutDeviceMeta.setLore(scoutDeviceLore);

        scoutDevice.setItemMeta(scoutDeviceMeta);

        return scoutDevice;
    }

}
