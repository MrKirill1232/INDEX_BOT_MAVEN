package com.index.commandsHandler.cases.yummyparser;

import com.index.IndexMain;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class jsonParser {
    IndexMain im = new IndexMain();
    String newmessage;

    long chat_id;
    String name;
    String orig_message;

    boolean search_success;
    String ya_user_name;
    long ya_user_id;
    String ya_user_avatar;

    public jsonParser (Update update) {
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();
        JSONParser parser = new JSONParser();
        File file = new File("response_1642373427959.json");
        try (FileReader reader = new FileReader("response_1642373427959.json")) {

            JSONObject rootJSONobject = (JSONObject) parser.parse(reader);
            JSONArray dataJsonArray = (JSONArray) rootJSONobject.get("data");

            search_success = (boolean) rootJSONobject.get("ok");
            if (true){
                for (Object it : dataJsonArray) {
                    JSONObject dataJsobObject = (JSONObject) it;

                    ya_user_id = (Long) dataJsobObject.get("id");
                    ya_user_name = (String) dataJsobObject.get("name");
                    ya_user_avatar = (String) dataJsobObject.get("avatar");
                }
            }
        } catch (Exception e) {
            System.out.println("eror" + e.toString());
        }
        newmessage = file.exists() + "\n" + search_success + "\n" + ya_user_id + "\n" + ya_user_name + "\n" + ya_user_avatar;
        im.SendAnswer(chat_id, name, newmessage);
    }
}
