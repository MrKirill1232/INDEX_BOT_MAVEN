package com.index.chatAdmin.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.objects.Update;

public class getEntitiesAction {
    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    String name;
    String orig_message;

    public getEntitiesAction(Update update) {
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();


        if ( update.getMessage().getReplyToMessage() == null ){
            newmessage = "Чтобы увидеть ентити, отправьте команду ответив на интересующее Вас сообщение;";
        }
        else if (orig_message.equals("//getEntities")){
            //newmessage = String.valueOf(Objects.requireNonNull(update.getMessage().getReplyToMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null)).getFileSize());
            newmessage = String.valueOf(update.getMessage().getReplyToMessage().getEntities());
        }
        else {
            newmessage = "Неправильно составлена комманда! Отправьте команду ответив на интересующее Вас сообщение;";

        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
