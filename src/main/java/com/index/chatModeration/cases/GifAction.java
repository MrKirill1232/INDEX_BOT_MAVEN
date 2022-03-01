package com.index.chatModeration.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GifAction {

    IndexMain im = new IndexMain();
    String newmessage;

    Long chat_id;
    String name;

    public GifAction (Update update) {
        chat_id = update.getMessage().getChatId();
        name = update.getMessage().getFrom().getFirstName();
        if (true){
            GIFdelete(update);
        }
    }

    void GIFdelete(Update update){

        //newmessage = "Функция отправки GIF была ограничена;";
        //im.SendAnswer(chat_id, name, newmessage,false, 0);
        im.deleteMessage(chat_id, update.getMessage().getMessageId());
    }
}
