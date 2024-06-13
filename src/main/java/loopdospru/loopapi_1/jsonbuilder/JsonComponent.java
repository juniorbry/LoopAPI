package loopdospru.loopapi_1.jsonbuilder;

import com.google.gson.JsonObject;

public class JsonComponent {
    private final String text;
    private String command;
    private String description;

    public JsonComponent(String text) {
        this.text = text;
    }

    public JsonComponent execute(String command) {
        this.command = command;
        return this;
    }

    public JsonComponent descricao(String description) {
        this.description = description;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("text", text);

        if (command != null) {
            JsonObject clickEvent = new JsonObject();
            clickEvent.addProperty("action", "run_command");
            clickEvent.addProperty("value", command);
            json.add("clickEvent", clickEvent);
        }

        if (description != null) {
            JsonObject hoverEvent = new JsonObject();
            hoverEvent.addProperty("action", "show_text");
            hoverEvent.addProperty("value", description);
            json.add("hoverEvent", hoverEvent);
        }

        return json;
    }
}
