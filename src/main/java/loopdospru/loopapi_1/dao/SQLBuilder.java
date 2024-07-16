package loopdospru.loopapi_1.dao;

import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;

public class SQLBuilder {
    private final String tableName;
    private final Map<String, String> columns = new LinkedHashMap<>();
    private final Plugin plugin;

    public SQLBuilder(Plugin plugin, String tableName) {
        this.plugin = plugin;
        this.tableName = tableName;
    }

    public SQLBuilder addColumn(String name, String type) {
        columns.put(name, type);
        return this;
    }

    public void build() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                sql.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(");");
            statement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + tableName + ".db");
    }

    public void save(Map<String, Object> data, String id) {
        try (Connection connection = getConnection()) {
            StringBuilder sql = new StringBuilder("INSERT OR REPLACE INTO ").append(tableName).append(" (");
            StringBuilder values = new StringBuilder("VALUES (");
            for (String column : columns.keySet()) {
                sql.append(column).append(", ");
                values.append("?, ");
            }
            sql.setLength(sql.length() - 2);
            values.setLength(values.length() - 2);
            sql.append(") ").append(values).append(");");

            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                int index = 1;
                for (String column : columns.keySet()) {
                    ps.setObject(index++, data.get(column));
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT id FROM " + tableName)) {
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public String getString(String id, String column) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(column);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getList(String id, String column) {
        String data = getString(id, column);
        if (data != null) {
            return Arrays.asList(data.split(","));
        }
        return Collections.emptyList();
    }

    public double getDouble(String id, String column) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(column);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean getBoolean(String id, String column) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(column);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getInt(String id, String column) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(column);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
