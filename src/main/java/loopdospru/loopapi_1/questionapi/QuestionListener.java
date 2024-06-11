package loopdospru.loopapi_1.questionapi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class QuestionListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        Question question = QuestionBuilder.getActiveQuestion(player);
        if (question == null) {
            return;
        }

        if (message.equalsIgnoreCase("cancelar")) {
            QuestionBuilder.removeActiveQuestion(player);
            player.sendMessage("Operação cancelada.");
            event.setCancelled(true);
            return;
        }

        QuestionType type = question.getType();
        if (type == QuestionType.YESORNO) {
            if (message.equalsIgnoreCase("sim")) {
                event.setCancelled(true);
                question.getCallback().accept(new QuestionEvent(player, type, "YES"));
                QuestionBuilder.removeActiveQuestion(player);
            } else if (message.equalsIgnoreCase("não")) {
                event.setCancelled(true);
                question.getCallback().accept(new QuestionEvent(player, type, "NO"));
                QuestionBuilder.removeActiveQuestion(player);
            }
        } else if (type == QuestionType.STRING) {
            if (question.getLimit() > 0 && message.length() > question.getLimit()) {
                player.sendMessage("Mensagem muito longa. Limite de " + question.getLimit() + " caracteres.");
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            question.getCallback().accept(new QuestionEvent(player, type, message));
            QuestionBuilder.removeActiveQuestion(player);
        } else if (type == QuestionType.INTEGER) {
            try {
                int value = Integer.parseInt(message);
                if (question.getLimit() > 0 && value > question.getLimit()) {
                    player.sendMessage("Valor muito alto. Limite de " + question.getLimit() + ".");
                    event.setCancelled(true);
                    return;
                }
                event.setCancelled(true);
                question.getCallback().accept(new QuestionEvent(player, type, message));
                QuestionBuilder.removeActiveQuestion(player);
            } catch (NumberFormatException e) {
                player.sendMessage("Por favor, insira um número inteiro válido.");
                event.setCancelled(true);
            }
        } else if (type == QuestionType.DOUBLE) {
            try {
                double value = Double.parseDouble(message);
                if (question.getLimit() > 0 && value > question.getLimit()) {
                    player.sendMessage("Valor muito alto. Limite de " + question.getLimit() + ".");
                    event.setCancelled(true);
                    return;
                }
                event.setCancelled(true);
                question.getCallback().accept(new QuestionEvent(player, type, message));
                QuestionBuilder.removeActiveQuestion(player);
            } catch (NumberFormatException e) {
                player.sendMessage("Por favor, insira um número válido.");
                event.setCancelled(true);
            }
        }
    }
}