package io.github.jmh07.scoutingeyes;

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


public final class ScoutingEyes extends JavaPlugin implements CommandExecutor, Listener {

    final private String SCOUT_EYES_NAME = ChatColor.GOLD + "Eye of Jeremiah";
    final private String SCOUT_OMEGA_EYES_NAME = ChatColor.GOLD + "Eye of Mormoon";
    final private String YAPPP_DESERTER_MSG = ChatColor.DARK_RED + "You feel as though you are being watched...";
    final private String YAPPP_DETECT_NOBODY = ChatColor.DARK_RED + "You feel as though you detected nobody...";
    final private String SCOUT_EYES_LORE = ChatColor.BLUE + "Lord Jeremiah blessed this Eye with the power of finding deserters.";
    final private String SCOUT_OMEGA_EYES_LORE = ChatColor.BLUE + "The Lord blessed this Eye with the power of finding deserters within extreme distances.";

    final private int EYES_DISTANCE = 250;
    final private int OMEGA_EYES_DISTANCE = 2000;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Scouting Eyes plugin has started!");
        this.getCommand("scouteyes").setExecutor(this::onCommand);
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && args.length == 2) {

            String name;
            try{
                name = args[0];
                name = name.toLowerCase();
            } catch (Exception eeeel){
                return false;
            }

            int amount;
            try{
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException eeeel){
                return false;
            }

            Player player = (Player) sender;
            ItemStack scoutingDevice;

            if(name.equals("basic")){
                scoutingDevice = createScoutingEyes();
            }else if(name.equals("omega")){
                scoutingDevice = createOmegaScoutingEyes();
            }else{
                return false;
            }
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

            boolean isScoutEyes = item.getItemMeta().getDisplayName().equals(SCOUT_EYES_NAME);
            boolean isOmegaScoutEyes = item.getItemMeta().getDisplayName().equals(SCOUT_OMEGA_EYES_NAME);


            if (isScoutEyes || isOmegaScoutEyes) {

                int distance = 0;

                if(isScoutEyes) {distance = EYES_DISTANCE;}
                if(isOmegaScoutEyes) {distance = OMEGA_EYES_DISTANCE;}

                e.setCancelled(true);
                item.setAmount(item.getAmount() - 1);

                int count = 0;
                ArrayList<String> dirList = new ArrayList<>();

                for (Player ds : Bukkit.getOnlinePlayers()) {
                    if (player == ds) continue;

                    boolean isInSameWorld = ds.getWorld().equals(player.getWorld());
                    boolean isInSurvivalMode = ds.getGameMode().equals(GameMode.SURVIVAL);

                    if (isInSameWorld && isInSurvivalMode) {
                        if (ds.getLocation().distance(player.getLocation()) <= distance) {
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
                    player.sendMessage(YAPPP_DETECT_NOBODY);
                }
            }
        }
    }



    private ItemStack createScoutingEyes() {
        ItemStack scoutEyes = new ItemStack(Material.ENDER_PEARL);

        ItemMeta scoutEyesMeta = scoutEyes.getItemMeta();

        scoutEyesMeta.setDisplayName(SCOUT_EYES_NAME);

        ArrayList<String> scoutEyesLore = new ArrayList<>();

        scoutEyesLore.add(SCOUT_EYES_LORE);

        scoutEyesMeta.setLore(scoutEyesLore);

        scoutEyes.setItemMeta(scoutEyesMeta);

        return scoutEyes;
    }

    private ItemStack createOmegaScoutingEyes() {
        ItemStack scoutOmegaEyes = new ItemStack(Material.ENDER_EYE);

        ItemMeta scoutOmegaEyesMeta = scoutOmegaEyes.getItemMeta();

        scoutOmegaEyesMeta.setDisplayName(SCOUT_OMEGA_EYES_NAME);

        ArrayList<String> scoutOmegaEyesLore = new ArrayList<>();

        scoutOmegaEyesLore.add(SCOUT_OMEGA_EYES_LORE);

        scoutOmegaEyesMeta.setLore(scoutOmegaEyesLore);

        scoutOmegaEyes.setItemMeta(scoutOmegaEyesMeta);

        return scoutOmegaEyes;
    }



}
