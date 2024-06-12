package loopdospru.loopapi_1.jsonbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JsonBuilder {
    private final List<JsonComponent> components;
    private final JsonMessageType type;

    private JsonBuilder(JsonMessageType type) {
        this.type = type;
        this.components = new ArrayList<>();
    }

    public static JsonBuilder of(JsonMessageType type) {
        return new JsonBuilder(type);
    }

    public JsonBuilder addMessage(String text) {
        components.add(new JsonComponent(text));
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
        // Just to align with the example, no action needed here
        return this;
    }

    public JsonBuilder create() {
        return this;
    }

    public void send(Player player) {
        JsonArray jsonArray = new JsonArray();
        for (JsonComponent component : components) {
            jsonArray.add(component.toJson());
        }

        JsonObject jsonMessage = new JsonObject();
        jsonMessage.add("text", jsonArray);

        String jsonString = jsonMessage.toString();

        player.spigot().sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(jsonString));
    }
}
