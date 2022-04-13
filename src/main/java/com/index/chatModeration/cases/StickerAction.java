package com.index.chatModeration.cases;

import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import com.index.data.sql.stickerInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StickerAction {

    muteHandler mh = new muteHandler();
    IndexMain im = new IndexMain();
    // dbStickerHandler sh = new dbStickerHandler();
    stickerInfoHolder instance = stickerInfoHolder.getInstance();

    Integer message_id;
    String name;
    String sticker_url;
    long chat_id;

    public StickerAction (Update update) {
        String re_name = update.getMessage().getFrom().getFirstName();
        name = "Index_bot";
        long time_in_seconds = 120;
        message_id = update.getMessage().getMessageId();
        chat_id = update.getMessage().getChatId();
        sticker_url = update.getMessage().getSticker().getSetName();
        long user_id = update.getMessage().getFrom().getId();

        if (sticker_url.equals("gayman02")){
            if ( mh.tryMute(chat_id, user_id, name, re_name, time_in_seconds, true)){
            }
        }
        if ( !instance.checkStickerInList(String.valueOf(chat_id), sticker_url) ){
            /**
             * if true - in list
             * if false - delete
             */
            new ModeratorChat(update, "DeleteMe");
            im.SendAnswer(im.YummyReChat, "Index_BOT", "Удалил стикеры выше :)");
            im.deleteMessage(chat_id, message_id);
        }
    }
}
