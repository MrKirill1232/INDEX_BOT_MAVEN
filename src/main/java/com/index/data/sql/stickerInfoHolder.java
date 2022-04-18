package com.index.data.sql;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class stickerInfoHolder {

    private final Map<String, List<String>> _ignoreStickers = new HashMap<>();
    // MICS
    private final static String SELECT_CHAT_QUERY = "SELECT chat_id FROM sticker_ignore";
    private final static String SELECT_URL_QUERY = "SELECT sticker_url FROM sticker_ignore WHERE chat_id=?";
    private final static String DELETE_URL_QUERY = "DELETE FROM sticker_ignore WHERE chat_id=?";
    private final static String INSERT_URL_QUERY = "INSERT INTO sticker_ignore (chat_id, sticker_url) VALUES (?,?)";

    private final static String DELETE_ALL_URL_QUERY = "DELETE FROM sticker_ignore";

    protected stickerInfoHolder(){
        load();
        System.out.println("Загружено информация о игнорируемых стикерах для " + _ignoreStickers.size() + " чатов;");
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "Загружено информация о игнорируемых стикерах для " + _ignoreStickers.size() + " чатов;");
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "Загружено количество игнорируемых стикеров для ями чата: " + _ignoreStickers.get(String.valueOf(new IndexMain().YummyChannel_CHAT)).size() + ";");
    }

    public void load(){
        _ignoreStickers.clear();
        List<String> chat_id_list = new ArrayList<>();
        try (Connection con = dbMain.getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_CHAT_QUERY))
        {
            while (rset.next()){
                final String chat_id_db = String.valueOf(rset.getLong("chat_id"));
                if ( !chat_id_list.isEmpty() && chat_id_list.contains(chat_id_db) ){
                    continue;
                }
                chat_id_list.add(chat_id_db);
            }
        }
        catch (Exception e)
        {
            System.out.println("Ошибка при получении ID чатов с базы данных - " + e);
        }

        try (Connection con = dbMain.getConnection();
             PreparedStatement st = con.prepareStatement(SELECT_URL_QUERY))
        {
            for ( String chat_id_index : chat_id_list ){
                List<String> stickers_url = new ArrayList<>();
                st.setString(1, chat_id_index);
                try (ResultSet rset = st.executeQuery()){
                    while (rset.next()){
                        stickers_url.add(rset.getString("sticker_url"));
                    }
                    _ignoreStickers.put(chat_id_index, stickers_url);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Ошибка при получении ссылок на стикеры из базы данных - " + e);
        }
    }

    public boolean checkStickerInList(String chat_id, String st_url){
        if ( _ignoreStickers.get(chat_id).isEmpty() ){
            return false;
        }
        return _ignoreStickers.get(chat_id).contains(st_url);
    }

    public boolean addNewSticker(String chat_id, String st_url, boolean save){
        if ( _ignoreStickers.get(chat_id).add(st_url) ){
            if ( save ){
                storeMe(chat_id);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public List<String> getListOfStickers(String chat_id){
        return _ignoreStickers.isEmpty() ? null : _ignoreStickers.get(chat_id);
    }

    public boolean deleteSticker(String chat_id, String st_url, boolean save){
        if ( _ignoreStickers.get(chat_id).remove(st_url) ) {
            if ( save ){
                storeMe(chat_id);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public String getTokenFromStickerUrl(String sticker_url){
        return (sticker_url.substring(25));
    }

    public void storeMe(String chat_id){
        try (Connection con = dbMain.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_URL_QUERY))
            {
                st.setString(1, chat_id);
                st.execute();
            }
            // Insert all url back.
            try (PreparedStatement st = con.prepareStatement(INSERT_URL_QUERY))
            {
                st.setString(1, chat_id);
                for (String url : _ignoreStickers.get(chat_id))
                {
                    st.setString(2, url);
                    st.addBatch();
                }
                st.executeBatch();
            }
            System.out.println("Игнорируемые стикеры для чата " + chat_id + " сохранены в базе");
            new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), "Игнорируемые стикеры для чата " + chat_id + " сохранены в базе;");

        }
        catch (SQLException e)
        {
            System.out.println("Ошибка при сохранении игнорируемых стикеров для чата " + chat_id + " - " + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "@MrKirill1232 Ошибка при сохранении игнорируемых стикеров для чата " + chat_id + " - " + e);
        }
    }

    public void storeAll(){
        try ( Connection con = dbMain.getConnection() ){
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_ALL_URL_QUERY))
            {
                st.execute();
            }
            for ( String chat_id : _ignoreStickers.keySet() ) {
                // Insert all url back.
                try (PreparedStatement st = con.prepareStatement(INSERT_URL_QUERY)) {
                    st.setString(1, chat_id);
                    for (String url : _ignoreStickers.get(chat_id)) {
                        st.setString(2, url);
                        st.addBatch();
                    }
                    st.executeBatch();
                }
            }
            System.out.println("Игнорируемые стикеры для всех чатов сохранены в базе");
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "Игнорируемые стикеры для всех чатов сохранены в базе;");
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка при сохранении игнорируемых стикеров для всех чатов - " + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "@MrKirill1232 Ошибка при сохранении игнорируемых стикеров для всех чатов - " + e);
        }
    }

    public static stickerInfoHolder getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final stickerInfoHolder INSTANCE = new stickerInfoHolder();
    }
}
