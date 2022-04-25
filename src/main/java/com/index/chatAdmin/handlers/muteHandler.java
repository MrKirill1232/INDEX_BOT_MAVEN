package com.index.chatAdmin.handlers;

import com.index.IndexMain;
import com.index.data.sql.userInfoHolder;
import com.index.dbHandler.handlers.dbRestrictionHandler;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.StringTokenizer;

public class muteHandler {

    IndexMain im = new IndexMain();

    public boolean  callMute(Update update) {
        Message temp = update.getMessage();
        String chat_id = String.valueOf(update.getMessage().getChatId());
        StringBuilder user_id = new StringBuilder();
        String call_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        String mute_name = temp.getReplyToMessage() == null ? null : temp.getReplyToMessage().getSenderChat() == null ? temp.getReplyToMessage().getFrom().getFirstName() : temp.getReplyToMessage().getSenderChat().getTitle();
        StringBuilder comment = new StringBuilder();
        Calendar time = null;
        boolean set_mute = true;
        String message = update.getMessage().getText().toLowerCase();
        StringTokenizer st = new StringTokenizer(message);
        st.nextToken();
        if (message.startsWith("//unmute")){
            set_mute = false;
            while ( st.hasMoreTokens() ){
                user_id.append(st.nextToken());
            }
            user_id = new StringBuilder(user_id.isEmpty() ? temp.getReplyToMessage().getSenderChat() == null ? String.valueOf(temp.getReplyToMessage().getFrom().getId()) : String.valueOf(temp.getReplyToMessage().getSenderChat().getId()) : user_id);
            mute_name = mute_name == null ? userInfoHolder.getInstance().getTemplate(chat_id, user_id.toString()).get_user_name() : mute_name;
        }
        else if (message.contains("//mute") || message.contains("/mute") ){
            if ( message.equals("//mute") || message.equals("/mute") || !st.hasMoreTokens() ){
                im.SendAnswer(chat_id, call_name, "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)");
            }
            while ( st.hasMoreTokens() ){
                String token = st.nextToken();
                if ( token.contains("д") && time == null ){
                    time = Calendar.getInstance();
                    time.add(Calendar.DATE, Integer.parseInt(token.replace("д","")));
                } else if (token.contains("ч") && time == null ){
                    time = Calendar.getInstance();
                    time.add(Calendar.HOUR, Integer.parseInt(token.replace("ч","")));
                } else if ( token.contains("м") && time == null ) {
                    time = Calendar.getInstance();
                    time.add(Calendar.MINUTE, Integer.parseInt(token.replace("м","")));
                } else if ( time == null ){
                    time = Calendar.getInstance();
                    if ( Integer.parseInt(token.replace("c","")) == 0 ) {
                        time.add(Calendar.DATE, -2);
                    } else time.add(Calendar.SECOND, Integer.parseInt(token.replace("c","")));
                } else {
                    comment.append(comment.isEmpty() ? "" : " ").append(token);
                }
            }
            if ( time == null ) {
                im.SendAnswer(chat_id, call_name, "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)");
                return false;
            }
            user_id = new StringBuilder(temp.getReplyToMessage().getSenderChat() == null ? String.valueOf(temp.getReplyToMessage().getFrom().getId()) : String.valueOf(temp.getReplyToMessage().getSenderChat().getId()));
        }
        return callMute(chat_id, user_id.toString(), call_name, mute_name, comment.toString(), time, set_mute, update);
    }

    public boolean callMute(String chat_id, String user_id, String call_name, String mute_name, String comment, @Nullable Calendar time, boolean set_mute, Update bot_comment){
        // permission constructor
        ChatPermissions mute_perm = new ChatPermissions();
        mute_perm.setCanSendMessages(!set_mute);
        mute_perm.setCanSendOtherMessages(!set_mute);
        mute_perm.setCanSendMediaMessages(!set_mute);
        // mute constructor
        RestrictChatMember mute = new RestrictChatMember();
        mute.setChatId(chat_id);
        mute.setUserId(Long.parseLong(user_id));
        mute.setUntilDate( time != null ? Math.toIntExact(time.getTimeInMillis()/1000) : 0 );
        mute.setPermissions(mute_perm);
        // set mute in DB
        userInfoHolder.getInstance().updateRestrictionName(chat_id, user_id, String.valueOf(mute.getUntilDate()));
        userInfoHolder.getInstance().updateRestrictionType(chat_id, user_id, set_mute ? 1 : 0);
        userInfoHolder.getInstance().storeMe(chat_id, user_id);
        if ( set_mute ) {
            new dbRestrictionHandler().AddRestrictionUserToTable(Long.parseLong(chat_id), Long.parseLong(user_id), call_name, mute.getUntilDate(), "mute", comment, bot_comment);
            im.SendAnswer(im.YummyReChat, call_name, "Попытка замутить пользователя " + mute_name + " до " + time.getTime() + ".");
        } else {
            im.SendAnswer(im.YummyReChat, call_name, "Попытка раз-замутить пользователя " + mute_name + ".");
        }
        // trying to execute
        try {
            if ( Long.parseLong(user_id) > 0 ) {
                im.execute(mute);
            }
            return true;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
