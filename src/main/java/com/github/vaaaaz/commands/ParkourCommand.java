package com.github.vaaaaz.commands;

import com.github.vaaaaz.events.ParkourEvents;
import com.github.vaaaaz.mysql.SQLutils;
import com.github.vaaaaz.Parkour;
import com.github.vaaaaz.utils.Cords;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ParkourCommand implements CommandExecutor, Cords {
    public static List<Player> playerlist = new ArrayList<>();
    public static List<Player> playerlist2 = new ArrayList<>();
    public static List<Player> spawnpointsstaff = new ArrayList<>();
    public static List<Player> removespawnpoints = new ArrayList<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        SQLutils sql = new SQLutils();
        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("ajuda") || args[0].equalsIgnoreCase("help")) {
            if (p.hasPermission("parkour.admin")) {
                p.sendMessage("");
                p.sendMessage("§8--------------->§f§lPARKOUR§8<---------------");
                p.sendMessage("");
                p.sendMessage("§e/parkour ajuda - §7Lista os comandos.");
                p.sendMessage("§e/parkour top - §7Lista o top 10 jogadores mais rapidos.");
                p.sendMessage("§e/parkour spawnpoints - §7Mostra o número de spawnpoints.");
                p.sendMessage("§e/parkour setar spawnpoint - §7Seta o(s) spawnpoint(s)");
                p.sendMessage("§e/parkour setspawn - §7Seta o spawn do parkour.");
                p.sendMessage("§e/parkour sair - §7Você sai do parkour e volta para o spawn");
                p.sendMessage("§e/parkour setar inicio - §7Seta o spawn do parkour.");
                p.sendMessage("§e/parkour setar fim - §7Seta o final do parkour.");
                p.sendMessage("§e/parkour deletar (player) - §7Deleta um player do banco de dados.");
                p.sendMessage("§e/parkour remover spawnpoint - §7Remove um spawnpoint especifico.");
                p.sendMessage("");
                p.sendMessage("§8--------------->§f§lPARKOUR§8<---------------");
                p.sendMessage("");
                return true;
            }
            p.sendMessage("");
            p.sendMessage("§8--------------->§f§lPARKOUR§8<---------------");
            p.sendMessage("");
            p.sendMessage("§e/parkour ajuda - §7Lista os comandos.");
            p.sendMessage("§e/parkour top - §7Lista o top 10 jogadores mais rapidos.");
            p.sendMessage("§e/parkour spawnpoints - §7Mostra o número de spawnpoints.");
            p.sendMessage("§e/parkour sair - §7Você sai do parkour e volta para o spawn");
            p.sendMessage("");
            p.sendMessage("§8--------------->§f§lPARKOUR§8<---------------");
            p.sendMessage("");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("spawnpoints")) {
                if (Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false).size() == 0) {
                    p.sendMessage("§cNão existe nenhum spawnpoint setado.");
                    return true;
                }
                int i = Parkour.getLoc().getConfig().getConfigurationSection("Spawnpoints").getKeys(false).size();
                p.sendMessage("§aAtualmente, existem " + i + " spawnpoint(s).");
                return true;
            }
            if (args[0].equalsIgnoreCase("setspawn")) {
                if (p.hasPermission("parkour.admin")) {
                    Location loc = p.getLocation();
                    Parkour.getLoc().getConfig().set("Spawn.x", loc.getX());
                    Parkour.getLoc().getConfig().set("Spawn.y", loc.getY());
                    Parkour.getLoc().getConfig().set("Spawn.z", loc.getZ());
                    Parkour.getLoc().getConfig().set("Spawn.yaw", loc.getYaw());
                    Parkour.getLoc().getConfig().set("Spawn.pitch", loc.getPitch());
                    Parkour.getLoc().saveConfig();
                    p.sendMessage("§aVocê setou o spawn aonde os players voltarão do parkour.");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("sair")) {
                if (!ParkourEvents.hashplayer.containsKey(p.getName())) {
                    p.sendMessage("§cVocê não está no parkour!");
                    return true;
                }
                ParkourEvents.sair(p);
                p.sendMessage("§cVocê saiu do parkour!");
                p.teleport(spawn(p));
                return true;
            }

            if (args[0].equalsIgnoreCase("top")) {
                sql.getTop(p);
                return true;
            }
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("deletar") || args[0].equalsIgnoreCase("del")) {
                if (p.hasPermission("parkour.admin")) {
                    if (sql.hasPlayer(args[1])) {
                        sql.delPlayer(args[1]);
                        p.sendMessage("§aVocê deletou o " + args[1] + " do banco de dados");
                        return true;
                    }
                    p.sendMessage("§cEsse jogador não existe em nosso banco de dados");
                    return true;
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("setar") && args[1].equalsIgnoreCase("spawnpoint")) {
                if (p.hasPermission("parkour.admin")) {
                    if (spawnpointsstaff.contains(p)) {
                        spawnpointsstaff.remove(p);
                        p.sendMessage("§cVocê não está mais setando os spawnpoints.");
                        return true;
                    }
                    if (!spawnpointsstaff.isEmpty()) {
                        p.sendMessage("§cJá existe algum staffer setando os spawnpoints do parkour.");
                        return true;
                    }

                    spawnpointsstaff.add(p);
                    p.sendMessage("§aPara setar os spawnpoints, coloque as plates de ferro no chão.");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("setar") && args[1].equalsIgnoreCase("inicio")) {
                if (p.hasPermission("parkour.admin")) {
                    if (!playerlist.isEmpty()) {
                        p.sendMessage("§cJa existe algum staffer setando o inicio do parkour.");
                        return true;
                    }
                    if (playerlist.contains(p)) {
                        p.sendMessage("§cVocê ja esta setando o inicio do parkour.");
                        return true;
                    }
                    playerlist.add(p);
                    p.sendMessage("§aPara setar o inicio do parkour, coloque uma plate de ouro no chão.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("setar") && args[1].equalsIgnoreCase("fim")) {
                if (p.hasPermission("parkour.admin")) {
                    if (!playerlist2.isEmpty()) {
                        p.sendMessage("§cJa existe algum staffer setando o fim do parkour.");
                        return true;
                    }
                    if (playerlist2.contains(p)) {
                        p.sendMessage("§cVocê ja esta setando o fim do parkour.");
                        return true;
                    }
                    playerlist2.add(p);
                    p.sendMessage("§aPara setar o fim do parkour, coloque uma plate de ouro no chão.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("remover") && args[1].equalsIgnoreCase("spawnpoint")) {
                if (p.hasPermission("parkour.admin")) {
                    if (removespawnpoints.contains(p)) {
                        removespawnpoints.remove(p);
                        p.sendMessage("§cVocê não está mais removendo os spawnpoints.");
                        return true;
                    }
                    if (!removespawnpoints.isEmpty()) {
                        p.sendMessage("§cJa existe algum staffer removendo os spawnpoints.");
                        return true;
                    }
                    removespawnpoints.add(p);
                    p.sendMessage("§aPara remover um spawnpoint, quebre a plate de ferro em que ele está.");
                }
            }
            return true;
        }

        return false;
    }


    public String format(long time) {
        if (time == 0) return "0 segundos";

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

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

        return (stringBuilder.length() > 0 ? stringBuilder.toString() : "0 segundos");
    }
}
