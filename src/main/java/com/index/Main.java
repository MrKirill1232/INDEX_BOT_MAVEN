package com.index;

import com.index.data.files.Config;
import com.index.data.sql.restrictionFilesHolder;
import com.index.data.sql.stickerInfoHolder;
import com.index.data.sql.userInfoHolder;
import com.index.dbHandler.dbMain;
import com.index.future.FutureAction;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        Config.load();
        dbMain.init();
        FutureAction.getInstance();
        stickerInfoHolder.getInstance();
        userInfoHolder.getInstance();
        restrictionFilesHolder.getInstance();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            IndexMain im = new IndexMain();
            botsApi.registerBot(im);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}