package com.index;

import com.index.dbHandler.dbMain;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Main {
    public static void main(String[] args) {
        dbMain.init();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            IndexMain im = new IndexMain();
            botsApi.registerBot(im);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}