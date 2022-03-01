package com.index.commandsHandler.cases;

import com.index.IndexMain;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand {

    IndexMain im = new IndexMain();
    String newmessage;

    String name;
    long chat_id;



    public HelpCommand (Update update){
        name = update.getMessage().getFrom().getFirstName();
        chat_id = update.getMessage().getChatId();

        newmessage = "Список комманд, доступных для использования;";
        newmessage += "\n";

        // user commands //
        newmessage += "Пользовательские комманды:";
        newmessage += "\n";
        newmessage += "× <code>/help</code> - выводит этот список;";
        newmessage += "\n";
        newmessage += "× <code>/start</code> - что-то выводит;";
        newmessage += "\n";
        newmessage += "× <code>/yaUser</code> - не работает;";
        newmessage += "\n";

        // admin commands //

        newmessage += "Комманды для администраторов:";
        newmessage += "\n";
        newmessage += "× <code>//delay</code> - не рабочая комманды, должна была устанавливать задержку;";
        newmessage += "\n";
        newmessage += "× <code>//GetChatID</code> - кидает администратору в ЛС идентификационный номер чата;";
        newmessage += "\n";
        newmessage += "× <code>//AddIgnoringStickers</code> - добавляет стикер-пак, на который ответили, в список исключений;";
        newmessage += "\n";
        newmessage += "× <code>//RemoveIgnoringStickers</code> - отправляет запрос администратору на удаление стикер-пака из списка исключений;";
        newmessage += "\n";
        newmessage += "× <code>//ListOfIgnoringStickers</code> - присылает список стикер-паков, которые находятся в списке исключений;";
        newmessage += "\n";
        newmessage += "× <code>//mute</code> - время в секунда - выдает пользователю запрет на отправку сообщений. Если значение меньше 10 - навсегда;";
        newmessage += "\n";
        newmessage += "× <code>//unmute</code> - возвращает пользователю возможность отправки сообщений, стикеров, файлов;";
        newmessage += "\n";
        newmessage += "× <code>//getEntities</code> - получает список переменных в сообщении;";
        newmessage += "\n";
        newmessage += "× <code>//pin</code> - закрепляет сообщение, на которое ответили;";
        newmessage += "\n";
        newmessage += "× <code>//getFileID</code> - получает ИД файла, на который ответили;";
        newmessage += "\n";
        newmessage += "× <code>//getUserID</code> - получает ИД пользователя, с ответа;";
        newmessage += "\n";
        newmessage += "× <code>//dbStatus</code> - возвращает статус подключения к Базе Данных;";
        newmessage += "\n";

        im.SendAnswer(chat_id, name, newmessage, "HTML");

    }
}
