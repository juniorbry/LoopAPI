package loopdospru.loopapi_1.questionapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuestionBuilder {
    private static final Map<Player, Question> activeQuestions = new HashMap<>();
    private static JavaPlugin plugin;

    private final QuestionType type;
    private String title;
    private int limit = -1;
    private Consumer<QuestionEvent> callback;

    private QuestionBuilder(QuestionType type) {
        this.type = type;
    }

    public static void enable(JavaPlugin plugin) {
        QuestionBuilder.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new QuestionListener(), plugin);
    }

    public static QuestionBuilder of(QuestionType type) {
        return new QuestionBuilder(type);
    }

    public QuestionBuilder title(String title) {
        this.title = title;
        return this;
    }

    public QuestionBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QuestionBuilder callback(Consumer<QuestionEvent> callback) {
        this.callback = callback;
        return this;
    }

    public Question create() {
        return new Question(type, title, limit, callback);
    }

    public void send(Player player) {
        create().send(player);
    }

    public static Question getActiveQuestion(Player player) {
        return activeQuestions.get(player);
    }

    public static void addActiveQuestion(Player player, Question question) {
        activeQuestions.put(player, question);
    }

    public static void removeActiveQuestion(Player player) {
        activeQuestions.remove(player);
    }
}