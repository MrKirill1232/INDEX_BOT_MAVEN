package com.index.data.sql;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.*;
import java.util.*;

public class restrictionFilesHolder {

    private static final String SELECT_TYPE_QUERY = "SELECT type FROM ban_files";
    private static final String SELECT_ID_QUERY = "SELECT id FROM ban_files WHERE type=?";
    private final static String DELETE_ALL_INFO_QUERY = "DELETE FROM ban_files";
    private final static String INSERT_URL_QUERY = "INSERT INTO ban_files (type, id) VALUES (?,?)";


    private final Map<String, List<String>> _template = new HashMap<>();

    protected restrictionFilesHolder() {
        load();
        StringBuilder st = new StringBuilder(": Загружены запрещенные файлы:\n");
        st.append("- Фото - ").append(_template.get("photo") != null ? _template.get("photo").size() : "").append("\n");
        st.append("- GIF - ").append(_template.get("gif") != null ? _template.get("gif").size() : "").append("\n");
        st.append("- Sticker - ").append(_template.get("sticker") != null ? _template.get("sticker").size() : "").append("\n");
        st.append("- Video - ").append(_template.get("video") != null ? _template.get("video").size() : "").append("\n");
        st.append("- ViaBOT - ").append(_template.get("viabot") != null ? _template.get("viabot").size() : "").append("\n");
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + st);
        System.out.println(getClass().getSimpleName() + st);
    }

    private void load() {
        _template.clear();
        List<String> type = new ArrayList<>();
        try (Connection con = dbMain.getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_TYPE_QUERY))
        {
            while (rset.next()){
                final String type_id = rset.getString("type");
                if ( !type.isEmpty() && type.contains(type_id) ){
                    continue;
                }
                type.add(type_id);
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ТИПов файлов с базы данных - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при получении ТИПов файлов с базы данных - " + e);
        }

        try (Connection con = dbMain.getConnection();
                 PreparedStatement st = con.prepareStatement(SELECT_ID_QUERY)) {
            for (String type_id : type) {
                st.setString(1, type_id);
                try (ResultSet rset = st.executeQuery()) {
                    List<String> temporary = new ArrayList<>();
                    while (rset.next()) {
                        temporary.add(rset.getString("id"));
                    }
                    _template.put(type_id, temporary);
                }
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
                    for ( String id : _template.get(type) ) {
                        st.setString(1, type);
                        st.setString(2, id);
                        st.execute();
                    }
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
    public List<String> getRestrictionViaBotIDs(){
        return _template.get("viabot");
    }

    public boolean addRestrictionGIFIDs(String id){
        List<String> temporary = _template.get("gif") == null ? new ArrayList<>() : _template.get("gif");
        if ( temporary.contains(id)) return false;
        temporary.add(id);
        _template.putIfAbsent("gif", temporary);
        _template.replace("gif", temporary);
        return _template.get("gif").contains(id);
    }
    public boolean addRestrictionStickerIDs(String id){
        List<String> temporary = _template.get("sticker") == null ? new ArrayList<>() : _template.get("sticker");
        if ( temporary.contains(id)) return false;
        temporary.add(id);
        _template.putIfAbsent("sticker", temporary);
        _template.replace("sticker", temporary);
        return _template.get("sticker").contains(id);
    }
    public boolean addRestrictionVideoIDs(String id){
        List<String> temporary = _template.get("video") == null ? new ArrayList<>() : _template.get("video");
        if ( temporary.contains(id)) return false;
        temporary.add(id);
        _template.putIfAbsent("video", temporary);
        _template.replace("video", temporary);
        return _template.get("video").contains(id);
    }
    public boolean addRestrictionPhotoIDs(String id){
        List<String> temporary = _template.get("photo") == null ? new ArrayList<>() : _template.get("photo");
        if ( temporary.contains(id)) return false;
        temporary.add(id);
        _template.putIfAbsent("photo", temporary);
        _template.replace("photo", temporary);
        return _template.get("photo").contains(id);
    }
    public boolean addRestrictionViaBotIDs(String id){
        List<String> temporary = _template.get("viabot") == null ? new ArrayList<>() : _template.get("viabot");
        if ( temporary.contains(id)) return false;
        temporary.add(id);
        _template.putIfAbsent("viabot", temporary);
        _template.replace("viabot", temporary);
        return _template.get("viabot").contains(id);
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
    public boolean isRestrictionViaBotIDs(String id){
        return _template.get("viabot") != null && _template.get("viabot").contains(id);
    }


    public static restrictionFilesHolder getInstance() {
        return restrictionFilesHolder.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final restrictionFilesHolder INSTANCE = new restrictionFilesHolder();
    }
}
