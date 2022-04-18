package com.index.chatAdmin.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.objects.Update;

public class getFileIDAction {
    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    String name;
    String orig_message;

    public getFileIDAction(Update update) {
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        if ( update.getMessage().getReplyToMessage() == null ){
            newmessage = "Чтобы узнать ID, отправьте комманду ответив на интересующее Вас сообщение;";
            im.SendAnswer(chat_id, name, newmessage);
        }
        else if (orig_message.equals("//getFileID")){
            newmessage = update.getMessage().getReplyToMessage().getAnimation().getFileUniqueId();
            im.SendAnswer(chat_id, name, newmessage);
        }
        else {
            newmessage = "Неправильно составлена комманда! Отправьте комманду ответив на интересующее Вас сообщение;";
            im.SendAnswer(chat_id, name, newmessage);
        }
    }
}

