package loopdospru.loopapi_1.particles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImageParticleDisplay {

    public static void readImage(String url, int size, int displayTime, List<Player> players, Location location, Plugin plugin) {
        if (url == null || url.isEmpty()) {
            System.out.println("URL da imagem é inválido.");
            return;
        }

        BufferedImage image = null;
        try {
            URL imageUrl = new URL(url);
            image = ImageIO.read(imageUrl);

            if (image == null) {
                System.out.println("URL não leva para uma imagem válida.");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        int finalHeight = height;
        BufferedImage finalImage = image;
        int finalWidth = width;
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

                        // Extrair componentes de cor do RGB
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Criar instância de Color com base nos componentes RGB
                        org.bukkit.Color color = org.bukkit.Color.fromRGB(red, green, blue);

                        spawnParticle(location.clone().add(x * 0.1, -y * 0.1, 0), color, players, protocolManager);
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = resizedImage.createGraphics();
        g2.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();
        return resizedImage;
    }

    private static void spawnParticle(Location location, org.bukkit.Color color, List<Player> players, ProtocolManager protocolManager) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getEnumModifier(EnumWrappers.Particle.class, 0).write(0, EnumWrappers.Particle.REDSTONE);
        packet.getBooleans().write(0, true); // Long distance
        packet.getFloat().write(0, (float) location.getX());
        packet.getFloat().write(1, (float) location.getY());
        packet.getFloat().write(2, (float) location.getZ());
        packet.getFloat().write(3, (float) color.getRed() / 255); // Red
        packet.getFloat().write(4, (float) color.getGreen() / 255); // Green
        packet.getFloat().write(5, (float) color.getBlue() / 255); // Blue
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