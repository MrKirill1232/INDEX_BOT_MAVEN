package com.index.data.files;

import com.index.data.util.PropertiesParser;

public class Config {

    public static final String BOT_MAIN_CONFIG = "./config/IndexBot.ini";

    public static String BOT_NAME;
    public static String BOT_TOKEN;
    public static String DB_NAME;
    public static String DB_USER;
    public static long DB_PASS;
    public static int DB_MAX_CON;

    public static void load(){
        final PropertiesParser botConfig = new PropertiesParser(BOT_MAIN_CONFIG);
        BOT_NAME = botConfig.getString("BotName", null);
        BOT_TOKEN = botConfig.getString("TelegramToken", null);
        DB_NAME = botConfig.getString("DataBaseName", null);
        DB_USER = botConfig.getString("DataBaseUser", null);
        DB_PASS = botConfig.getLong("DataBasePass", 0);
        DB_MAX_CON = botConfig.getInt("DataBaseMaxConnection", 0);
    }
}

