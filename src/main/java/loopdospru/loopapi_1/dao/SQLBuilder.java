package loopdospru.loopapi_1.dao;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;

public class SQLBuilder {
    private static final HashMap<String, SQLBuilder> sqls = new HashMap<>();
    private final Plugin plugin;
    private final String dbName;
    private Connection connection;
    private final Map<String, String> columns = new LinkedHashMap<>();

    public SQLBuilder(Plugin plugin, String dbName) {
        this.plugin = plugin;
        this.dbName = dbName;
    }

    public SQLBuilder addColumn(String columnName, String dataType) {
        columns.put(columnName, dataType);
        return this;
    }

    public void build() {
        try {
            connect();
            createTable();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao criar ou inserir nas tabelas.");
            e.printStackTrace();
        }
    }

    public void save(Map<String, Object> data, String id) {
        try {
            StringBuilder insertQuery = new StringBuilder("REPLACE INTO ").append(dbName).append(" (");
            StringBuilder valuesQuery = new StringBuilder(" VALUES (");

            for (String column : columns.keySet()) {
                insertQuery.append(column).append(", ");
                valuesQuery.append("?, ");
            }

            insertQuery.setLength(insertQuery.length() - 2);  // Remove última vírgula
            valuesQuery.setLength(valuesQuery.length() - 2);  // Remove última vírgula

            insertQuery.append(")").append(valuesQuery).append(")");

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery.toString())) {
                int index = 1;
                for (String column : columns.keySet()) {
                    preparedStatement.setObject(index++, convertValueToString(data.get(column)));
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao salvar dados na tabela.");
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + dbName + ".db";
            connection = DriverManager.getConnection(url);
            sqls.put(dbName, this);
            plugin.getLogger().info("Conectado ao banco de dados: " + dbName);
        }
    }

    private void createTable() throws SQLException {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(dbName).append(" (");
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            createTableQuery.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
        }
        createTableQuery.setLength(createTableQuery.length() - 2);  // Remove última vírgula
        createTableQuery.append(", PRIMARY KEY(id))"); // Adiciona PRIMARY KEY
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery.toString());
        }
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        String query = "SELECT id FROM " + dbName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao obter IDs.");
            e.printStackTrace();
        }
        return ids;
    }

    public String getString(String id, String columnName) {
        return (String) get(id, columnName);
    }

    public int getInt(String id, String columnName) {
        return (int) get(id, columnName);
    }

    public double getDouble(String id, String columnName) {
        return (double) get(id, columnName);
    }

    public boolean getBoolean(String id, String columnName) {
        return (boolean) get(id, columnName);
    }

    public List<String> getList(String id, String columnName) {
        return Arrays.asList(((String) get(id, columnName)).split(","));
    }

    public Object get(String id, String columnName) {
        String query = "SELECT " + columnName + " FROM " + dbName + " WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getObject(columnName);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao obter dados da tabela.");
            e.printStackTrace();
        }
        return null;
    }

    private String convertValueToString(Object value) {
        if (value instanceof Location) {
            Location location = (Location) value;
            return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            List<String> stringList = new ArrayList<>();
            for (Object item : list) {
                stringList.add(String.valueOf(item));
            }
            return String.join(",", stringList);
        } else {
            return String.valueOf(value);
        }
    }

    public static void closeConnections() {
        for (SQLBuilder sql : sqls.values()) {
            try {
                if (sql.connection != null && !sql.connection.isClosed()) {
                    sql.connection.close();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Erro ao fechar a conexão com o banco de dados.");
                e.printStackTrace();
            }
        }
    }
}
