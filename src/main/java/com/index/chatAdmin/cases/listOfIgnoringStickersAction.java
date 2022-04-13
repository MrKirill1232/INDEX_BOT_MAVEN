package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.data.sql.stickerInfoHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class listOfIgnoringStickersAction {

    IndexMain im = new IndexMain();
    StringBuilder newmessage = new StringBuilder();
    stickerInfoHolder instance = stickerInfoHolder.getInstance();

    long chat_id;
    String name;
    String orig_message;

    public listOfIgnoringStickersAction (Update update, String sticker_url){

        boolean check = false;
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();
        orig_message = update.getMessage().getText();

        List<String> list_of_st = instance.getListOfStickers(String.valueOf(chat_id));
        newmessage.append("Список разрешённых стикеров:\n");
        if ( list_of_st.isEmpty() || ( !list_of_st.contains(sticker_url) && !sticker_url.equals("null") ) )
        {
            return;
        }
        int index = 0;
        for ( String st : list_of_st )
        {
            if ( check || sticker_url.equals("null") || st.equals(sticker_url) ){
                check = true;
                index++;
            }
            else {
                continue;
            }

            newmessage.append(index).append(". ").append(st).append(" - <a href=\"https://t.me/addstickers/").append(st).append("\">[*клик*]</a>\n");

            if (newmessage.length() >= 3500 || index >= 50 ){
                check = false;
                break;
            }
        }
        if ( index == 0 || check ){
            newmessage.append("Достигнут конец списка");
        }
        im.SendAnswer(chat_id, name, newmessage.toString(), "HTML", 0);
    }
}
