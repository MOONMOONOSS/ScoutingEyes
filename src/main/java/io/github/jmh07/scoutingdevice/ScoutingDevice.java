package io.github.jmh07.scoutingdevice;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;


public final class ScoutingDevice extends JavaPlugin implements CommandExecutor, Listener {

    final private String SCOUT_DEVICE_NAME = ChatColor.GOLD + "Eye of the Lord";
    final private String YAPPP_DESERTER_MSG = ChatColor.DARK_RED + "You feel as though you are being watched...";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Scouting Device plugin has started!");
        this.getCommand("scoutdevice").setExecutor(this::onCommand);
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && args.length == 1) {
            int amount;
            try{
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException eeeel){
                return false;
            }
            Player player = (Player) sender;
            ItemStack scoutingDevice = createScoutingDevice();
            scoutingDevice.setAmount(amount);
            player.getInventory().addItem(scoutingDevice);
        }

        return true;
    }

    @EventHandler()
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        Action action = e.getAction();
        ItemStack item = e.getItem();

        boolean didRightClick = action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
        boolean hasItem = e.getItem() != null;

        if(didRightClick && hasItem) {
            if (item.getItemMeta().getDisplayName().equals(SCOUT_DEVICE_NAME)) {
                e.setCancelled(true);
                item.setAmount(item.getAmount() - 1);

                int count = 0;
                ArrayList<String> dirList = new ArrayList<>();

                for (Player ds : Bukkit.getOnlinePlayers()) {
                    if (player == ds) continue;
                    if (ds.getWorld().equals(player.getWorld()) && ds.getGameMode().equals(GameMode.SURVIVAL)) {
                        if (ds.getLocation().distance(player.getLocation()) <= 250) {
                            ds.sendMessage(YAPPP_DESERTER_MSG);

                            //some poggers math to find where a player is in relation to you.

                            //If you are looking in their direction straight on: angle = ~0 or ~360
                            //if you are looking in the opposite direction: angle = ~180
                            Vector inBetween = ds.getLocation().clone().subtract(player.getLocation()).toVector();
                            Vector lookVec = player.getLocation().getDirection();

                            //YAPPP MATH
                            double angleDir = (Math.atan2(inBetween.getZ(), inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
                            double angleLook = (Math.atan2(lookVec.getZ(), lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;

                            double angle = (angleDir - angleLook + 360) % 360;

                            if (angle < 0) {
                                angle += 360;
                            }

                            String directions[] = {"North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West"};


                            String dir = directions[(int) Math.round((((double) angle % 360) / 45)) % 8];

                            if (!dirList.contains(dir)) {
                                dirList.add(dir);
                            }

                            ++count;

                        }
                    }
                }

                if (count > 0) {
                    String dirString = String.join(", ", dirList);
                    player.sendMessage(ChatColor.GOLD + "You detect " + count + " people. " + dirString + " from here.");
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You feel as though you detected nobody...");
                }
            }
        }
    }



    private ItemStack createScoutingDevice() {
        ItemStack scoutDevice = new ItemStack(Material.ENDER_EYE);

        ItemMeta scoutDeviceMeta = scoutDevice.getItemMeta();

        scoutDeviceMeta.setDisplayName(SCOUT_DEVICE_NAME);

        ArrayList<String> scoutDeviceLore = new ArrayList<>();

        scoutDeviceLore.add(ChatColor.BLUE + "The Lord blessed this Eye with the power of finding deserters.");

        scoutDeviceMeta.setLore(scoutDeviceLore);

        scoutDevice.setItemMeta(scoutDeviceMeta);

        return scoutDevice;
    }

}
