package loopdospru.loopapi_1;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemStackSerializer {

    /**
     * Serializa um ItemStack em uma String Base64.
     *
     * @param item o ItemStack a ser serializado.
     * @return a string Base64 representando o ItemStack.
     * @throws IllegalStateException se ocorrer um erro durante a serialização.
     */
    public static String serialize(ItemStack item) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

            bukkitObjectOutputStream.writeObject(item);
            bukkitObjectOutputStream.close();

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível serializar o ItemStack.", e);
        }
    }

    /**
     * Desserializa uma String Base64 em um ItemStack.
     *
     * @param base64 a string Base64 a ser desserializada.
     * @return o ItemStack resultante.
     * @throws IllegalStateException se ocorrer um erro durante a desserialização.
     */
    public static ItemStack deserialize(String base64) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            ItemStack item = (ItemStack) bukkitObjectInputStream.readObject();
            bukkitObjectInputStream.close();

            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Não foi possível desserializar o ItemStack.", e);
        }
    }
}

