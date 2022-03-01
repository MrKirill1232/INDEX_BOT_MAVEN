package com.index.chatAdmin.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class pinAction {

    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    String name;

    public pinAction(Update update){
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();

        if ((update.getMessage().getReplyToMessage() == null)){
            newmessage = "Для того, чтобы закрепить сообщение, отправьте комманду, ответив на одно из сообщений;";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }

        PinChatMessage pin = new PinChatMessage();
        pin.setChatId(String.valueOf(chat_id));
        pin.setMessageId(update.getMessage().getReplyToMessage().getMessageId());
        pin.setDisableNotification(true);
        try {
            im.execute(pin);
            newmessage = "Сообщение закреплено;";
        } catch (TelegramApiException e) {
            newmessage = "Сообщение не закреплено;";
            e.printStackTrace();
        }
        if ( chat_id == im.YummyChannel_CHAT ){
            im.SendAnswer(im.YummyReChat, name, update.getMessage().getFrom() + "\n" + update.getMessage().getReplyToMessage().getMessageId() + "\n" + newmessage);
        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
