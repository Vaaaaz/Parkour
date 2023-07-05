package com.github.vaaaaz.Mysql;

import com.github.vaaaaz.Parkour;
import com.github.vaaaaz.Utils.InventoryAPI;
import com.github.vaaaaz.Utils.Skull;
import com.github.vaaaaz.Utils.MillisecondConverter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.vaaaaz.Utils.Itens.criarItem;

public class SQLutils {
    private static SQLconnection connection;

    public SQLutils() {
        super();
        connection = Parkour.getSqLconnection();
    }

    public String getValue(String tabela, String coluna, String valor, int columnLabel) {
        String value = null;
        try {
            connection.openConnection();
            PreparedStatement ps;
            ResultSet rs;
            Statement st = connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM `" + tabela + "` WHERE " + coluna + " = '" + valor + "'");
            if (rs.next()) {
                String getgrupo = rs.getString(columnLabel);
                value = getgrupo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return value;
    }


    public void setPlayer(Player p) {
        try {
            connection.openConnection();
            PreparedStatement ps;
            ResultSet rs;
            Statement st = connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM `parkour` where NOME = '" + p.getName() + "'");
            if (rs.next()) {
                return;
            } else {
                ps = connection.getConnection().prepareStatement("INSERT INTO parkour (UUID,NOME,TEMPO)VALUES(?,?,?)");

                ps.setString(1, "" + p.getUniqueId());
                ps.setString(2, p.getName());
                ps.setLong(3, 0);

                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e + "§cOcorreu um erro ao entrar em contato com o banco de dados");
        }
    }

    public void delPlayer(String p) {
        try {
            connection.openConnection();
            ResultSet rs;
            Statement st = connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM `parkour` where NOME = '" + p + "'");
            if (rs.next()) {
                PreparedStatement ps = connection.getConnection().prepareStatement("DELETE FROM parkour WHERE NOME = '" + p + "'");
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e + "§cOcorreu um erro ao entrar em contato com o banco de dados");
        }
    }

    public void setTime(String nomedoPlayer, long time) {
        try {
            connection.openConnection();
            PreparedStatement ps;
            ResultSet rs;
            Statement st = connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM `parkour` WHERE NOME = '" + nomedoPlayer + "'");
            if (rs.next()) {
                ps = connection.getConnection().prepareStatement("UPDATE parkour SET TEMPO = '" + time + "' WHERE NOME = '" + nomedoPlayer + "'");
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
    }
//                        jogadores.add("§f"+i+"º "+resultSet.getString("NOME")+"-§7 "+ format(resultSet.getLong(3)));

    public void getTop(Player p) {
        List<ItemStack> itens = new ArrayList<>();
        if(jogadores() == 0){
            p.sendMessage("§cNenhum jogador terminou o parkour ainda.");
            return;
        }
        InventoryAPI inv = new InventoryAPI(5, "§ATOP JOGADORES MAIS RÁPIDOS", new int[]{19, 20, 21, 22, 23, 24, 25, 29, 30, 31, 32, 33});
        ItemStack voltar = new Skull().getSkull(
                "http://textures.minecraft.net/texture/37aee9a75bf0df7897183015cca0b2a7d755c63388ff01752d5f4419fc645", "§cVoltar", null);
        ItemStack proximo = new Skull().getSkull(
                "http://textures.minecraft.net/texture/682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e", "§aPróxima página", null);

        ItemStack cabeca = null;
        try {
            connection.openConnection();
            ResultSet resultSet = connection.getConnection().prepareStatement("SELECT * FROM `parkour` ORDER BY `TEMPO`").executeQuery();
            int i = 0;

            while (resultSet.next()) {
                MillisecondConverter milli = new MillisecondConverter(resultSet.getLong(3));
                if (resultSet.getLong(3) > 0) {
                    i++;
                    cabeca = new Skull().getSkullPlayer(
                            resultSet.getString(2),
                            "§a" + i + "° " + resultSet.getString(2), Arrays.asList("","§aVelocidade do jogador: ", "§e"+format(resultSet.getLong(3))));
                    itens.add(cabeca);
                }
                inv.setPageItens(itens);
                inv.setGuiItem(3, voltar);
                inv.setGuiItem(4, criarItem(Material.BARRIER, "§cFechar inventario", null));
                inv.setGuiItem(5, proximo);
                inv.update();
                Parkour.getInstance().hashinv.put(p.getName(), inv);
            }
            p.openInventory(inv.getInventory());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
    }

    public int jogadores(){
        try{
            connection.openConnection();
            ResultSet rs = connection.getConnection().prepareStatement("SELECT count(NOME) FROM parkour").executeQuery();
            if(rs.next()){
                return Integer.parseInt(rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
        return 0;
    }

    public int countResults(String tabela, String coluna){
        try {
            connection.openConnection();
            ResultSet rs = connection.getConnection().prepareStatement("SELECT count("+coluna+") FROM "+tabela).executeQuery();
            if(rs.next()){
                return Integer.parseInt(rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
        return 0;
    }

    public long getTime(String nome) {
        return Long.parseLong(getValue("parkour", "NOME", nome, 3));
    }

    public boolean hasPlayer(String nomedoPlayer) {
        return getValue("parkour", "NOME", nomedoPlayer, 2) != null;
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
