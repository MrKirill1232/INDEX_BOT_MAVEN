package com.index;

import com.index.chatAdmin.AdminCommandHandler;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.chatModeration.ChatModerationHandler;
import com.index.chatModeration.moderators_chat.ModeratorChat;
import org.jetbrains.annotations.Async;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class IndexMain extends TelegramLongPollingBot {
    public Long chat = (-1001608680834L);
    public long YummyChannel_CHAT = -1001454322922L;
    public long YummyReChat = -1001362165830L;
    public boolean RESEND = true;

    @Override
    public void onUpdateReceived(Update update) {

        /*

        if (!update.getMessage().getChatId().equals(chat))
        {
            return;
        }*/
        /*
        BanChatMember BAN = new BanChatMember();
        BAN.setChatId(String.valueOf(update.getMessage().getChatId()));
        BAN.setUserId(update.getMessage().getFrom().getId());
        BAN.setRevokeMessages(true);
        try {
            execute(BAN);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        */

        if (update.hasMessage() && update.getMessage().hasText()) {
            if ( update.getMessage().getReplyToMessage()!=null && (update.getMessage().getReplyToMessage().getMessageId()==407281
                    ||
            update.getMessage().getReplyToMessage().getMessageId() == 407297
                    ||
            update.getMessage().getReplyToMessage().getMessageId() == 407306) && update.getMessage().getFrom().getId() != 499220683
            ){
                muteHandler mh = new muteHandler();
                mh.tryMute(update.getMessage().getChatId(),update.getMessage().getFrom().getId(), "INDEX_BOT", update.getMessage().getFrom().getFirstName(),
                        System.currentTimeMillis()/1000+92000, true, "ПОЛИТИЧЕСКИЙ СПАМ");
                deleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
            }

            String message_text = update.getMessage().getText();
            if ( false ) {
                if (message_text.contains("MrKirill1232") ||
                    message_text.contains("mrkirill1232") ||
                        update.getMessage().getReplyToMessage()!=null && update.getMessage().getReplyToMessage().getFrom().getId() == 499220683) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
                }
            }
            // Set variables
            if (message_text.startsWith("//")){
                new AdminCommandHandler(update);
                if ( update.getMessage().getChatId() == YummyChannel_CHAT){
                    if (RESEND) {
                        new ModeratorChat(update, "Forwarding");
                    }
                }
            }
            /*else if (message_text.startsWith("/"))
            {
                if ( update.getMessage().getChatId() == YummyChannel_CHAT){
                    if (RESEND) {
                        new ModeratorChat(update, "Forwarding");
                    }
                }
                new UserCommansHandler(update);
            }*/
            else {
                new ChatModerationHandler(update);
            }
        }
        else {
            if ( false ) {
                if (update.hasMessage() && update.getMessage().getReplyToMessage() != null && update.getMessage().getReplyToMessage().getFrom().getId() == 499220683) {
                    new ModeratorChat(update, "DeleteMe");
                    deleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
                }
            }
            new ChatModerationHandler(update);
        }
    }

    public void SendAnswer(long ChatID, String userName, String text){
        SendAnswer(ChatID, userName, text, "null", 0);
    }
    public void SendAnswer(long ChatID, String userName, String text, String syntaxis){
        SendAnswer(ChatID, userName, text, syntaxis, 0);
    }

    public void SendAnswer(long ChatID, String userName, String text, String syntaxis, int ReplyOn){
        SendMessage message = new SendMessage();
        if (syntaxis.equals("Markdown")){
            message.enableMarkdown(true);
        }
        else if (syntaxis.equals("HTML")){
            message.enableHtml(true);
        }
        else if (syntaxis.equals("null")){
            message.enableMarkdown(false);
            message.enableHtml(false);
            message.enableMarkdownV2(false);
        }
        message.setChatId(Long.toString(ChatID));
        message.setText(text);
        if (ReplyOn != (0)){
            message.setReplyToMessageId(ReplyOn);
        }
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(long ChatID, int messageId){
        DeleteMessage delete = new DeleteMessage(String.valueOf(ChatID), messageId);
        try {
            execute(delete);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

}

