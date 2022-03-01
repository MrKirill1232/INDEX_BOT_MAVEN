package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.chatModeration.ChatModerationHandler;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;

public class delayAction {
    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    String name;
    String orig_message;
    Chat hehe = new Chat();

    public delayAction (Update update){
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        if (true){
            newmessage = "API не поддерживает установку медленного режима, вообщем бесполезная комманда;";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }
        if (orig_message.equals("//delay"))
        {
            newmessage = "Не указано значение новой задержки. Воспользуйтесь \"/delay *время*\";";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }
        String val_number = orig_message.substring(8);
        if (Integer.parseInt(val_number) < 0)
        {
            newmessage = "Время новой задержки меньше 0. Воспользуйтесь \"/delay *время*\";";
        }
        else {
            if ( update.getMessage().getChat() != null && !update.getMessage().getChat().isUserChat() )
            {
                hehe.setId(chat_id);
                hehe.setSlowModeDelay(Integer.parseInt(val_number));
                newmessage = "Время сейчас " + hehe.getSlowModeDelay() + " секунд;";
            }
            else if ( update.getMessage().getChat() == null )
            {
                newmessage = "Чат не поддерживает установку времени задержки отправки сообщений;";
            }
            else
            {
                newmessage = "Чат не поддерживает установку времени задержки отправки сообщений;";
            }
        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
