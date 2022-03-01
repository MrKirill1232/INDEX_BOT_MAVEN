package com.index.chatAdmin.handlers;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class banHandler {

    IndexMain im = new IndexMain();
    long chat_id;

    /*public banHandler(Update update) {
    }*/
    public banHandler(Update update){

        int mod_mes = update.getMessage().getMessageId();
        BanChatMember BAN = new BanChatMember();
        BAN.setChatId(String.valueOf(update.getMessage().getChatId()));
        BAN.setUserId(update.getMessage().getReplyToMessage().getFrom().getId());
        BAN.setRevokeMessages(false);
        BAN.getUntilDate();
        UnbanChatMember UNBAN = new UnbanChatMember();
        UNBAN.setUserId(update.getMessage().getReplyToMessage().getFrom().getId());
        UNBAN.setChatId(String.valueOf(update.getMessage().getChatId()));
        try {
            im.execute(BAN);
            im.execute(UNBAN);
            im.deleteMessage(chat_id, mod_mes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
