package com.index.chatModeration.moderators_chat;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ModeratorChat {
    IndexMain im = new IndexMain();

    public ModeratorChat (){
        new ModeratorChat(null, "null");
    }
    public ModeratorChat (Update update, String type){
        if (type.equals("Forwarding")){
            if (false){
                Forwarding(update);
            }
        }
        else if (type.equals("Translate")){
            Translate(update);
        }
        else if (type.equals("DeleteMe"))
        {
            if (true){
                Forwarding(update);
            }
        }
        else {
        }
    }
    private void Forwarding (Update update){
        /* forwarding
        * Пересылается все сообщения с yummy чата в чат модератора
        * */
        if ( update.getMessage().getDate() > (System.currentTimeMillis()/1000)-300 )
        {
            String message;
            if (update.getMessage().getSenderChat() == null) {
                message =
                        "<code>" + update.getMessage().getFrom().getId() + "</code>\n" +
                        "<code>" + update.getMessage().getMessageId().toString() + "</code>";
                if (update.getMessage().getReplyToMessage() != null) {
                    message += "\n<code>" + update.getMessage().getReplyToMessage().getMessageId() + "</code>";
                }
                message += "\n<code>" + update.getMessage().getFrom().getFirstName() + "</code>" ;
            } else {
                message =
                        "<code>" + update.getMessage().getSenderChat().getId() + "</code>\n" +
                        "<code>" + update.getMessage().getMessageId().toString() + "</code>";
                if (update.getMessage().getReplyToMessage() != null) {
                    message += "\n<code>" + update.getMessage().getReplyToMessage().getMessageId() + "</code>";
                }
                message += "\n<code>" + update.getMessage().getSenderChat().getTitle() + "</code>";
            }

            im.SendAnswer(im.YummyReChat, "Index_BOT", message, "HTML");
            if (update.getMessage().getNewChatMembers().stream().findFirst().isPresent()){
                im.SendAnswer(im.YummyReChat, "INDEX_BOT", "Присоединился к чату Yummy Anime");
            }
            else {
                ForwardMessage re = new ForwardMessage();
                re.setMessageId(update.getMessage().getMessageId());
                re.setChatId(String.valueOf(im.YummyReChat));
                re.setFromChatId(String.valueOf(im.YummyChannel_CHAT));
                re.setDisableNotification(true);
                try {
                    im.execute(re);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void Translate (Update update){

        if ( update.getMessage().hasText() && update.getMessage().getText().startsWith("!")){
            return;
        }
        new SendHandler(update, getName(update.getMessage().getFrom().getId()));
    }
    public String getName (Long user_id){
        if (user_id == (499220683)){
            return "To Aru Majutsu No Index";
        }
        /*else if ( user_id == ( )){

        }*/
        else {
            return null;
        }
    }
}
