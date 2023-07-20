package com.github.vaaaaz.events;

import com.github.vaaaaz.mysql.SQLutils;
import com.github.vaaaaz.Parkour;
import com.github.vaaaaz.utils.Cords;
import com.github.vaaaaz.utils.InventoryAPI;
import com.github.vaaaaz.utils.Itens;
import com.github.vaaaaz.utils.MillisecondConverter;
import com.github.vaaaaz.commands.ParkourCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ParkourEvents implements Listener, Cords {


    public static HashMap<String, Long> hashplayer = new HashMap<>();
    public static HashMap<String, Location> playerpoint = new HashMap<>();
    public static HashMap<String, HashMap<Integer, ItemStack>> guardarhashitens = new HashMap<>();
    private int pontofodase = 0;
    private int tempo = 0;


    @EventHandler
    void saiu(PlayerQuitEvent e) {
        hashplayer.remove(e.getPlayer().getName());
        playerpoint.remove(e.getPlayer().getName());
        ParkourCommand.removespawnpoints.remove(e.getPlayer());
        ParkourCommand.spawnpointsstaff.remove(e.getPlayer());
        ParkourCommand.playerlist.remove(e.getPlayer());
        ParkourCommand.playerlist2.remove(e.getPlayer());
    }

    @EventHandler
    void moveu(PlayerMoveEvent e) {
        tempo = 0;
    }

    //
//                Parkour.getLoc().getConfig().set("Spawnpoints."+numerodepoints+".x");
    @EventHandler
    void quebrou(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.IRON_PLATE)) {
            if (ParkourCommand.removespawnpoints.contains(e.getPlayer())) {
                if (hasCheckPoint(e.getBlock().getLocation())) {
                    e.getPlayer().sendMessage("§aVocê removeu o spawnpoint de número " + getCheckPoint(e.getBlock().getLocation()));
                    Parkour.getLoc().getConfig().set("Spawnpoints." + getCheckPoint(e.getBlock().getLocation()), null);
                    Parkour.getLoc().saveConfig();
                }
            }
        }
    }

    @EventHandler
    void colocouPlate(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType().equals(Material.GOLD_PLATE)) {
            if (ParkourCommand.playerlist.contains(e.getPlayer())) {
                Location loc = e.getBlockPlaced().getLocation();
                Parkour.getLoc().getConfig().set("Inicio.x", loc.getX());
                Parkour.getLoc().getConfig().set("Inicio.y", loc.getY());
                Parkour.getLoc().getConfig().set("Inicio.z", loc.getZ());
                Parkour.getLoc().getConfig().set("Inicio.yaw", loc.getYaw());
                Parkour.getLoc().getConfig().set("Inicio.pitch", loc.getPitch());
                Parkour.getLoc().saveConfig();
                e.getPlayer().sendMessage("§aO inicio do parkour foi setado");
                ParkourCommand.playerlist.remove(e.getPlayer());
                return;
            }
            if (ParkourCommand.playerlist2.contains(e.getPlayer())) {
                Location loc = e.getBlockPlaced().getLocation();
                Parkour.getLoc().getConfig().set("Fim.x", loc.getX());
                Parkour.getLoc().getConfig().set("Fim.y", loc.getY());
                Parkour.getLoc().getConfig().set("Fim.z", loc.getZ());
                Parkour.getLoc().getConfig().set("Fim.yaw", loc.getYaw());
                Parkour.getLoc().getConfig().set("Fim.pitch", loc.getPitch());
                Parkour.getLoc().saveConfig();
                e.getPlayer().sendMessage("§aO fim do parkour foi setado");
                ParkourCommand.playerlist2.remove(e.getPlayer());
                return;
            }
            return;
        }
        if (ParkourCommand.spawnpointsstaff.contains(e.getPlayer())) {
            if (e.getBlockPlaced().getType().equals(Material.IRON_PLATE)) {
                Location loc = e.getBlockPlaced().getLocation();

                int numerodepoints = Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false) == null ? 1 : 1 + Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false).size();
                Parkour.getLoc().getConfig().set("Spawnpoints." + numerodepoints + ".x", loc.getX());
                Parkour.getLoc().getConfig().set("Spawnpoints." + numerodepoints + ".y", loc.getY());
                Parkour.getLoc().getConfig().set("Spawnpoints." + numerodepoints + ".z", loc.getZ());
                Parkour.getLoc().getConfig().set("Spawnpoints." + numerodepoints + ".yaw", loc.getYaw());
                Parkour.getLoc().getConfig().set("Spawnpoints." + numerodepoints + ".pitch", loc.getPitch());
                Parkour.getLoc().saveConfig();
                e.getPlayer().sendMessage("§aVocê setou o spawnpoint de número " + numerodepoints);
            }
        }
    }

    @EventHandler
    void pisouNaPlate(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
                e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (e.getItem() == null || e.getItem().getType() == Material.AIR) return;
            if (hashplayer.containsKey(p.getName())) {
                if (e.getItem().getType().equals(Material.IRON_PLATE)) {
                    if (hashplayer.containsKey(p.getName()) && !playerpoint.containsKey(p.getName())) {
                        p.teleport(spawn(p));
                        return;
                    }
                    p.teleport(getPointLocPlayer(p));
                    return;
                }
                if (e.getItem().getType().equals(Material.WOOD_DOOR)) {
                    playerpoint.remove(p.getName());
                    hashplayer.remove(p.getName());
                    hashplayer.put(p.getName(), System.currentTimeMillis());
                    p.teleport(spawn(p));
                    return;
                }
                if (e.getItem().getType().equals(Material.BED)) {
                    p.chat("/parkour sair");
                    return;
                }
            }
            return;
        }
        if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
                if (e.getClickedBlock().getLocation().equals(getInicio(p))) {
                    if (!hashplayer.containsKey(p.getName())) {
                        hashplayer.put(p.getName(), System.currentTimeMillis());
                        runnable(p);
                        guardarItens(p);
                        p.getInventory().clear();
                        p.getInventory().setItem(3, new Itens(Material.IRON_PLATE, 1, 0).nome("§aTeleportar ao ultimo spawnpoint.").getItemStack());
                        p.getInventory().setItem(4, new Itens(Material.WOOD_DOOR, 1, 0).nome("§cResetar.").getItemStack());
                        p.getInventory().setItem(5, new Itens(Material.BED, 1, 0).nome("§cCancelar.").getItemStack());
                        p.sendMessage(Parkour.config.getConfig().getString("mensagens.iniciado").replace("&", "§"));
                        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    }
                }
                if (e.getClickedBlock().getLocation().equals(getFim(p))) {
                    if (hashplayer.containsKey(p.getName())) {
                        p.teleport(spawn(p));
                        long finished = System.currentTimeMillis() - hashplayer.get(p.getName());
                        MillisecondConverter milli = new MillisecondConverter(finished);
                        if (new SQLutils().hasPlayer(p.getName())) {
                            if (finished > new SQLutils().getTime(p.getName()) && new SQLutils().getTime(p.getName()) > 0) {
                                p.sendMessage(Parkour.config.getConfig().getString("mensagens.finalizou-semrecord").replace("&", "§").replace("{tempo}", format(finished).replace("{tempo}", format(new SQLutils().getTime(p.getName())))));
                                p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
                                setItens(p);
                                hashplayer.remove(p.getName());
                                playerpoint.remove(p.getName());
                                return;
                            }
                        } else {
                            new SQLutils().setPlayer(p);
                        }
                        new SQLutils().setTime(p.getName(), finished);
                        setItens(p);
                        hashplayer.remove(p.getName());
                        playerpoint.remove(p.getName());
                        p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
                        p.sendMessage(Parkour.config.getConfig().getString("mensagens.finalizou").replace("&", "§").replace("{tempo}", format(finished)));

                    }
                    return;
                }
                return;
            }
            if (e.getClickedBlock().getType().equals(Material.IRON_PLATE)) {
                if (hashplayer.containsKey(p.getName())) {
                    if (!hasCheckPoint(e.getClickedBlock().getLocation())) return;
                    if (!playerpoint.containsKey(p.getName())) {
                        playerpoint.put(p.getName(), e.getClickedBlock().getLocation());
                        p.sendMessage(Parkour.config.getConfig().getString("mensagens.spawnpoint").replace("&", "§"));
                        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        return;
                    }
                    if (!playerpoint.get(p.getName()).equals(e.getClickedBlock().getLocation())) {
                        try {
                            if (Integer.parseInt(getCheckPoint(p, e.getClickedBlock().getLocation())) < Integer.parseInt(getCheckPoint(p, playerpoint.get(p.getName()))))
                                return;
                            playerpoint.remove(p.getName());
                            playerpoint.put(p.getName(), e.getClickedBlock().getLocation());
                            p.sendMessage(Parkour.config.getConfig().getString("mensagens.spawnpoint").replace("&", "§"));
                            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        } catch (NumberFormatException e11) {
                            System.out.println("");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    void clickEvent(InventoryClickEvent e) {
        if (e.getInventory() == null) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().equals(p.getInventory())) return;
        InventoryAPI api = Parkour.getInstance().hashinv.get(p.getName());
        if (api != null) {
            if (e.getInventory().getTitle().equals(api.getTitle())) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 3:
                        if (api.getPage() == 1) {
                            p.sendMessage("§cVocê está na primeira página.");
                            return;
                        }
                        api.previous();
                        break;
                    case 4:
                        p.closeInventory();
                        break;
                    case 5:
                        if (api.getPage() == api.getPages()) {
                            p.sendMessage("§cVocê está na última página.");
                            return;
                        }
                        api.next();
                        break;
                }
            }
        }
    }

    @EventHandler
    void voltarPoint(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                if (hashplayer.containsKey(p.getName()) && playerpoint.containsKey(p.getName())) {
                    p.teleport(getPointLocPlayer(p));
                }
            }
        }
    }

    Location getFim(Player p) {
        double xi = Parkour.getLoc().getConfig().getDouble("Fim.x");
        double yi = Parkour.getLoc().getConfig().getDouble("Fim.y");
        double zi = Parkour.getLoc().getConfig().getDouble("Fim.z");
        float yawi = (float) Parkour.getLoc().getConfig().getDouble("Fim.yaw");
        float pitchi = (float) Parkour.getLoc().getConfig().getDouble("Fim.pitch");
        return new Location(p.getWorld(), xi, yi, zi, yawi, pitchi);
    }

    Location getInicio(Player p) {
        double xi = Parkour.getLoc().getConfig().getDouble("Inicio.x");
        double yi = Parkour.getLoc().getConfig().getDouble("Inicio.y");
        double zi = Parkour.getLoc().getConfig().getDouble("Inicio.z");
        float yawi = (float) Parkour.getLoc().getConfig().getDouble("Inicio.yaw");
        float pitchi = (float) Parkour.getLoc().getConfig().getDouble("Inicio.pitch");
        return new Location(p.getWorld(), xi, yi, zi, yawi, pitchi);
    }
