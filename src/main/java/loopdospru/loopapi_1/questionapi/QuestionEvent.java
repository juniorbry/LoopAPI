package loopdospru.loopapi_1.questionapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final QuestionType type;
    private boolean cancelled;
    private String answer;

    public QuestionEvent(Player player, QuestionType type, String answer) {
        this.player = player;
        this.type = type;
        this.answer = answer;
    }

    public Player getPlayer() {
        return player;
    }

    public QuestionType getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}