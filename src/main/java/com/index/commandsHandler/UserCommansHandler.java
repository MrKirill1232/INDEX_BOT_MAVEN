package com.index.commandsHandler;

import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.commandsHandler.cases.HelpCommand;
import com.index.commandsHandler.cases.StartCommand;
import com.index.commandsHandler.cases.yummyparser.jsonParser;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserCommansHandler {

    IndexMain im = new IndexMain();
    muteHandler mh = new muteHandler();
    String newmessage;

    String chat_id;
    String original_message;
    String user_id;
    String update_name;

    List<String> user_moderation = new ArrayList<>();

    private void setVariables(Update update){
        Message temp = update.getMessage();
        original_message = temp.getText() != null ? temp.getText() : temp.getCaption() != null ? temp.getCaption() : null;
        user_id = temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId());
        update_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        chat_id = String.valueOf(temp.getChatId());
        user_moderation.add("499220683");       // MrKirill1232
        user_moderation.add("610980102");      // Исаия
        user_moderation.add("1093703997");      // Altair
        user_moderation.add("-1001454322922L"); // YummyChannel_CHAT
    }

    public UserCommansHandler(Update update) {
        setVariables(update);
        if (original_message.equals("/start")){
            new StartCommand(update);
        }
        else if (original_message.startsWith("/help")){
            new HelpCommand(update);
        }
        else if (original_message.startsWith("/mute")){
            new muteHandler().callMute(update);
        }
        /*else if (orig_message.startsWith("/yaUser")){
            new jsonParser(update);
        }

        else if ( orig_message.startsWith("/banticket")){
            new BanTicketHandler(update);
        }
        else if (
                orig_message.contains("Isaia") ||
                orig_message.contains("Isaiya") ||
                orig_message.contains("Isaya") ||
                orig_message.contains("Iseya") ||
                orig_message.contains("Iseia") ||
                orig_message.contains("Iseiya")
        ){
            update.getMessage().getMessageId();
            //mh.tryMute(chat_id, update.getMessage().getFrom().getId(), "", "", System.currentTimeMillis()/1000-1200, true, "СПАМ");
        }*/
    }
}
