package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.data.sql.stickerInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

public class removeIgnoringStikersAction {

    IndexMain im = new IndexMain();
    stickerInfoHolder instance = stickerInfoHolder.getInstance();
    String newmessage = "";

    long chat_id;
    String name;
    String orig_message;
    String sticker_url;

    public removeIgnoringStikersAction (Update update){

        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        final StringTokenizer st = new StringTokenizer(orig_message);
        st.nextToken();
        if (       ( update.getMessage().getReplyToMessage() == null && ( !st.hasMoreTokens() ))
                || ( (update.getMessage().getReplyToMessage() != null && !update.getMessage().getReplyToMessage().hasSticker()) && ( !st.hasMoreTokens() ) )  ) {
            newmessage = "Для удаления стикер-пака из списка исключений, отправьте комманду, " +
                    "ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }
        else {
            if ( update.getMessage().getReplyToMessage() == null && st.hasMoreTokens() ) {
                sticker_url = st.nextToken();
                if ( sticker_url == null ){
                    newmessage = "Для удаления стикер-пака из списка исключений, отправьте комманду,\n" +
                            "ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
                    im.SendAnswer(chat_id, name, newmessage);
                    return;
                }
                else if ( sticker_url.startsWith("https://t.me/addstickers/") ){
                    sticker_url = instance.getTokenFromStickerUrl(sticker_url);
                }
            }
            else if ( update.getMessage().getReplyToMessage().hasSticker() ) {
                sticker_url = update.getMessage().getReplyToMessage().getSticker().getSetName();
                im.deleteMessage(chat_id, update.getMessage().getReplyToMessage().getMessageId());
            }
            if ( instance.checkStickerInList(String.valueOf(chat_id), sticker_url) ){
                if ( instance.deleteSticker(String.valueOf(chat_id), sticker_url, true) ){
                    newmessage = "Стрикер-пак " + sticker_url + " удален из списка игнорируемых стикеров;";
                }
                else {
                    newmessage = "АДМИН, ТІ ЧУРКА ЕБАНІАЯ";
                }
            }
        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
