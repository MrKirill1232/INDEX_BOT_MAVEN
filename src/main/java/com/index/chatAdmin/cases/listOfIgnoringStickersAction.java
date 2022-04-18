package com.index.chatAdmin.cases;

import com.index.IndexMain;
import com.index.data.sql.stickerInfoHolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class listOfIgnoringStickersAction {

    IndexMain im = new IndexMain();
    StringBuilder newmessage = new StringBuilder();
    stickerInfoHolder instance = stickerInfoHolder.getInstance();

    long chat_id;
    String name;
    String orig_message;

    public listOfIgnoringStickersAction (Update update, String sticker_url){

        Message temp = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery().getMessage();
        String first_url = "null";
        String last_url = "null";
        boolean check = false;
        name = temp.getFrom().getFirstName();
        chat_id = temp.getChatId();
        orig_message = temp.getText();

        List<String> list_of_st = instance.getListOfStickers(String.valueOf(chat_id));
        newmessage.append("Список разрешённых стикеров:\n");
        if ( list_of_st.isEmpty() || ( !list_of_st.contains(sticker_url) && !sticker_url.equals("null") ) )
        {
            return;
        }
        int index = 0;
        int show_index = 0;
        for ( String st : list_of_st )
        {
            show_index++;
            if ( check || sticker_url.equals("null") || st.equals(sticker_url) ){
                if ( index == 0 ) first_url = st;
                check = true;
                index++;
            }
            else {
                continue;
            }
            last_url = st;
            newmessage.append(show_index).append(". ").append(st).append(" - <a href=\"https://t.me/addstickers/").append(st).append("\">[*клик*]</a>\n");

            if (newmessage.length() >= 3500 || index >= 50 ){
                check = false;
                break;
            }
        }
        if ( index == 0 || check ){
            newmessage.append("Достигнут конец списка");
        }
        //im.SendAnswer(chat_id, name, newmessage.toString(), "HTML", 0);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        check = false;
        for ( int index_b = 0; index_b < 2; index_b++ ) {
            switch (index_b) {
                case 0: {
                    if ( sticker_url.equals("null") ) {
                        continue;
                    }
                    else {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setText("Начало");
                        button.setCallbackData("sticker_list_" + "null");
                        rowInline.add(button);
                    }
                    break;
                }
                case 1: {
                    if ( newmessage.toString().toLowerCase().contains("достигнут конец списка") ) {
                        continue;
                    }
                    else {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setText("След.");
                        button.setCallbackData("sticker_list_" + last_url);
                        rowInline.add(button);
                    }
                }
            }
        }
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        if ( !update.hasCallbackQuery() ) {
            SendMessage message = new SendMessage();
            message.setReplyMarkup(markupInline);
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(newmessage.toString());
            message.enableHtml(true);
            try {
                im.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            EditMessageText message = new EditMessageText();
            message.setMessageId(temp.getMessageId());
            message.setText(newmessage.toString());
            message.setChatId(String.valueOf(temp.getChatId()));
            message.setReplyMarkup(markupInline);
            message.enableHtml(true);
            try {
                im.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
