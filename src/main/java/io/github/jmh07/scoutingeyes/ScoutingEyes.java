package io.github.jmh07.scoutingeyes;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
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
import java.util.TreeMap;
import java.util.Map;



public final class ScoutingEyes extends JavaPlugin implements CommandExecutor, Listener {

    final private String SCOUT_EYES_NAME = ChatColor.GOLD + "Eye of Jeremiah";
    final private String SCOUT_OMEGA_EYES_NAME = ChatColor.GOLD + "Eye of Mormoon";
    final private String YAPPP_DESERTER_MSG = ChatColor.DARK_RED + "You feel as though you are being watched...";
    final private String YAPPP_DETECT_NOBODY = ChatColor.DARK_RED + "You feel as though you detected nobody...";
    final private String SCOUT_EYES_LORE_P1 = ChatColor.BLUE + "Lord Jeremiah blessed this Eye";
    final private String SCOUT_EYES_LORE_P2 = ChatColor.BLUE + "with the power of finding deserters.";
    final private String SCOUT_OMEGA_EYES_LORE_P1 = ChatColor.BLUE + "The Lord blessed this Eye with the power";
    final private String SCOUT_OMEGA_EYES_LORE_P2 = ChatColor.BLUE + "of finding deserters within extreme distances.";
    final private String ERROR_COMMAND_USAGE = ChatColor.DARK_RED + "Command usage => /scouteyes basic/omega <amount>";
    final private String ERROR_NO_PERM = ChatColor.DARK_RED + "You do not have permission to use this command.";
    final private String ERROR_PLAYERS_ONLY = "This command is for players only!";
    final private String SCOUT_EYES_GIVEN = ChatColor.GOLD + "Item may have been added to your inventory! Check to make sure.";
    final private String SCOUT_EYES_USED = ChatColor.AQUA + "You used the Scout's eye.";
    //final private String ERROR_PLAYER_INVENTORY_FULL = ChatColor.DARK_RED + "Inventory Full!";

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

        boolean isPlayer = sender instanceof Player;
        boolean hasTwoArgs = args.length == 2;

