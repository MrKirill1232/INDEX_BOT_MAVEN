package com.index.chatModeration;

import com.index.IndexMain;
import com.index.chatAdmin.cases.listOfIgnoringStickersAction;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import com.index.commandsHandler.UserCommansHandler;
import com.index.data.sql.chatInfoHolder;
import com.index.data.sql.restrictionFilesHolder;
import com.index.data.sql.stickerInfoHolder;
import com.index.data.sql.userInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public class ChatModerationHandler {

    IndexMain im = new IndexMain();
    muteHandler mh = new muteHandler();
    userInfoHolder user_holder = userInfoHolder.getInstance();
    chatInfoHolder chat_holder = chatInfoHolder.getInstance();
    String original_message;
    String user_id;
    String update_name;
    String chat_id;
    Long curr_time;
    Long mute_time;
    Long next_reset_time;
    Long curr_mute_time;
    String message_id;
    Long update_time;
    List<String> ignoreUsers;
    List<String> user_moderation;

    private void setVariables(Update update){
        Message temp = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage() : null;
        if ( temp == null ) return;
        original_message = temp.getText() != null ? temp.getText() : temp.getCaption() != null ? temp.getCaption() : null;
        user_id = temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId());
        update_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        chat_id = String.valueOf(temp.getChatId());
        if ( !chat_holder.checkChatInDB(chat_id) ) {
            if ( !chat_holder.addNewChatInfo(update) ) {
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        curr_time = calendar.getTimeInMillis() / 1000;
        int restriction_time = Math.toIntExact(chat_holder.getTemplate(chat_id).get_reset_time()); // minutes
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + restriction_time);
        mute_time = calendar.getTimeInMillis() / 1000;
        user_moderation = chat_holder.getTemplate(chat_id).get_user_moderation();
        ignoreUsers = chat_holder.getTemplate(chat_id).get_admins_list();
        /*
        ignoreUsers.add("499220683");       // MrKirill1232
        ignoreUsers.add("1087968824");      // YummyChannel
        ignoreUsers.add("1093703997");      // Altair
        ignoreUsers.add("-1001454322922L"); // YummyChannel_CHAT
        user_moderation.add("610980102");      // Исаия
        user_moderation.add("499220683");      // MrKirill1232
        */
        if ( !user_holder.checkUserInDB(String.valueOf(chat_id), String.valueOf(user_id)) ) {
            addUserInDataBase(false);
        }
        curr_mute_time = user_holder.getTemplate(chat_id, user_id).get_restriction_time() != null ? Long.parseLong(user_holder.getTemplate(chat_id, user_id).get_restriction_time()) : 0;
        next_reset_time = user_holder.getTemplate(chat_id, user_id).get_next_message_reset() != null ? Long.parseLong(user_holder.getTemplate(chat_id, user_id).get_next_message_reset()) : 0;
        message_id = String.valueOf(update.getMessage().getMessageId());
    }

    protected void addUserInDataBase(Boolean announce_type) {
        if (announce_type) im.SendAnswer(chat_id, update_name, "Пробую добавить...");
        if (user_holder.addNewUser(String.valueOf(chat_id), update_name, String.valueOf(user_id), 0, 0, String.valueOf(curr_time), 0, String.valueOf(0), null)){
            if (announce_type) im.SendAnswer(chat_id, "Index - DataBase", "Пользователь " + update_name + " добавлен в базу.");
        }
        else {
            sendMessage("Ошибка при добавлении пользователя " + update_name + " в базу. @MrKirill1232");
        }
    }
    protected void deleteMessage(){
        im.deleteMessage(Long.parseLong(chat_id), Integer.parseInt(message_id));
    }
    protected void sendMessage(String message){
        im.SendAnswer(Long.parseLong(chat_id), "Index", message, "null", 0);
    }
    protected void callMute(Calendar time, String comment, boolean announce, Update bot_comment){
        if (announce) sendMessage(update_name + " - " + comment + " Разблокировка - " + time.getTime());
        mh.callMute(chat_id, user_id, "Index", update_name, comment, time, true, bot_comment);
    }
    private boolean ifOutDate(){
        Calendar update_time_calendar = Calendar.getInstance();
        update_time_calendar.setTimeInMillis(update_time*1000);
        update_time_calendar.add(Calendar.MINUTE, 10);
        return ( update_time_calendar.getTimeInMillis() < System.currentTimeMillis() ) ;
    }
    public ChatModerationHandler (Update update)
    {
        if ( update.getMessage() == null && !update.hasCallbackQuery() )
        {
            return;
        }
        update_time = update.getMessage() == null ? update.getCallbackQuery().getMessage().getDate().longValue() : update.getMessage().getDate().longValue();
        if ( update.hasCallbackQuery() && !ifOutDate()) {
            original_message = update.getCallbackQuery().getData();
            original_message = original_message.substring(13);
            new listOfIgnoringStickersAction(update, original_message);
            return;
        } else if ( update.hasCallbackQuery() && ifOutDate() ) { System.out.println("UMU"); return; }
        setVariables(update);
        if ( update.getMessage().getChatId() > 0 ) {
            return;
        }
        if ( ( chat_id.equals(String.valueOf(im.YummyChannel_CHAT)) && im.RESEND ) ){
            new ModeratorChat(update, "Forwarding");
        }
        if ( chat_id.equals(String.valueOf(im.YummyReChat)) ) {
            new ModeratorChat(update, "Translate");
        }
        if ( ( ( original_message==null || !original_message.startsWith("/") ) &&
                ( (ignoreUsers != null && ignoreUsers.contains(String.valueOf(user_id))) ) )
                || chat_id.equals(String.valueOf(im.YummyReChat) ) ){
            return;
        }
        if ( curr_mute_time > curr_time){
            deleteMessage();
            return;
        }
        if ( next_reset_time < curr_time ) {
            resetFields();
        }
        if ( ( original_message != null && original_message.startsWith("/") ) && ( (user_moderation != null && user_moderation.contains(String.valueOf(user_id))) || ( ignoreUsers!=null && ignoreUsers.contains(String.valueOf(user_id))) )){
            new UserCommansHandler(update);
            return;
        }
        CheckRestriction(update);
    }

    protected String sendGreeting(){
        return "Привет " + update_name +"! Добро пожаловать в " + "Чат YummyAnime" + " :)\n" +
                "Правила можешь найти здесь - [*клик*](https://t.me/c/1454322922/65922/)\n" +
                "Если интересует как обойти блокировку сайта - [*клик*](https://t.me/c/1454322922/21351/)";
    }

    private void CheckRestriction(Update update){
        Calendar calendar = Calendar.getInstance();
        Message temp = update.getMessage();
        restrictionFilesHolder rfh = restrictionFilesHolder.getInstance();
        String file_id;
        boolean need_to_announce = false;
        if ( temp.hasAnimation() ) {
            file_id = temp.getAnimation().getFileUniqueId();
            if ( rfh.isRestrictionGIFIDs(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "GIF файлы подозрительного характера - автоматическая блокировка;", false, update);
                need_to_announce = true;
            } else {
                int max_available_gif = chat_holder.getTemplate(chat_id).get_max_gif_count();
                int gif_count = user_holder.getTemplate(chat_id, user_id).get_gif_count();
                if ( gif_count > max_available_gif ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                    calendar.add(Calendar.MINUTE, 2);
                    callMute(calendar, "Спам GIF файлами - автоматическая блокировка;", true, null);
                } else {
                    if ( !ifOutDate() ) user_holder.updateGifCount(chat_id, user_id, ( gif_count + 1 ));
                }
            }
        }
        if ( temp.hasVideo() ){
            file_id = temp.getVideo().getFileUniqueId();
            if ( rfh.isRestrictionVideoIDs(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "Видео файлы подозрительного характера - автоматическая блокировка;\n" + update, false, update);
                need_to_announce = true;
            }
        }
        if ( temp.hasSticker() ) {
            file_id = temp.getSticker().getSetName();
            if ( rfh.isRestrictionStickerIDs(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "Стикеры подозрительного характера - автоматическая блокировка;", false, update);
            } else {
                int sticker_available_count = chat_holder.getTemplate(chat_id).get_max_sticker_count();
                int sticker_count = user_holder.getTemplate(chat_id, user_id).get_sticker_count();
                if ( !stickerInfoHolder.getInstance().checkStickerInList(String.valueOf(chat_id), file_id) ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                }
                if ( sticker_count > sticker_available_count ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                    calendar.add(Calendar.MINUTE, 2);
                    callMute(calendar, "Спам стрикерами - Автоматическая блокировка;", true, null);
                }
                if ( !ifOutDate() ) user_holder.updateStickerCount(chat_id, user_id, ( sticker_count + 1 ) );
            }
        }
        if ( update.getMessage().hasVoice()
                || update.getMessage().hasDocument()
                || update.getMessage().hasText()
                || update.getMessage().hasAudio()
                || update.getMessage().hasContact()
                || update.getMessage().hasDice()
                || update.getMessage().hasLocation()
                || update.getMessage().hasPassportData()
                || update.getMessage().hasPhoto()
                || update.getMessage().hasPoll()
                || update.getMessage().hasReplyMarkup()
                || update.getMessage().hasSuccessfulPayment()
                || update.getMessage().hasInvoice()
                || update.getMessage().hasVideo()
                || update.getMessage().hasVideoNote() ) {
        }
        if (temp.hasViaBot())
        {
            if ( rfh.isRestrictionViaBotIDs(String.valueOf(temp.getViaBot().getId())))
            {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
            }
        }
        if ( temp.hasPhoto() ) {
            file_id = Objects.requireNonNull(temp.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null)).getFileUniqueId();
            if ( rfh.isRestrictionPhotoIDs(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "Фото файлы подозрительного характера - автоматическая блокировка;", false, update);
                need_to_announce = true;
            }
        }
        if ( temp.getNewChatMembers().stream().findFirst().isPresent() ){
            new ModeratorChat(update, "DeleteMe");
            deleteMessage();
            if ( false ) {
                sendMessage(sendGreeting());
            }
        }
        if ( need_to_announce ) {
            sendMessage("@MrKirill1232 - иди сюда, тут расчленёнка от " + update.getMessage().getFrom());
        }
    }

    protected void resetFields() {
            user_holder.updateNextMessageReset(String.valueOf(chat_id), String.valueOf(user_id), String.valueOf(mute_time));
            user_holder.updateGifCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
            user_holder.updateStickerCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
    }
}
