package loopdospru.loopapi_1.configapibuilder;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigBuilder {

    private static final Map<String, ConfigBuilder> configs = new HashMap<>();
    private FileConfiguration config;
    private final File configFile;
    private final Plugin plugin;

    private ConfigBuilder(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static ConfigBuilder create(Plugin plugin, String fileName) {
        if (!configs.containsKey(fileName)) {
            configs.put(fileName, new ConfigBuilder(plugin, fileName));
        }
        return configs.get(fileName);
    }

    public ConfigBuilder addString(String path, String value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return this;
    }

    public ConfigBuilder addInt(String path, int value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return this;
    }

    public ConfigBuilder addDouble(String path, double value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return this;
    }

    public ConfigBuilder addBoolean(String path, boolean value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return this;
    }

    public ConfigBuilder addList(String path, List<?> value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return this;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public void load() {
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                config.save(configFile);
            } else {
                config.load(configFile);
            }
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            plugin.getLogger().severe("Não foi possível localizar os arquivos de: " + configFile.getName());
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar dados do: " + configFile.getName());
            e.printStackTrace();
        }
    }

    public static ConfigBuilder getConfig(String fileName) {
        return configs.get(fileName);
    }
}
