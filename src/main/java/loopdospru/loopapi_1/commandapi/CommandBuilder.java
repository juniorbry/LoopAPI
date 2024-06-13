package loopdospru.loopapi_1.commandapi;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CommandBuilder implements CommandExecutor {
    private static final Map<String, CommandBuilder> commands = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static JavaPlugin plugin;

    private final String command;
    private final String permission;
    private final int interval;

    protected CommandBuilder(String command, String permission, int interval) {
        this.command = command;
        this.permission = permission;
        this.interval = interval;
    //linha 29    registerCommand();
    }

    private void registerCommand() {
        PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            commands.put(command, this);
        } else {
            throw new IllegalArgumentException("Comando " + command + " não está definido no plugin.yml");
        }
    }

    public static void enable(JavaPlugin plugin) {
        CommandBuilder.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando só pode ser executado por um jogador.");
            return true;
        }

        Player player = (Player) sender;

        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
            return true;
        }

        if (interval > 0) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId)) {
                long lastUsed = cooldowns.get(playerId);
                if ((currentTime - lastUsed) < interval * 1000L) {
                    player.sendMessage(ChatColor.RED + "Você deve esperar " + interval + " segundos entre usos deste comando.");
                    return true;
                }
            }
            cooldowns.put(playerId, currentTime);
        }

        CommandEditor editor = new CommandEditor(args);
        return configureCommand(new CommandExecutorWrapper(player), editor);
    }

    protected abstract boolean configureCommand(CommandExecutorWrapper executor, CommandEditor editor);

    public static class CommandExecutorWrapper {
        private final Player player;

        public CommandExecutorWrapper(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        public boolean isSneaking() {
            return player.isSneaking();
        }

        public ItemStack getHandItem() {
            return player.getInventory().getItemInHand();
        }
    }

    public static class CommandEditor {
        private final String[] args;

        public CommandEditor(String[] args) {
            this.args = args;
        }

        public String getString(int index) {
            return args.length > index ? args[index] : "";
        }

        public int getInt(int index) {
            try {
                return Integer.parseInt(getString(index));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public double getDouble(int index) {
            try {
                return Double.parseDouble(getString(index));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public StringBuilder generateMessage(int startIndex) {
            StringBuilder message = new StringBuilder();
            for (int i = startIndex; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            return new StringBuilder(message.toString().trim());
        }

        public void sendArrayMessage(CommandSender sender, String... mensagens) {
            for (String mensagem : mensagens) {
                sender.sendMessage(mensagem);
            }
        }
    }
}
