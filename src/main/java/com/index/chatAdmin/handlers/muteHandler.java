package com.index.chatAdmin.handlers;

import com.index.IndexMain;
import com.index.dbHandler.handlers.dbRestrictionHandler;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.StringTokenizer;

public class muteHandler {

    IndexMain im = new IndexMain();
    dbRestrictionHandler rh = new dbRestrictionHandler();
    String newmessage;

    ChatPermissions UnGetPermissions (){
        ChatPermissions mute_perm = new ChatPermissions();
        mute_perm.setCanSendMessages(false);
        mute_perm.setCanSendOtherMessages(false);
        mute_perm.setCanSendMediaMessages(false);
        return mute_perm;
    }
    ChatPermissions GetPermissions (){
        ChatPermissions mute_perm = new ChatPermissions();
        mute_perm.setCanSendMessages(true);
        mute_perm.setCanSendOtherMessages(true);
        mute_perm.setCanSendMediaMessages(true);
        return mute_perm;
    }

    /**
     * Проверка, имеется ли стикер в список игнорируемых
     *
     * @param chat_id чат, для которого добавляется исключение;
     * @param user_id ид пользователя, для которого добавляется исключение;

     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code true} стикер присутствует в списке игнорируемых стикеров;<li>{@code false} отсутствует в списке игнорируемых стикеров;
     */

    public boolean tryMute ( Long chat_id, Long user_id, String name, String mute_name, long time, boolean set_mute ){
        im.SendAnswer(im.YummyReChat, name, "ВЫЗВАЛИ УСТАРЕВШИЙ МЕТОД МУТА, ИСПРАВЛЯЙ!");
        return tryMute(chat_id, user_id, name, mute_name, time, set_mute, "");
    }
    public boolean tryMute ( Long chat_id, Long user_id, String name, String mute_name, long time, boolean set_mute, String comment ){

        if ( user_id > 0 ){
            return setMuteUser(  chat_id,  user_id,  name,  mute_name,  time,  set_mute, comment );
        }
        else /*if ( user_id < 0 )*/{
            return setMuteGroup(  chat_id,  user_id,  name,  mute_name,  time,  set_mute, comment );
        }
    }
    public boolean setMuteUser( Long chat_id, Long user_id, String name, String mute_name, long time, boolean set_mute, String comment ){
        RestrictChatMember mute = new RestrictChatMember();
        mute.setChatId(String.valueOf(chat_id));
        mute.setUserId(user_id);
        mute.setUntilDate((int) time);
        if (set_mute){
            mute.setPermissions(UnGetPermissions());
        }
        else {
            mute.setPermissions(GetPermissions());
            rh.UpdateRestrictionTimeFromTable(chat_id, user_id, name, 0);
        }
        try {
            im.execute(mute);
            if (  rh.AddRestrictionUserToTable(chat_id, user_id, name, time, "mute", comment) ){
                im.SendAnswer(im.YummyReChat, "INDEX_BOT", "Мут для пользователя установлен на уровне БД;");
            }
            else {
                im.SendAnswer(im.YummyReChat, "INDEX_BOT", "Ошибка при установке мута на уровне БД;");
            }
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean setMuteGroup ( Long chat_id, Long user_id, String name, String mute_name, long time, boolean set_mute, String comment ){
        if ( set_mute ){
            if ( rh.GetRestrictionTimeFromTable(chat_id, user_id, name) > System.currentTimeMillis()/1000 ){
                return rh.UpdateRestrictionTimeFromTable(chat_id, user_id, name, time);
            }
            else {
                return rh.AddRestrictionUserToTable(chat_id, user_id, name, time, "mute", comment);
            }
        }
        else {
            if ( rh.CheckUserInRestrictionTable(chat_id, user_id, name)){
                return rh.UpdateRestrictionTimeFromTable(chat_id, user_id, name, 0);
            }
            else {
                return false;
            }
        }
    }
}
