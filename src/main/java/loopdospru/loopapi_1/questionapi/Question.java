package loopdospru.loopapi_1.questionapi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class Question {
    private final QuestionType type;
    private final String title;
    private final int limit;
    private final Consumer<QuestionEvent> callback;

    public Question(QuestionType type, String title, int limit, Consumer<QuestionEvent> callback) {
        this.type = type;
        this.title = title;
        this.limit = limit;
        this.callback = callback;
    }

    public QuestionType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getLimit() {
        return limit;
    }

    public Consumer<QuestionEvent> getCallback() {
        return callback;
    }
    public void send(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + title);
        player.sendMessage(ChatColor.GRAY + "Caso queira cancelar, digite " + ChatColor.RED + "cancelar");
        player.sendMessage("");
        QuestionBuilder.addActiveQuestion(player, this);
    }

}