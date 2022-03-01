package com.index.commandsHandler.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand {

    IndexMain im = new IndexMain();
    String newmessage;

    public StartCommand (Update update) {
        String name = update.getMessage().getFrom().getFirstName();
        long chat_id = update.getMessage().getChatId();
        newmessage = "Привет\nКак дела ?";
        newmessage = newmessage + "\n" + name;
        im.SendAnswer(chat_id, "MrKirill1232", newmessage);
    }

}
