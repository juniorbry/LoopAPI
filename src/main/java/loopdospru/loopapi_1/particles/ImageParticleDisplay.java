package loopdospru.loopapi_1.particles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImageParticleDisplay extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void readImage(String url, int size, int displayTime, List<Player> players, Location location, ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
        BufferedImage image = null;

        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (image == null) {
            return;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // Resize the image if necessary
        if (size > 0) {
            image = resizeImage(image, size, size);
            width = image.getWidth();
            height = image.getHeight();
        }

        int finalHeight = height;
        int finalWidth = width;
        BufferedImage finalImage = image;
        new BukkitRunnable() {
            int timeLeft = displayTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    this.cancel();
                    return;
                }

                for (int y = 0; y < finalHeight; y++) {
                    for (int x = 0; x < finalWidth; x++) {
                        int rgb = finalImage.getRGB(x, y);
                        if ((rgb >> 24) == 0x00) {
                            continue; // Skip transparent pixels
                        }

                        Color color = new Color(rgb, true);
                        spawnParticle(location.clone().add(x * 0.1, -y * 0.1, 0), color, players);
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    private static void spawnParticle(Location location, Color color, List<Player> players) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getEnumModifier(EnumWrappers.Particle.class, 0).write(0, EnumWrappers.Particle.REDSTONE);
        packet.getBooleans().write(0, true); // Long distance
        packet.getFloat().write(0, (float) location.getX());
        packet.getFloat().write(1, (float) location.getY());
        packet.getFloat().write(2, (float) location.getZ());
        packet.getFloat().write(3, color.getRed() / 255.0f);
        packet.getFloat().write(4, color.getGreen() / 255.0f);
        packet.getFloat().write(5, color.getBlue() / 255.0f);
        packet.getFloat().write(6, 1.0f); // Particle size
        packet.getIntegers().write(0, 0); // Particle count

        players.forEach(player -> {
            try {
                protocolManager.sendServerPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
