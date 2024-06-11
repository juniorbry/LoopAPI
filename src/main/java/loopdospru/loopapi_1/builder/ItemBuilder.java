package loopdospru.loopapi_1.builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack i) {
        this.itemStack = i;
        this.itemMeta = i.getItemMeta();
    }
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }
    public ItemBuilder lore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }
    public ItemBuilder lore(String... lore) {
        List<String> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        itemMeta.setLore(loreList);
        return this;
    }

    public ItemBuilder lore(String lore) {
        return lore(new String[]{lore});
    }
    public ItemBuilder data(int data) {
        itemStack.setDurability((short) data);
        return this;
    }
    public ItemBuilder glow(boolean status) {
        if (status == true) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }
    public ItemBuilder glow() {
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder hideEnchants() {
        itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }
    public ItemBuilder setOwner(String nome) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwner(nome);
        return this;
    }
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
