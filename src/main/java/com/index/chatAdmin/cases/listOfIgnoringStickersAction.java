package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.dbHandler.handlers.dbStickerHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class listOfIgnoringStickersAction {

    IndexMain im = new IndexMain();
    String newmessage;
    dbStickerHandler dbStickerHandler = new dbStickerHandler();

    long chat_id;
    String name;
    String orig_message;

    public listOfIgnoringStickersAction (Update update, String sticker_url){

        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        newmessage = dbStickerHandler.getStickerURL(sticker_url, chat_id, name);
        /*try {

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        im.SendAnswer(chat_id, name, newmessage, "HTML", 0);
    }
}
