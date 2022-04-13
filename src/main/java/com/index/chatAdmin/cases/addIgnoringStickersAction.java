package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.data.sql.stickerInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

public class addIgnoringStickersAction {
    IndexMain im = new IndexMain();
    stickerInfoHolder instance = stickerInfoHolder.getInstance();
    StringBuilder newmessage = new StringBuilder();

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
            newmessage.append("Для добавление стикер-пака в список исключений, " +
                    "отправьте комманду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;");
            im.SendAnswer(chat_id, name, String.valueOf(newmessage));
            return;
        }
        else {
            if ( update.getMessage().getReplyToMessage() == null && st.hasMoreTokens() ) {
                sticker_url = st.nextToken();
                if ( st.hasMoreTokens() ){
                    newmessage.append("Для добавление стикер-пака в список исключений, " +
                            "отправьте комманду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;");
                    im.SendAnswer(chat_id, name, String.valueOf(newmessage));
                    return;
                }
                if ( sticker_url.startsWith("https://t.me/addstickers/") ){
                    sticker_url = instance.getTokenFromStickerUrl(sticker_url);
                }
            }
            else if ( update.getMessage().getReplyToMessage().hasSticker() ) {
                sticker_url = update.getMessage().getReplyToMessage().getSticker().getSetName();
                im.deleteMessage(chat_id, update.getMessage().getReplyToMessage().getMessageId());
            }

            if ( instance.checkStickerInList(String.valueOf(chat_id), sticker_url) ) {
                newmessage.append("Стикер-пак ").append(sticker_url).append(" уже добавлен в список исключений;");
            }
            else if ( instance.addNewSticker(String.valueOf(chat_id), sticker_url, true) ){
                newmessage.append("Стикер-пак ").append(sticker_url).append(" успешно добавлен в список исключений;");
            }
            else {
                newmessage.append("Произошла ошибка при добавлении нового стикера");
            }
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        im.SendAnswer(chat_id, name, String.valueOf(newmessage));
    }
}
