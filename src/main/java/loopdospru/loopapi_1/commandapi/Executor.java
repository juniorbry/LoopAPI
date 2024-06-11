package loopdospru.loopapi_1.commandapi;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Executor {
    private final CommandSender sender;

    public Executor(CommandSender sender) {
        this.sender = sender;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        if (isPlayer()) {
            return (Player) sender;
        }
        return null;
    }
    public void sendArrayMessage(String... mensagens) {
        for (String mensagem : mensagens) {
            sender.sendMessage(mensagem);
        }
    }
    public boolean isSneaking() {
        if (isPlayer()) {
            return getPlayer().isSneaking();
        }
        return false;
    }

    public ItemStack getHandItem() {
        if (isPlayer()) {
            return getPlayer().getInventory().getItemInHand();
        }
        return new ItemStack(Material.AIR);
    }

    public ViewerStatus getLookingDirection() {
        if (isPlayer()) {
            float yaw = getPlayer().getLocation().getYaw();
            if (yaw < 0) {
                yaw += 360;
            }
            if (yaw >= 315 || yaw < 45) {
                return ViewerStatus.NORTH;
            } else if (yaw < 135) {
                return ViewerStatus.EAST;
            } else if (yaw < 225) {
                return ViewerStatus.SOUTH;
            } else {
                return ViewerStatus.WEST;
            }
        }
        return ViewerStatus.NONE;
    }

    public CommandSender getSender() {
        return sender;
    }
}
