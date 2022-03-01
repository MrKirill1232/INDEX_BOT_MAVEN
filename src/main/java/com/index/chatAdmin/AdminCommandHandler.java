package com.index.chatAdmin;

import com.index.IndexMain;
import com.index.chatAdmin.cases.*;
import com.index.chatAdmin.handlers.banHandler;
import com.index.dbHandler.dbMain;
import com.index.dbHandler.handlers.dbGIFHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.StringTokenizer;

public class AdminCommandHandler {

    dbGIFHandler gifh = new dbGIFHandler();
    IndexMain im = new IndexMain();
    String newmessage;

    String name;
    String orig_message;
    Long user_id;
    String user_name;
    long chat_id;

    public AdminCommandHandler (Update update){
        name = update.getMessage().getFrom().getFirstName();
        orig_message = update.getMessage().getText();
        user_id = update.getMessage().getFrom().getId();
        user_name = update.getMessage().getFrom().getUserName();
        chat_id = update.getMessage().getChatId();

        if ( !CheckUserPermissions(update) ){
            // если правда - у пользователя достаточно прав
            return;
        }
        if (orig_message.startsWith("//delay"))
        {
            new delayAction(update);
        }
        else if (orig_message.startsWith("//GetChatID")){
            newmessage = String.valueOf(chat_id);
            im.SendAnswer(499220683, user_name, newmessage);
        }
        else if (orig_message.startsWith("//AddIgnoringStickers")) {
            new addIgnoringStickersAction(update);
        }
        else if (orig_message.startsWith("//RemoveIgnoringStickers")) {
            new removeIgnoringStikersAction(update);
        }
        else if (orig_message.startsWith("//ListOfIgnoringStickers")) {
            StringTokenizer st = new StringTokenizer(orig_message);
            st.nextToken();
            String sticker_url = "";
            if (st.hasMoreTokens()){
                while (st.hasMoreTokens()){
                    sticker_url += st.nextToken().toString();
                }
                new listOfIgnoringStickersAction(update, sticker_url);
            }
            else {
                new listOfIgnoringStickersAction(update, "null");
            }

        }
        else if (orig_message.startsWith("//mute")) {
            new muteAction(update);
        }
        else if (orig_message.startsWith("//unmute")) {
            new muteAction(update);
        }
        else if (orig_message.startsWith("//getEntities")) {
            new getEntitiesAction(update);
        }
        else if (orig_message.startsWith("//pin")) {
            new pinAction(update);
        }
        else if (orig_message.startsWith("//getFileID")){
            new getFileIDAction(update);
        }
        else if (orig_message.startsWith("//getUserID")){
            new getUserIDAction(update);
        }
        else if (orig_message.startsWith("//getViaBotID")){
            newmessage = String.valueOf(update.getMessage().getReplyToMessage().getViaBot().getId());
            im.SendAnswer(chat_id, name, newmessage);
        }
        else if (orig_message.startsWith("//dbStatus")){
            newmessage = String.valueOf(gifh.UpdateRestrictionTime(chat_id, user_id, name, String.valueOf(1)));
            im.SendAnswer(chat_id, name, newmessage);
        }
        else if (orig_message.startsWith("//dbClose")){
            dbMain.close();
        }
        else if ( orig_message.startsWith("//ban")){
            new banHandler(update);
        }
        else if ( orig_message.startsWith("//getMessageID")){
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
            final StringTokenizer st = new StringTokenizer(orig_message);
            st.nextToken();
            im.deleteMessage(im.YummyChannel_CHAT, Integer.parseInt(st.nextToken().toString()));
        }
        else if ( orig_message.startsWith("//remove_url")){
            final StringTokenizer st = new StringTokenizer(orig_message);
            st.nextToken();
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setDisableWebPagePreview(true);
            editMessageText.setChatId(String.valueOf(im.YummyChannel_CHAT));
            editMessageText.setMessageId(Integer.valueOf(st.nextToken().toString()));
            editMessageText.setText("Ну и от Кирилла.\n" +
                    "Чат - не знаю, завтра или послезавтра открою наверное... Или уже после того как все закончится...\n" +
                    "Если не скучно будет - можете вот исходники бота поколупать :)\n" +
                    "https://github.com/MrKirill1232/INDEX_BOT_MAVEN\n" +
                    "Бот кое-как работает, по-удалял там что Вам не нужно видеть, не думаю что в ближайшее время я им буду заниматься :(");
            try {
                im.execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    boolean CheckUserPermissions (Update update){
        if ( update.getMessage().getFrom().getId() == 499220683 || update.getMessage().getFrom().getId() == 1087968824
        || update.getMessage().getFrom().getId() == 1093703997 || update.getMessage().getFrom().getId() == 1087968824 )
        {
            return true;
        }
        return false;
    }
}
