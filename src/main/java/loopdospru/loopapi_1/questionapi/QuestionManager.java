package loopdospru.loopapi_1.questionapi;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class QuestionManager {
    private static final Map<Player, Question> activeQuestions = new HashMap<>();

    public static void addPlayerQuestion(Player player, Question question) {
        activeQuestions.put(player, question);
    }

    public static Question getPlayerQuestion(Player player) {
        return activeQuestions.get(player);
    }

    public static void removePlayerQuestion(Player player) {
        activeQuestions.remove(player);
    }
}