package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.data.sql.userInfoHolder;
import com.index.dbHandler.handlers.dbRestrictionHandler;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatSenderChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.StringTokenizer;

public class banAction {
    IndexMain im = new IndexMain();
    userInfoHolder user = userInfoHolder.getInstance();
    dbRestrictionHandler rh = new dbRestrictionHandler();
    Message message;
    String chat_id;
    String user_id;
    String ban_user_id;
    String update_name;
    String ban_name;
    Calendar ban_time;
    StringBuilder ban_comment;

    public boolean setVariables ( Update update, String type ) {
        message = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage() : null;
        if ( message == null ) { return false; }
        String original_message = message.getText();
        if ( message.getReplyToMessage() != null ) {
            ban_user_id = String.valueOf(message.getReplyToMessage().getFrom().getId());
            ban_name = message.getReplyToMessage() == null ? null : message.getReplyToMessage().getSenderChat() == null ? message.getReplyToMessage().getFrom().getFirstName() : message.getReplyToMessage().getSenderChat().getTitle();
        }
        chat_id = String.valueOf(message.getChatId());
        user_id = message.getSenderChat() == null ? message.getFrom().getFirstName() : message.getSenderChat().getTitle();
        update_name = message.getSenderChat() == null ? message.getFrom().getFirstName() : message.getSenderChat().getTitle();
        StringTokenizer st = new StringTokenizer(original_message);
        st.nextToken();
        if ( ban_user_id.isEmpty() ) {
            ban_user_id = st.nextToken();
            for (char c : ban_user_id.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }
        while ( st.hasMoreTokens() ){
            String token = st.nextToken();
            try {
                if ((token.contains("д") && ban_time == null) && type.equals("ban")) {
                    ban_time = Calendar.getInstance();
                    ban_time.add(Calendar.DATE, Integer.parseInt(token.replace("д", "")));
                } else if ((token.contains("ч") && ban_time == null) && type.equals("ban")) {
                    ban_time = Calendar.getInstance();
                    ban_time.add(Calendar.HOUR, Integer.parseInt(token.replace("ч", "")));
                } else if ((token.contains("м") && ban_time == null) && type.equals("ban")) {
                    ban_time = Calendar.getInstance();
                    ban_time.add(Calendar.MINUTE, Integer.parseInt(token.replace("м", "")));
                } else if ((token.contains("c") && ban_time == null) && type.equals("ban")) {
                    ban_time = Calendar.getInstance();
                    if (Integer.parseInt(token.replace("c", "")) == 0) {
                        ban_time.add(Calendar.DATE, -2);
                    } else ban_time.add(Calendar.SECOND, Integer.parseInt(token.replace("c", "")));
                } else {
                    ban_comment.append(token);
                }
            }
            catch (NumberFormatException e){
                ban_comment.append(token);
            }
        }
        if ( ban_name == null || ban_name.isEmpty() ) {
            ban_name = userInfoHolder.getInstance().getTemplate(chat_id, user_id).get_user_name();
        }
        return true;
    }

    public boolean banAction ( Update update, String type ) {
        switch (type.toLowerCase()){
            case "unban":{
                setVariables(update, "kick");
                return callUnBANAction();
            }
            case "kick":
            {
                setVariables(update, "kick");
                return callKickAction();
            }
            case "ban":
            {
                setVariables(update, "ban");
                return callBANAction();
            }
            default: return false;
        }
    }
    protected boolean callUnBANAction ( ) {
        return false;
    }
    protected boolean callBANAction ( ) {
        if ( Long.parseLong(user_id) > 0 ) {
            BanChatMember ban;
            if ( ban_time != null ){
                ban = new BanChatMember(chat_id, Long.parseLong(ban_user_id), Math.toIntExact(ban_time.getTimeInMillis() / 1000), false);
            }
            else {
                ban = new BanChatMember(chat_id, Long.parseLong(ban_user_id));
            }
            try {
                im.execute(ban);
                im.SendAnswer(im.YummyReChat, "Index", "Забанил группу в чате " + chat_id + " " + ban_user_id );
                user.updateRestrictionType(chat_id, ban_user_id, 2);
                user.updateRestrictionName(chat_id, ban_user_id, String.valueOf(ban_time != null ? ban_time.getTimeInMillis() / 1000 : 0));
                rh.AddRestrictionUserToTable(Long.parseLong(chat_id), Long.parseLong(ban_user_id), update_name, ban_time != null ? ban_time.getTimeInMillis() / 1000 : 0, "ban", ban_comment.toString(), null);
                return true;
            } catch (TelegramApiException e) {
                im.SendAnswer(im.YummyReChat, "Index", "Ошибка при попытке выполнить \"БАН\" для группы :( " + chat_id + " " + ban_user_id + "\n" + e);
                return false;
            }
        } else {
            BanChatSenderChat ban;
            if ( ban_time != null ){
                ban = new BanChatSenderChat(chat_id, Long.parseLong(ban_user_id), Math.toIntExact(ban_time.getTimeInMillis() / 1000));
            }
            else {
                ban = new BanChatSenderChat(chat_id, Long.parseLong(ban_user_id));
            }
            try {
                im.execute(ban);
                im.SendAnswer(im.YummyReChat, "Index", "Забанил пользователя в чате " + chat_id + " " + ban_user_id );
                user.updateRestrictionType(chat_id, ban_user_id, 2);
                user.updateRestrictionName(chat_id, ban_user_id, String.valueOf(ban_time != null ? ban_time.getTimeInMillis() / 1000 : 0));
                rh.AddRestrictionUserToTable(Long.parseLong(chat_id), Long.parseLong(ban_user_id), update_name, ban_time != null ? ban_time.getTimeInMillis() / 1000 : 0, "ban", ban_comment.toString(), null);
                return true;
            } catch (TelegramApiException e) {
                im.SendAnswer(im.YummyReChat, "Index", "Ошибка при попытке выполнить \"БАН\" пользователя :( " + chat_id + " " + ban_user_id + "\n" + e);
                return false;
            }
        }
    }
    protected boolean callKickAction ( ) {
        if ( Long.parseLong(user_id) > 0 ) {
            BanChatMember ban = new BanChatMember(chat_id, Long.parseLong(ban_user_id), Math.toIntExact((System.currentTimeMillis()/1000)+10), false);
            try {
                im.execute(ban);
                im.SendAnswer(im.YummyReChat, "Index", "Кикнул пользователя с чата " + chat_id + " " + ban_user_id );
                return true;
            } catch (TelegramApiException e) {
                im.SendAnswer(im.YummyReChat, "Index", "Ошибка при попытке выполнить \"КИК\" пользователя :( \n" + e);
                return false;
            }
        }
        else {
            BanChatSenderChat ban = new BanChatSenderChat(chat_id, Long.parseLong(ban_user_id), Math.toIntExact((System.currentTimeMillis()/1000)+10));
            try {
                im.execute(ban);
                im.SendAnswer(im.YummyReChat, "Index", "Кикнул группу с чата " + chat_id + " " + ban_user_id );
                return true;
            } catch (TelegramApiException e) {
                im.SendAnswer(im.YummyReChat, "Index", "Ошибка при попытке выполнить \"КИК\" группы ( а на... зачем ? ) :( " + chat_id + " " + ban_user_id + "\n" + e);
                return false;
            }
        }
    }
}
