package com.index.chatAdmin;

import com.index.IndexMain;
import com.index.chatAdmin.cases.*;
import com.index.chatAdmin.handlers.banHandler;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.data.sql.restrictionFilesHolder;
import com.index.data.sql.userInfoHolder;
import com.index.dbHandler.dbMain;
import com.index.future.FutureAction;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class AdminCommandHandler {

    IndexMain im = new IndexMain();
    String newmessage;

    String name;
    String orig_message;
    Long user_id;
    Long call_user_id;
    String user_name;
    String re_name;
    long chat_id;

    public AdminCommandHandler (Update update) {
        name = update.getMessage().getFrom().getFirstName();
        orig_message = update.getMessage().getText().toLowerCase();
        user_id = update.getMessage().getFrom().getId();
        user_name = update.getMessage().getFrom().getUserName();
        chat_id = update.getMessage().getChatId();
        if (update.getMessage().getReplyToMessage() != null){
            if (update.getMessage().getReplyToMessage().getSenderChat() == null) {
                call_user_id = update.getMessage().getReplyToMessage().getFrom().getId();
            } else {
                call_user_id = update.getMessage().getReplyToMessage().getSenderChat().getId();
            }
            if (update.getMessage().getReplyToMessage().getSenderChat() == null) {
                re_name = update.getMessage().getReplyToMessage().getFrom().getFirstName();
            } else {
                re_name = update.getMessage().getReplyToMessage().getSenderChat().getTitle();
            }
        }
        if ( !CheckUserPermissions(update) ){
            // если правда - у пользователя достаточно прав
            return;
        }
        if (orig_message.startsWith("//delay"))
        {
            new delayAction(update);
        }
        else if (orig_message.equalsIgnoreCase("//save_user"))
        {
            userInfoHolder.getInstance().storeChat(String.valueOf(chat_id));
        }
        else if(orig_message.equals("//save_all")) {
            FutureAction.getInstance().save();
        }
        else if ( orig_message.equalsIgnoreCase("//get_info")){
            String message =  userInfoHolder.getInstance().
                    getTemplate(String.valueOf(chat_id),
                            update.getMessage().getReplyToMessage() != null ?
                                    String.valueOf(update.getMessage().getReplyToMessage().getFrom().getId()) :
                                    String.valueOf(user_id)).getAllInfo();
            im.SendAnswer(chat_id, user_name,message);
        }
        else if (orig_message.startsWith("//getchatid")){
            newmessage = String.valueOf(chat_id);
            im.SendAnswer(499220683, user_name, newmessage);
        }
        else if (orig_message.startsWith("//addignoringstickers")) {
            new addIgnoringStickersAction(update);
        }
        else if (orig_message.startsWith("//removeignoringstickers")) {
            new removeIgnoringStikersAction(update);
        }
        else if (orig_message.startsWith("//listofignoringstickers")) {
            StringTokenizer st = new StringTokenizer(orig_message);
            st.nextToken();
            StringBuilder sticker_url = new StringBuilder();
            if (st.hasMoreTokens()){
                while (st.hasMoreTokens()){
                    sticker_url.append(st.nextToken());
                }
                new listOfIgnoringStickersAction(update, sticker_url.toString());
            }
            else {
                new listOfIgnoringStickersAction(update, "null");
            }

        }
        else if (orig_message.startsWith("//mute")) {
            new muteHandler().callMute(update);
        }
        else if (orig_message.startsWith("//unmute")) {
            new muteHandler().callMute(update);
        }
        else if (orig_message.startsWith("//getentities")) {
            new getEntitiesAction(update);
        }
        else if (orig_message.startsWith("//pin")) {
            new pinAction(update);
        }
        else if (orig_message.startsWith("//getfileid")){
            new getFileIDAction(update);
        }
        else if (orig_message.startsWith("//getuserid")){
            new getUserIDAction(update);
        }
        else if (orig_message.startsWith("//getviabotid")){
            newmessage = String.valueOf(update.getMessage().getReplyToMessage().getViaBot().getId());
            im.SendAnswer(chat_id, name, newmessage);
        }
        else if (orig_message.startsWith("//dbclose")){
            dbMain.close();
        }
        else if ( orig_message.startsWith("//ban")){
            new banHandler(update);
        }
        else if ( orig_message.startsWith("//getmessageid")){
            im.SendAnswer(chat_id, name, String.valueOf(update.getMessage().getReplyToMessage().getMessageId()));
        }
        else if ( orig_message.startsWith("//close_messages")){
            ChatPermissions permissions = new ChatPermissions();
            permissions.setCanSendMessages(false);
            SetChatPermissions chatPermissions = new SetChatPermissions();
            chatPermissions.setChatId(String.valueOf(im.YummyChannel_CHAT));
            chatPermissions.setPermissions(permissions);
            try {
                im.execute(chatPermissions);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( orig_message.startsWith("//open_messages")){
            ChatPermissions permissions = new ChatPermissions();
            permissions.setCanSendMessages(true);
            permissions.setCanSendMediaMessages(true);
            permissions.setCanSendOtherMessages(true);
            SetChatPermissions chatPermissions = new SetChatPermissions();
            chatPermissions.setChatId(String.valueOf(im.YummyChannel_CHAT));
            chatPermissions.setPermissions(permissions);
            try {
                im.execute(chatPermissions);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( orig_message.startsWith("//delete")){
            String ID;
            if ( update.getMessage().getReplyToMessage() == null ) {
                final StringTokenizer st = new StringTokenizer(orig_message);
                st.nextToken();
                ID = st.nextToken();
            }
            else {
                ID = String.valueOf(update.getMessage().getReplyToMessage().getMessageId());
            }
            im.deleteMessage(im.YummyChannel_CHAT, Integer.parseInt(ID));
        }
        else if ( orig_message.startsWith("//remove_url")){
            final StringTokenizer st = new StringTokenizer(orig_message);
            st.nextToken();
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setDisableWebPagePreview(true);
            editMessageText.setChatId(String.valueOf(im.YummyChannel_CHAT));
            editMessageText.setMessageId(Integer.valueOf(st.nextToken()));
            editMessageText.setText("""
                    Ну и от Кирилла.
                    Чат - не знаю, завтра или послезавтра открою наверное... Или уже после того как все закончится...
                    Если не скучно будет - можете вот исходники бота поколупать :)
                    https://github.com/MrKirill1232/INDEX_BOT_MAVEN
                    Бот кое-как работает, по-удалял там что Вам не нужно видеть, не думаю что в ближайшее время я им буду заниматься :(""");
            try {
                im.execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( orig_message.startsWith("//test")){
            im.SendAnswer(chat_id, name, userInfoHolder.getInstance().getAllTemplate());
        }
        if ( update.getMessage().getText().toLowerCase().startsWith("//online")) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Наркутить онлайн");
            button.setSwitchInlineQuery("Онлайн успешно накручен!");
            button.setCallbackData("Онлайн успешно накручен!");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            SendMessage message = new SendMessage();
            message.setReplyMarkup(markupInline);
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText("Желаете накрутить онлайн на Asterios?");
            try {
                im.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else if (orig_message.equals("//addtodb")) {
            Message umu;
            if ( update.getMessage().getReplyToMessage() != null ){
                umu = update.getMessage().getReplyToMessage();
            } else return;
            restrictionFilesHolder rfh = restrictionFilesHolder.getInstance();
            if (umu.hasAnimation()) {
                rfh.addRestrictionGIFIDs(umu.getAnimation().getFileUniqueId());
            }
            else if ( umu.hasPhoto() ) {
                rfh.addRestrictionPhotoIDs(Objects.requireNonNull(umu.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null)).getFileUniqueId());
            }
            else if ( umu.hasSticker() ) {
                rfh.addRestrictionStickerIDs(umu.getSticker().getSetName());
            }
            else if ( umu.hasVideo() ) {
                rfh.addRestrictionVideoIDs(umu.getVideo().getFileUniqueId());
            }
            else if ( umu.hasViaBot() ) {
                rfh.addRestrictionViaBotIDs(String.valueOf(umu.getViaBot().getId()));
            }
            rfh.storeMe();
        }
    }

    boolean CheckUserPermissions (Update update){
        return update.getMessage().getFrom().getId() == 499220683 || update.getMessage().getFrom().getId() == 1087968824
                || update.getMessage().getFrom().getId() == 1093703997 || update.getMessage().getFrom().getId() == 244171712;
    }
}
