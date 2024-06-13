package loopdospru.loopapi_1.jsonbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JsonBuilder {
    private final List<JsonComponent> components;
    private final boolean solo;

    private JsonBuilder(JsonMessageType type) {
        this.components = new ArrayList<>();
        this.solo = (type == JsonMessageType.SOLO);
    }

    public static JsonBuilder of(JsonMessageType type) {
        return new JsonBuilder(type);
    }

    public JsonBuilder addMessage(String message) {
        components.add(new JsonComponent(message));
        return this;
    }

    public JsonBuilder execute(String command) {
        if (!components.isEmpty()) {
            components.get(components.size() - 1).execute(command);
        }
        return this;
    }

    public JsonBuilder descricao(String description) {
        if (!components.isEmpty()) {
            components.get(components.size() - 1).descricao(description);
        }
        return this;
    }

    public JsonBuilder nullMessage() {
        components.add(new JsonComponent(""));
        return this;
    }

    public JsonBuilder create() {
        return this;
    }

    public void send(Player player) {
        if (solo) {
            TextComponent textComponent = new TextComponent();
            for (JsonComponent component : components) {
                textComponent.addExtra(convertToTextComponent(component));
            }
            player.spigot().sendMessage(textComponent);
        } else {
            for (JsonComponent component : components) {
                player.spigot().sendMessage(convertToTextComponent(component));
            }
        }
    }

    private TextComponent convertToTextComponent(JsonComponent component) {
        JsonObject json = component.toJson();
        String text = json.get("text").getAsString();
        TextComponent textComponent = new TextComponent(text);

        if (json.has("clickEvent")) {
            JsonObject clickEvent = json.getAsJsonObject("clickEvent");
            String command = clickEvent.get("value").getAsString();
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }

        if (json.has("hoverEvent")) {
            JsonObject hoverEvent = json.getAsJsonObject("hoverEvent");
            String description = hoverEvent.get("value").getAsString();
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(description)}));
        }

        return textComponent;
    }

    public enum JsonMessageType {
        SOLO, MULTI
    }
}