        if (isPlayer && hasTwoArgs) {
            Player player = (Player) sender;
            if (player.hasPermission("scouteyes.give")) {

                String name;
                try {
                    name = args[0];
                    name = name.toLowerCase();
                } catch (Exception eeeel) {
                    player.sendMessage(ERROR_COMMAND_USAGE);
                    return false;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException eeeel) {
                    player.sendMessage(ERROR_COMMAND_USAGE);
                    return false;
                }

                ItemStack scoutingDevice;

                if (name.equals("basic")) {
                    scoutingDevice = createScoutingEyes();
                } else if (name.equals("omega")) {
                    scoutingDevice = createOmegaScoutingEyes();
                } else {
                    player.sendMessage(ERROR_COMMAND_USAGE);
                    return false;
                }


                scoutingDevice.setAmount(amount);
                player.getInventory().addItem(scoutingDevice);
                player.sendMessage(SCOUT_EYES_GIVEN);

            }else{
                ((Player) sender).sendMessage(ERROR_NO_PERM);
            }
        } else {
            if(isPlayer) {
                ((Player)sender).sendMessage(ERROR_COMMAND_USAGE);
                return false;
            }else {
                getLogger().info(ERROR_PLAYERS_ONLY);
            }
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
                //Cancel item use
                e.setCancelled(true);

                player.sendMessage(SCOUT_EYES_USED);

                int distance = 0;

                if(isScoutEyes) {distance = EYES_DISTANCE;}
                if(isOmegaScoutEyes) {distance = OMEGA_EYES_DISTANCE;}

                item.setAmount(item.getAmount() - 1);

                int count = 0;

                //String directions[] = {"12 o'clock!", "1 o'clock!", "2 o'clock!" , "3 o'clock!", "4 o'clock!",
                       // "5 o'clock!", "6 o'clock!", "7 o'clock!", "8 o'clock!", "9 o'clock!", "10 o'clock!", "11 o'clock!"};

                int directions[] = {12, 1, 2 , 3, 4, 5, 6, 7, 8, 9, 10, 11};

                //Map<String, Integer> dirMap = new HashMap<String, Integer>();

                TreeMap<Integer, Integer> directionTreeMap = new TreeMap<Integer, Integer>();

                //Fill TreeMap with keys as directions and values set to 0
                for(int dir: directions) {
                    directionTreeMap.put(dir, 0);
                }

                for (Player ds : Bukkit.getOnlinePlayers()) {
                    if (player == ds) continue;
                    if (ds.hasPermission("scouteyes.hidden")) continue;

                    boolean isInSameWorld = ds.getWorld().equals(player.getWorld());
                    boolean isInSurvivalMode = ds.getGameMode().equals(GameMode.SURVIVAL);
                    boolean isPlayerDead = ds.isDead();

                    if (isInSameWorld && isInSurvivalMode && !isPlayerDead) {

                        double getDistanceBetweenPlayers = ds.getLocation().distance(player.getLocation());

                        if (getDistanceBetweenPlayers <= distance) {
                            ds.sendMessage(YAPPP_DESERTER_MSG);

                            //some poggers math to find where a player is in relation to you.

                            /*
                            If you are looking in their direction straight on: angle = ~0 or ~360
                            If you are looking in the opposite direction: angle = ~180
                            If you are looking straight ahead and they're to the absolute right of you angle = ~90
                            If you are looking straight ahead and they're to the absolute left of you angle = ~270
                            */
                            Vector inBetween = ds.getLocation().clone().subtract(player.getLocation()).toVector();
                            Vector lookVec = player.getLocation().getDirection();

                            //YAPPP MATH
                            double angleDir = (Math.atan2(inBetween.getZ(), inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
                            double angleLook = (Math.atan2(lookVec.getZ(), lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;

                            double angle = (angleDir - angleLook + 360) % 360;

                            if (angle < 0) {
                                angle += 360;
                            }

                            //String dir = directions[(int) Math.round((((double) angle % 360) / 30)) % 12];
                            int dir = directions[(int) Math.round((((double) angle % 360) / 30)) % 12];

                            int dirMapValue = directionTreeMap.get(dir);

                            directionTreeMap.put(dir, ++dirMapValue);

                            ++count;

                        }
                    }
                }

                if (count > 0) {

                    //Set<Map.Entry<String, Integer>> setOfDir = dirMap.entrySet();

                    for(Map.Entry<Integer, Integer> d : directionTreeMap.entrySet()) {
                        if(d.getValue() > 0){

                            String word = d.getValue() > 1 ? " people at " : " person at ";

                            player.sendMessage(ChatColor.GOLD + "You detect " + d.getValue() + word + d.getKey() + " o'clock!");
                        }
                    }

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

        scoutEyesLore.add(SCOUT_EYES_LORE_P1);
        scoutEyesLore.add(SCOUT_EYES_LORE_P2);

        scoutEyesMeta.setLore(scoutEyesLore);

        scoutEyes.setItemMeta(scoutEyesMeta);

        return scoutEyes;
    }

    private ItemStack createOmegaScoutingEyes() {
        ItemStack scoutOmegaEyes = new ItemStack(Material.ENDER_EYE);

        ItemMeta scoutOmegaEyesMeta = scoutOmegaEyes.getItemMeta();

        scoutOmegaEyesMeta.setDisplayName(SCOUT_OMEGA_EYES_NAME);

        ArrayList<String> scoutOmegaEyesLore = new ArrayList<>();

        scoutOmegaEyesLore.add(SCOUT_OMEGA_EYES_LORE_P1);
        scoutOmegaEyesLore.add(SCOUT_OMEGA_EYES_LORE_P2);

        scoutOmegaEyesMeta.setLore(scoutOmegaEyesLore);

        scoutOmegaEyes.setItemMeta(scoutOmegaEyesMeta);

        return scoutOmegaEyes;
    }
}
