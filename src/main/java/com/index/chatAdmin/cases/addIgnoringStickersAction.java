package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.dbHandler.handlers.dbStickerHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

public class addIgnoringStickersAction {
    IndexMain im = new IndexMain();
    dbStickerHandler sh = new dbStickerHandler();
    String newmessage = "";

    long chat_id;
    String name;
    String orig_message;
    String sticker_url;

    public addIgnoringStickersAction(Update update) {
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();
        final StringTokenizer st = new StringTokenizer(orig_message);
        st.nextToken();
        if (       ( update.getMessage().getReplyToMessage() == null && ( !st.hasMoreTokens() ))
                || ( (update.getMessage().getReplyToMessage() != null && !update.getMessage().getReplyToMessage().hasSticker()) && ( !st.hasMoreTokens() ) )  ) {
            newmessage = "Для добавление стикер-пака в список исключений, " +
                    "отправьте комманду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }
        else {
            if ( update.getMessage().getReplyToMessage() == null && st.hasMoreTokens() ) {
                sticker_url = st.nextToken();
                if ( st.hasMoreTokens() ){
                    newmessage = "Для добавление стикер-пака в список исключений, " +
                            "отправьте комманду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
                    im.SendAnswer(chat_id, name, newmessage);
                    return;
                }
                if ( sticker_url.startsWith("https://t.me/addstickers/") ){
                    sticker_url = sh.getTokenFromStickerUrl(sticker_url);
                }
            }
            else if ( update.getMessage().getReplyToMessage().hasSticker() ) {
                sticker_url = update.getMessage().getReplyToMessage().getSticker().getSetName();
                im.deleteMessage(chat_id, update.getMessage().getReplyToMessage().getMessageId());
            }

            if ( sh.checkStickerInList(sticker_url, chat_id, name) ) {
                newmessage = "Стикер-пак " + sticker_url + " уже добавлен в список исключений;";
            }
            else if ( sh.getAddStickerStatus(sticker_url, chat_id, name) ){
                newmessage = "Стикер-пак " + sticker_url + " успешно добавлен в список исключений;";
            }
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
