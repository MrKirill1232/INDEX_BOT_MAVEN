package com.index.chatAdmin.cases;

import com.google.common.primitives.Ints;
import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

public class muteAction {

    muteHandler mh = new muteHandler();
    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    long user_id;
    String name;
    String re_name;
    String orig_message;

    String val_number;
    long curr_time;
    long time_in_seconds;

    String comment = "";

    public muteAction (Update update){

        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();
        name = update.getMessage().getFrom().getFirstName();
        if (update.getMessage().getReplyToMessage().getSenderChat() == null) {
            user_id = update.getMessage().getReplyToMessage().getFrom().getId();
        } else {
            user_id = update.getMessage().getReplyToMessage().getSenderChat().getId();
        }
        if (update.getMessage().getReplyToMessage().getSenderChat() == null) {
            re_name = update.getMessage().getReplyToMessage().getFrom().getFirstName();
        } else {
            re_name = update.getMessage().getReplyToMessage().getSenderChat().getTitle();
        }

        if (chat_id == im.YummyReChat){
            re_name = "";
            chat_id = im.YummyChannel_CHAT;
            final StringTokenizer st = new StringTokenizer(update.getMessage().getReplyToMessage().getText());
            user_id = Long.parseLong(st.nextToken());
            comment += "\n" + st.nextToken();
            while (st.hasMoreTokens()) {
                re_name += " " + st.nextToken();
            }
            name = new ModeratorChat().getName(update.getMessage().getFrom().getId());
        }
        /*
        unmute handler
         */
        if (orig_message.startsWith("//unmute")){
            if(doUnMute()){
                newmessage = "Успешно снял мут с " + re_name + ";";
            }
            else {
                newmessage = "Ошибка при попытке снять мут с " + re_name + ";";
            }
        }

        /*
        mute handler
         */
        if (orig_message.equals("//mute")) {
            newmessage = "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)";
        }
        else if (orig_message.startsWith("//mute")) {
            val_number = orig_message.substring(7);
            curr_time = System.currentTimeMillis()/1000;
            time_in_seconds = curr_time + Long.parseLong(val_number);
            if(doMute()){
                newmessage = "Администратор "+name+" замутил пользователя "+ re_name + " на " + val_number + " секунд " + ";";
            }
            else {
                newmessage = "Ошибка при попытке замутить пользователя " + re_name + " на секунд " + val_number +";";
            }
        }
        if ( chat_id == im.YummyChannel_CHAT ){
            im.SendAnswer(im.YummyReChat, name, newmessage);
        }
        im.SendAnswer(chat_id, name, newmessage);
    }

    boolean doMute (){
        if (comment.isEmpty()){
            return mh.tryMute(chat_id, user_id, name, re_name, time_in_seconds, true);
        }
        else {
            return mh.tryMute(chat_id, user_id, name, re_name, time_in_seconds, true, comment);
        }
    }

    boolean doUnMute (){
        return mh.tryMute(chat_id, user_id, name, re_name, 0, false);
    }
}
