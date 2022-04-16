package com.index.future;

// ничего кроме как делать проверку пи получении апдейта я не придумал =_=

import com.index.IndexMain;
import com.index.data.sql.restrictionFilesHolder;
import com.index.data.sql.stickerInfoHolder;
import com.index.data.sql.userInfoHolder;

import java.util.Calendar;

public class FutureAction {

    private long next_save;
    protected FutureAction(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,10);
        next_save = calendar.getTimeInMillis();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", "Следующее сохранение запланировано " + calendar.getTime() + ";");
    }

    public void save(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,10);
        next_save = calendar.getTimeInMillis();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", "Следующее сохранение запланировано " + calendar.getTime() + ";");
        stickerInfoHolder.getInstance().storeAll();
        userInfoHolder.getInstance().storeAll();
        restrictionFilesHolder.getInstance().storeMe();
    }

    public long getNextSave(){
        return next_save;
    }

    public static FutureAction getInstance() {
        return FutureAction.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final FutureAction INSTANCE = new FutureAction();
    }
}
