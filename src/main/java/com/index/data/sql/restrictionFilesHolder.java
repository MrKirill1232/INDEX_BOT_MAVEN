package com.index.data.sql;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class restrictionFilesHolder {

    private static final String SELECT_CHAT_QUERY = "SELECT id FROM ban_files WHERE type=?";
    private final static String DELETE_ALL_INFO_QUERY = "DELETE FROM ban_files";

    private final static String INSERT_URL_QUERY = "INSERT INTO ban_files (type, id) VALUES (?,?)";


    private final Map<String, List<String>> _template = new HashMap<>();
    protected restrictionFilesHolder() {
        _template.put("video", List.of("1"));
        _template.put("gif", List.of("1"));
        _template.put("sticker", List.of("1"));
        _template.put("photo", List.of("1"));
        load();
        StringBuilder st = new StringBuilder(": Загружены запрещенные файлы:\n");
        st.append("- Фото - ").append(_template.get("photo") != null ? _template.get("photo").size() : "").append("\n");
        st.append("- GIF - ").append(_template.get("gif") != null ? _template.get("gif").size() : "").append("\n");
        st.append("- Sticker - ").append(_template.get("sticker") != null ? _template.get("sticker").size() : "").append("\n");
        st.append("- Video - ").append(_template.get("video") != null ? _template.get("video").size() : "").append("\n");
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + st);
        System.out.println(getClass().getSimpleName() + st);
    }

    private void load(){
        _template.clear();
        try (Connection con = dbMain.getConnection();
            PreparedStatement st = con.prepareStatement(SELECT_CHAT_QUERY)) {
            for (String type : _template.keySet()) {
                st.setString(1, type);
                List<String> temporary = new ArrayList<>();
                try (ResultSet rset = st.executeQuery()) {
                    while (rset.next()) {
                        temporary.add(rset.getString("id"));
                        System.out.println(temporary);
                    }
                }
                _template.replace(type, temporary);
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
        }
    }

    public void storeMe(){
        try (Connection con = dbMain.getConnection()) {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_ALL_INFO_QUERY)) {
                st.execute();
            }
            try (PreparedStatement st = con.prepareStatement(INSERT_URL_QUERY)) {
                for ( String type : _template.keySet() ){
                    st.setString(1, type);
                    for ( String id : _template.get(type) ) {
                        st.setString(2, id);
                        st.addBatch();
                    }
                    st.executeBatch();
                }
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе");
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе");
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах @MrKirill1232 \n" + e);
        }
    }

    public Map<String, List<String>> getRestrictionFilesIDs(){
        return _template;
    }

    public List<String> getRestrictionGIFIDs(){
        return _template.get("gif");
    }
    public List<String> getRestrictionStickerIDs(){
        return _template.get("sticker");
    }
    public List<String> getRestrictionVideoIDs(){
        return _template.get("video");
    }
    public List<String> getRestrictionPhotoIDs(){
        return _template.get("photo");
    }

    public boolean addRestrictionGIFIDs(String id){
        _template.putIfAbsent("gif", null);
        if ( !_template.get("gif").contains(id) ) {
            List<String> temporary = _template.get("gif").isEmpty() ? new ArrayList<>() : _template.get("gif");
            temporary.add(id);
            _template.replace("gif", temporary);
            return _template.get("gif").contains(id);
        }
        return false;
    }
    public boolean addRestrictionStickerIDs(String id){
        _template.putIfAbsent("sticker", null);
        if ( !_template.get("sticker").contains(id) ) {
            List<String> temporary = _template.get("sticker").isEmpty() ? new ArrayList<>() : _template.get("sticker");
            temporary.add(id);
            _template.replace("sticker", temporary);
            return _template.get("sticker").contains(id);
        }
        return false;
    }
    public boolean addRestrictionVideoIDs(String id){
        _template.putIfAbsent("video", null);
        if ( !_template.get("video").contains(id) ) {
            List<String> temporary = _template.get("video").isEmpty() ? new ArrayList<>() : _template.get("video");
            temporary.add(id);
            _template.replace("video", temporary);
            return _template.get("video").contains(id);
        }
        return false;
    }
    public boolean addRestrictionPhotoIDs(String id){
        _template.putIfAbsent("photo", null);
        if ( !_template.get("photo").contains(id) ) {
            List<String> temporary = _template.get("photo").isEmpty() ? new ArrayList<>() : _template.get("photo");
            temporary.add(id);
            _template.replace("photo", temporary);
            return _template.get("photo").contains(id);
        }
        return false;
    }

    public boolean isRestrictionGIFIDs(String id){
        return _template.get("gif") != null && _template.get("gif").contains(id);
    }
    public boolean isRestrictionStickerIDs(String id){
        return _template.get("sticker") != null && _template.get("sticker").contains(id);
    }
    public boolean isRestrictionVideoIDs(String id){
        return _template.get("video") != null && _template.get("video").contains(id);
    }
    public boolean isRestrictionPhotoIDs(String id){
        return _template.get("photo") != null && _template.get("photo").contains(id);
    }


    public static restrictionFilesHolder getInstance() {
        return restrictionFilesHolder.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final restrictionFilesHolder INSTANCE = new restrictionFilesHolder();
    }
}
