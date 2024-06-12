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
            StringBuilder insertQuery = new StringBuilder("REPLACE INTO terrenos (");
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
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS terrenos (");
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            createTableQuery.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
        }
        createTableQuery.setLength(createTableQuery.length() - 2);  // Remove última vírgula
        createTableQuery.append(")");

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery.toString());
        }
    }

    private String convertValueToString(Object value) {
        if (value instanceof List) {
            return String.join(",", (List<String>) value);
        } else if (value instanceof Location) {
            return serializeLocation((Location) value);
        } else {
            return value.toString();
        }
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        String query = "SELECT id FROM terrenos";
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

    public String getString(String columnName, String id) {
        return getValue(columnName, id, String.class);
    }

    public int getInt(String columnName, String id) {
        return getValue(columnName, id, Integer.class);
    }

    public double getDouble(String columnName, String id) {
        return getValue(columnName, id, Double.class);
    }

    public List<String> getList(String columnName, String id) {
        String csv = getValue(columnName, id, String.class);
        return csv != null ? Arrays.asList(csv.split(",")) : new ArrayList<String>();
    }

    public Location getLocation(String columnName, String id) {
        String locString = getValue(columnName, id, String.class);
        return locString != null ? deserializeLocation(locString) : null;
    }

    private <T> T getValue(String columnName, String id, Class<T> type) {
        String query = "SELECT " + columnName + " FROM terrenos WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Object value = resultSet.getObject(columnName);
                if (type.isInstance(value)) {
                    return type.cast(value);
                } else if (type == Integer.class) {
                    if (value instanceof String) {
                        if (((String) value).equalsIgnoreCase("true")) {
                            return type.cast(1);
                        } else if (((String) value).equalsIgnoreCase("false")) {
                            return type.cast(0);
                        } else {
                            return type.cast(Integer.parseInt((String) value));
                        }
                    }
                } else if (type == Boolean.class) {
                    if (value instanceof String) {
                        return type.cast(Boolean.parseBoolean((String) value));
                    } else if (value instanceof Integer) {
                        return type.cast((Integer) value != 0);
                    }
                }
            }
        } catch (SQLException | ClassCastException | NumberFormatException e) {
            plugin.getLogger().severe("Erro ao obter valor da coluna: " + columnName);
            e.printStackTrace();
        }
        return null;
    }

    public boolean getBoolean(String columnName, String id) {
        Boolean value = getValue(columnName, id, Boolean.class);
        return value != null && value;
    }

    public String serializeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("O mundo da localização é nulo.");
        }
        return world.getName() + "===" + location.getX() + "===" + location.getY() + "===" + location.getZ() + "===" + location.getYaw() + "===" + location.getPitch();
    }

    private Location deserializeLocation(String locString) {
        String[] parts = locString.split("===");
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            throw new NullPointerException("O mundo da localização não foi encontrado: " + parts[0]);
        }
        return new Location(
                world,
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }

    public Connection getConnection() {
        return connection;
    }

    public static SQLBuilder getSQL(String dbName) {
        return sqls.get(dbName);
    }
}
