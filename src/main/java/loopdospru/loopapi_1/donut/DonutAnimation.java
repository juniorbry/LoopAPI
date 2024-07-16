package loopdospru.loopapi_1.donut;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DonutAnimation extends JavaPlugin {

    private ProtocolManager protocolManager;

    public static void startDonutAnimation(Player player, ProtocolManager protocolManager, Plugin plugin) {
        Location center = player.getLocation();
        int durationSeconds = 10;
        final double[] A = {1};
        final double[] B = { 1 };

        new BukkitRunnable() {
            int timeLeft = durationSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    this.cancel();
                    return;
                }

                A[0] += 0.07;
                B[0] += 0.03;
                double cA = Math.cos(A[0]), sA = Math.sin(A[0]);
                double cB = Math.cos(B[0]), sB = Math.sin(B[0]);

                for (double j = 0; j < 6.28; j += 0.07) { // j <=> theta
                    double ct = Math.cos(j), st = Math.sin(j);
                    for (double i = 0; i < 6.28; i += 0.02) { // i <=> phi
                        double sp = Math.sin(i), cp = Math.cos(i);
                        double h = ct + 2;
                        double D = 1 / (sp * h * sA + st * cA + 5);
                        double t = sp * h * cA - st * sA;
                        int x = (int) (center.getX() + 30 * D * (cp * h * cB - t * sB));
                        int y = (int) (center.getY() + 15 * D * (cp * h * sB + t * cB));
                        int z = (int) (center.getZ());

                        if (x < 0 || x > 79 || y < 0 || y > 22) continue;

                        float red = 0.0f;
                        float green = 0.0f;
                        float blue = 1.0f;
                        float size = 1.0f;

                        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
                        packet.getEnumModifier(EnumWrappers.Particle.class, 0).write(0, EnumWrappers.Particle.REDSTONE);
                        packet.getBooleans().write(0, true);
                        packet.getFloat().write(0, (float) x + 0.5f);
                        packet.getFloat().write(1, (float) y + 1.0f);
                        packet.getFloat().write(2, (float) z + 0.5f);
                        packet.getFloat().write(3, red);
                        packet.getFloat().write(4, green);
                        packet.getFloat().write(5, blue);
                        packet.getFloat().write(6, size);
                        packet.getIntegers().write(0, 0);

                        try {
                            protocolManager.sendServerPacket(player, packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