//getCheckPoint

    Location getPointLocPlayer(Player p) {
        double x = playerpoint.get(p.getName()).getX();
        double y = playerpoint.get(p.getName()).getY();
        double z = playerpoint.get(p.getName()).getZ();
        float yaw = p.getLocation().getYaw();
        float pitch = p.getLocation().getPitch();
        return new Location(p.getWorld(), x, y, z, yaw, pitch).add(0.5, 0, 0.5);
    }

    String getCheckPoint(Player p, Location loc) {
        if (playerpoint.containsKey(p.getName())) {
            for (String key : Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false)) {
                if (Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".x") == loc.getX() &&
                        Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".y") == loc.getY() &&
                        Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".z") == loc.getZ()) {
                    return key;
                }
            }
        }
        return null;
    }

    String getCheckPoint(Location loc) {
        for (String key : Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false)) {
            if (Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".x") == loc.getX() &&
                    Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".y") == loc.getY() &&
                    Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".z") == loc.getZ()) {
                return key;
            }
        }
        return null;
    }

    boolean hasCheckPoint(Location loc) {
        for (String key : Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false)) {
            if (Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".x") == loc.getX() &&
                    Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".y") == loc.getY() &&
                    Parkour.getLoc().getConfig().getDouble("Spawnpoints." + key + ".z") == loc.getZ()) {
                return true;
            }
        }
        return false;
    }


    public static void guardarItens(Player p) {
        HashMap<Integer, ItemStack> itens = new HashMap<>();
        for (ItemStack itemfodase : p.getInventory().getContents()) {
            p.getInventory().all(itemfodase).entrySet().forEach(entry -> {
                itens.put(entry.getKey(), entry.getValue());
            });
        }
        guardarhashitens.put(p.getName(), itens);
    }

    public static void setItens(Player p) {
        if (guardarhashitens.containsKey(p.getName())) {
            p.getInventory().clear();
            for (Map.Entry<Integer, ItemStack> map : guardarhashitens.get(p.getName()).entrySet()) {
                p.getInventory().setItem(map.getKey(), map.getValue());
            }
        }
        guardarhashitens.remove(p.getName());
    }

    public static void sair(Player p) {
        ParkourEvents.setItens(p);
        ParkourEvents.hashplayer.remove(p.getName());
        ParkourEvents.playerpoint.remove(p.getName());
    }

    void runnable(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tempo++;
                if (hashplayer.containsKey(p.getName())) {
                    if (tempo == Parkour.config.getConfig().getInt("opcoes.tempo-parado")) {
                        sair(p);
                        p.sendMessage(Parkour.config.getConfig().getString("mensagens.afk").replace("&", "§"));
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Parkour.getInstance(), 0, 20);
    }

    public String format(long time) {
        if (time == 0) return "0 segundos";

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long timebonito = time % 1000;

        long days = totalSeconds / 60 / 60 / 24;
        long hours = totalSeconds / 60 / 60 % 24;
        long minutes = totalSeconds / 60 % 60;
        long seconds = totalSeconds % 60;

        StringBuilder stringBuilder = new StringBuilder();

        if (days > 0) stringBuilder.append(days).append(days == 1 ? " dia" : " dias");
        if (hours > 0)
            stringBuilder.append(days > 0 ? (minutes > 0 ? ", " : " e ") : "").append(hours).append(hours == 1 ? " hora" : " horas");
        if (minutes > 0)
            stringBuilder.append(days > 0 || hours > 0 ? (seconds > 0 ? ", " : " e ") : "").append(minutes).append(minutes == 1 ? " minuto" : " minutos");
        if (seconds > 0)
            stringBuilder.append(days > 0 || hours > 0 || minutes > 0 ? " e " : "").append(seconds).append(seconds == 1 ? " segundo" : " segundos");
        if (time > 0)
            stringBuilder.append(days > 0 || hours > 0 || minutes > 0 || seconds > 0 ? " e " : "").append(timebonito).append(timebonito == 1 ? " milissegundo" : " milissegundos");
        return (stringBuilder.length() > 0 ? stringBuilder.toString() : "0 segundos");
    }
}
