package com.index.commandsHandler;

import com.index.IndexMain;
import com.index.chatAdmin.handlers.muteHandler;
import com.index.commandsHandler.cases.HelpCommand;
import com.index.commandsHandler.cases.StartCommand;
import com.index.commandsHandler.cases.yummyparser.jsonParser;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserCommansHandler {

    IndexMain im = new IndexMain();
    muteHandler mh = new muteHandler();
    String newmessage;

    long chat_id;
    String name;
    String orig_message;

    public UserCommansHandler(Update update) {


        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        if (orig_message.equals("/start")){
            new StartCommand(update);
        }
        else if (orig_message.startsWith("/yaUser")){
            new jsonParser(update);
        }
        else if (orig_message.startsWith("/help")){
            new HelpCommand(update);
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
            mh.tryMute(chat_id, update.getMessage().getFrom().getId(), "", "", System.currentTimeMillis()/1000-1200, true, "СПАМ");
        }
    }
}
