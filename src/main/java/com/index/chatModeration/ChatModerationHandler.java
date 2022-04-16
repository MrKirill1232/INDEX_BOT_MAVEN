package com.index.chatModeration;

import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import com.index.data.sql.restrictionFilesHolder;
import com.index.data.sql.stickerInfoHolder;
import com.index.data.sql.userInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatModerationHandler {

    IndexMain im = new IndexMain();
    muteHandler mh = new muteHandler();
    userInfoHolder holder = userInfoHolder.getInstance();
    String original_message;
    String user_id;
    String update_name;
    String chat_id;
    Long curr_time;
    Long mute_time;
    Long next_reset_time;
    Long curr_mute_time;
    String message_id;

    List<String> ignoreUsers = new ArrayList<>(); // TODO - PUT IN SINGLETON

    private void setVariables(Update update){
        Message temp = update.getMessage();
        original_message = temp.hasText() ? temp.getText() : temp.getCaption() != null ? temp.getCaption() : null;
        user_id = temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId());
        update_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        chat_id = String.valueOf(temp.getChatId());
        Calendar calendar = Calendar.getInstance();
        curr_time = calendar.getTimeInMillis() / 1000;
        int restriction_time = 2; // minutes
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + restriction_time);
        mute_time = calendar.getTimeInMillis() / 1000;
        //ignoreUsers.add("499220683");       // MrKirill1232
        ignoreUsers.add("1087968824");      // YummyChannel
        ignoreUsers.add("1093703997");      // Altair
        ignoreUsers.add("-1001454322922L"); // YummyChannel_CHAT
        // TODO: REMADE PUNISHMENT SYSTEM
        if ( !holder.checkUserInDB(String.valueOf(chat_id), String.valueOf(user_id)) ) {
            addUserInDataBase(false);
        }
        curr_mute_time = holder.getTemplate(chat_id, user_id).get_restriction_time() != null ? Long.parseLong(holder.getTemplate(chat_id, user_id).get_restriction_time()) : 0;
        next_reset_time = holder.getTemplate(chat_id, user_id).get_next_message_reset() != null ? Long.parseLong(holder.getTemplate(chat_id, user_id).get_next_message_reset()) : 0;
        message_id = String.valueOf(update.getMessage().getMessageId());
    }

    protected void addUserInDataBase(Boolean announce_type) {
        if (announce_type) im.SendAnswer(chat_id, update_name, "Пробую добавить...");
        if (holder.addNewUser(String.valueOf(chat_id), update_name, String.valueOf(user_id), 0, 0, String.valueOf(curr_time), 0, String.valueOf(0), null)){
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
    protected void callMute(Calendar time, String comment, boolean announce){
        if (announce) sendMessage(update_name + " - " + comment + " Разблокировка - " + time.getTime());
        mh.tryMute(Long.parseLong(chat_id), Long.parseLong(user_id), "Index", update_name, time.getTimeInMillis()/1000, true, comment);
    }
    public ChatModerationHandler (Update update)
    {
        if ( update.getMessage() == null )
        {
            return;
        }
        setVariables(update);
        if ( ( chat_id.equals(String.valueOf(im.YummyChannel_CHAT)) && im.RESEND ) ){
            new ModeratorChat(update, "Forwarding");
        }
        if ( chat_id.equals(String.valueOf(im.YummyReChat)) ) {
            new ModeratorChat(update, "Translate");
        }
        if ( ignoreUsers.contains(String.valueOf(user_id)) || chat_id.equals(String.valueOf(im.YummyReChat)) ){
            return;
        }
        if ( curr_mute_time > curr_time){
            deleteMessage();
            return;
        }
        if ( next_reset_time < curr_time ) {
            resetFields();
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
            if ( rfh.isRestrictionGIFIDs(file_id) || black_list_gif(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "GIF файлы подозрительного характера - автоматическая блокировка;\n" + update, false);
                need_to_announce = true;
            } else {
                int max_available_gif = 5;
                int gif_count = holder.getTemplate(chat_id, user_id).get_gif_count();
                if ( gif_count > max_available_gif ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                    calendar.add(Calendar.MINUTE, 2);
                    callMute(calendar, "Спам GIF файлами - автоматическая блокировка;", true);
                } else {
                    holder.updateGifCount(chat_id, user_id, ( gif_count + 1 ));
                }
            }
        }
        if ( temp.hasVideo() ){
            file_id = temp.getAnimation().getFileUniqueId();
            if ( rfh.isRestrictionVideoIDs(file_id) || black_list_video(file_id) ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "Видео файлы подозрительного характера - автоматическая блокировка;\n" + update, false);
                need_to_announce = true;
            }
        }
        if ( temp.hasSticker() ) {
            file_id = temp.getSticker().getSetName();
            if ( rfh.isRestrictionStickerIDs(file_id) || file_id.equalsIgnoreCase("gayman02") ) {
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
                calendar.add(Calendar.MINUTE, 10);
                callMute(calendar, "Стикеры подозрительного характера - автоматическая блокировка;\n" + update, false);
            } else {
                int sticker_available_count = 10;
                int sticker_count = holder.getTemplate(chat_id, user_id).get_sticker_count();
                if ( !stickerInfoHolder.getInstance().checkStickerInList(String.valueOf(chat_id), file_id) ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                }
                if ( sticker_count > sticker_available_count ) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage();
                    calendar.add(Calendar.MINUTE, 2);
                    callMute(calendar, "Спам стрикерами - Автоматическая блокировка;", true);
                }
                holder.updateStickerCount(chat_id, user_id, ( sticker_count + 1 ) );
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
        if (update.getMessage().hasViaBot())
        {
            if ( temp.getViaBot().getId() == (1745626430) ||
                    temp.getViaBot().getId() == (1341194997)){
                new ModeratorChat(update, "DeleteMe");
                deleteMessage();
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
            holder.updateNextMessageReset(String.valueOf(chat_id), String.valueOf(user_id), String.valueOf(mute_time));
            holder.updateGifCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
            holder.updateStickerCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
    }

    private boolean black_list_gif (String file_id){
        List<String> temp = new ArrayList<>();
        temp.add("AgADVAIAAqTNAUs");
        temp.add("AQADVAIAAqTNAUty");
        temp.add("AgADmAkAAr5RIEk");
        temp.add("AgAD9AEAAjykEFA");
        temp.add("AgADYwUAAnd7AVA");
        temp.add("AgADrQsAArcB4Uk");
        temp.add("AQADrQsAArcB4Uly");
        temp.add("AgADzhIAAvmVyUg");
        temp.add("AgADZBMAAiYx6Es");
        temp.add("AgADkRIAAs5esUg");
        temp.add("AgADVwQAAsaCKFM");
        return temp.contains(file_id);
    }

    private boolean black_list_video (String file_id){
        List<String> temp = new ArrayList<>();
        temp.add("AgADhRMAAm4AAfBL");
        return temp.contains(file_id);
    }
}
