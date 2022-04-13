package com.index.chatModeration;

import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.cases.GifAction;
import com.index.chatModeration.cases.StickerAction;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import com.index.data.sql.userInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ChatModerationHandler {

    IndexMain im = new IndexMain();
    muteHandler mh = new muteHandler();
    userInfoHolder holder = userInfoHolder.getInstance();
    String newmessage;
    String original_message;

    final long curr_time = System.currentTimeMillis()/1000;

    String name_from;
    long user_id;

    String file_id;


    public ChatModerationHandler (Update update)
    {
        if ( update.getMessage() == null )
        {
            return;
        }
        if ( update.getMessage().getSenderChat() == null ) {
            user_id = update.getMessage().getFrom().getId();
        } else {
            user_id = update.getMessage().getSenderChat().getId();
        }
        if (update.getMessage() !=null && update.getMessage().getSenderChat() == null) {
            name_from = update.getMessage().getFrom().getFirstName();
        } else {
            name_from = update.getMessage().getSenderChat().getTitle();
        }

        Long chat_id = update.getMessage().getChatId();
        String name = update.getMessage().getFrom().getFirstName();

        if ( update.getMessage().getChatId() == im.YummyChannel_CHAT){
            if (im.RESEND) {
                new ModeratorChat(update, "Forwarding");
            }
        }

        if ( update.getMessage().getChatId() == im.YummyReChat )
        {
            new ModeratorChat(update, "Translate");
        }


        if (CheckUserPermissions(update)) {
            return;
        }
        boolean check_user = holder.checkUserInDB(String.valueOf(chat_id), String.valueOf(user_id));
        if (!check_user) {
            im.SendAnswer(chat_id, name, "Пробую добавить...");
            holder.addNewUser(String.valueOf(chat_id), name_from, String.valueOf(user_id), 0, 0, String.valueOf(curr_time), 0, String.valueOf(0), null);
            newmessage = "Пользователь " + name_from + " добавлен в базу";
            im.SendAnswer(chat_id, name, newmessage);
        }
        if ( Long.parseLong(holder.getTemplate(String.valueOf(chat_id), String.valueOf(user_id)).get_restriction_time()) > curr_time){
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        if (update.getMessage().hasAnimation()) {
            if (false) {
                new GifAction(update);
            } else {
                CheckGIFinDBResriction(update);
            }
        }
        else if (update.getMessage().hasSticker()) {
            if (true){
                new StickerAction(update);
            }
        }
        else if ( update.getMessage().hasViaBot() ){
            if ( GetSa4tikBot(update) ){
                im.deleteMessage(chat_id, update.getMessage().getMessageId());
            }
        }
        else if (       update.getMessage().hasVoice()
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
                || update.getMessage().hasVideoNote()){
            if ( update.getMessage().hasVideo() ){
                if ( black_list_video(update) ){
                    new ModeratorChat(update, "DeleteMe");
                    im.deleteMessage(chat_id, update.getMessage().getMessageId());
                    im.SendAnswer(chat_id, name, "@MrKirill1232 - иди сюда, тут расчленёнка от " + update.getMessage().getFrom());
                    mh.tryMute(chat_id, user_id, name, name_from, 120, true, String.valueOf(update.getMessage().getMessageId()));
                }
            }
        }
        /**
         *         // ПРОВЕРКА И ОТПРАВКА СООБЩЕНИЯ ДЛЯ НОВЫХ ЮЗВЕРЕЙ
         *         // update.getChatMember().getNewChatMember().getStatus()
         **/
        if (update.getMessage().getNewChatMembers().stream().findFirst().isPresent()){
            new ModeratorChat(update, "DeleteMe");
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        /*else if (update.getMessage().getNewChatMembers().get(0) != null){
            newmessage = "Привет " + String.valueOf(name) +"! Добро пожаловать в " + update.getMessage().getChat().getTitle() + " :)\n" +
                    "Правила можешь найти здесь - [*клик*](https://t.me/c/1454322922/65922/)\n" +
                    "Если интересует как обойти блокировку сайта - [*клик*](https://t.me/c/1454322922/21351/)";
            //im.SendAnswer(chat_id, name, newmessage,"Markdown", update.getMessage().getMessageId());

        }*/
    }

    boolean GetSa4tikBot(Update update){
        original_message = update.getMessage().getText();
        if (update.getMessage().hasViaBot())
        {
            return (update.getMessage().getViaBot().getId() == (1745626430))
                    || (update.getMessage().getViaBot().getId() == (1341194997));
        }
        return false;
    }

    boolean CheckUserPermissions (Update update){
        long MrKirill1232 = 499220683;
        long YummyChannel = 1087968824;
        long Altair = 1093703997;
        long YummyChannel_CHAT = -1001454322922L;
        if ( (/*update.getMessage().getFrom().getId() == MrKirill1232
                ||*/ update.getMessage().getFrom().getId() == YummyChannel
                /*|| update.getMessage().getFrom().getId() == Altair
                || update.getMessage().getChatId() == YummyChannel_CHAT*/ ) )
        {
            return true;
        }
        else return update.getMessage().getChatId() == im.YummyReChat;
    }

    boolean CheckGIFinDBResriction(Update update) {
        long chat_id = update.getMessage().getChatId();

        long curr_time = System.currentTimeMillis() / 1000;
        long max_restriction_time = 1; // mins
        long restriction_time = curr_time + (max_restriction_time * 60);
        String name = "Index_BOT";
        if (update.getMessage().hasAnimation()) {
            boolean check_user = holder.checkUserInDB(String.valueOf(chat_id), String.valueOf(user_id));
            if (!check_user) {
                im.SendAnswer(chat_id, name, "Пробую добавить...");
                holder.addNewUser(String.valueOf(chat_id), name_from, String.valueOf(user_id), 0, 0, String.valueOf(curr_time), 0, String.valueOf(0), null);
                newmessage = "Пользователь " + name_from + " добавлен в базу";
                im.SendAnswer(chat_id, name, newmessage);
            }
        }
        long last_restriction_time = Long.parseLong(holder.getTemplate(String.valueOf(chat_id), String.valueOf(user_id)).get_restriction_time());
        if (last_restriction_time <= curr_time) {
            holder.updateNextMessageReset(String.valueOf(chat_id), String.valueOf(user_id), String.valueOf(restriction_time));
            holder.updateGifCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
            holder.updateStickerCount(String.valueOf(chat_id), String.valueOf(user_id), 0);
        }
        if (black_list_gif(update)) {
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
            im.SendAnswer(chat_id, name, "@MrKirill1232 - иди сюда, тут расчленёнка от " + update.getMessage().getFrom());
            mh.tryMute(chat_id, user_id, name, name_from, 120, true);

        }
        if (update.getMessage().getAnimation().getFileUniqueId().startsWith("AgADZBMAAiYx6Es")
                || update.getMessage().getAnimation().getFileUniqueId().startsWith("AgADkRIAAs5esUg")
                || update.getMessage().getAnimation().getFileUniqueId().startsWith("AgADVwQAAsaCKFM")
        ) {
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        int gif_count = holder.getTemplate(String.valueOf(chat_id), String.valueOf(user_id)).get_gif_count();
        int new_gif_count = gif_count + 1;
        if (gif_count == 2 || gif_count == 1 || gif_count == 0) {
            holder.updateGifCount(String.valueOf(chat_id), String.valueOf(user_id), new_gif_count);
        } else if (gif_count == 3) {
            long mute_time_in_seconds = 120;
            long mute_time = curr_time + mute_time_in_seconds;
            holder.updateGifCount(String.valueOf(chat_id), String.valueOf(user_id), new_gif_count);
            mh.tryMute(chat_id, user_id, name, name_from, mute_time, true);
            im.SendAnswer(chat_id, name, "За спам ГИФ файлами, блокируем " + name_from + " на " + mute_time_in_seconds + " секунд :)");
        } else {
            im.deleteMessage(chat_id, update.getMessage().getMessageId());
        }
        return true;
    }

    private boolean black_list_gif (Update update){
        file_id = update.getMessage().getAnimation().getFileUniqueId();
        return file_id.startsWith("AgADVAIAAqTNAUs")
                || file_id.startsWith("AQADVAIAAqTNAUty")
                || file_id.startsWith("AgADmAkAAr5RIEk")
                || file_id.startsWith("AgAD9AEAAjykEFA")
                || file_id.startsWith("AgADYwUAAnd7AVA")
                || file_id.startsWith("AgADrQsAArcB4Uk")
                || file_id.startsWith("AQADrQsAArcB4Uly")
                || file_id.startsWith("AgADzhIAAvmVyUg");
    }

    private boolean black_list_video (Update update){
        file_id = update.getMessage().getVideo().getFileUniqueId();
        return file_id.startsWith("AgADhRMAAm4AAfBL");
    }
}
