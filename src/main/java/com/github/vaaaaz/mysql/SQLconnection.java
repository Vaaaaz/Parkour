package com.github.vaaaaz.mysql;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLconnection {

    private Connection connection;

    private final String user;
    private final String pass;
    private final String host;
    private final int port;
    private final String db;
    private int query;

    public SQLconnection(String user, String host, String pass, int port, String db) {
        this.user = user;
        this.host = host;
        this.pass = pass;
        this.port = port;
        this.db = db;
        this.query = 0;
        loadDB();
    }

    public void openConnection() {
        try {
            query++;
            if ((connection != null) && (!connection.isClosed())) return;
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false", user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            query--;
            e.printStackTrace();
            System.out.println("Ocorreu um erro ao abrir a conexao com o banco de dados");
            Bukkit.getConsoleSender().sendMessage("§aParkour: §cdevido a problemas com a conexao com o §fBANCO DE DADOS §cnao foi possivel iniciar conexao.");
        }
    }

    public void closeConnection() {
        query--;
        if (query <= 0) {
            try {
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (Exception e) {
                System.out.println("algo de errado nao esta certo! infelizmente nao foi possivel fechar a conexao");
            }
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void loadDB() {
        openConnection();
        criarTabelas();
        closeConnection();
    }

    private void criarTabela(String tabela, String colunas) {
        try {
            if ((connection != null) && (!connection.isClosed())) {
                Statement stm = connection.createStatement();
                stm.executeUpdate("CREATE TABLE IF NOT EXISTS " + tabela + " (" + colunas + ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ocorreu um erro ao criar a tabela " + tabela + ", verifique se o banco de dados esta funcionando");
        }
    }

    public void criarTabelas() {
        criarTabela("parkour", "UUID varchar(36), NOME varchar(16), TEMPO bigint");
    }
}
