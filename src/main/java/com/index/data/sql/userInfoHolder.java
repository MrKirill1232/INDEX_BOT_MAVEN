package com.index.data.sql;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.*;
import java.util.*;

public class userInfoHolder {

    private static final String SELECT_CHAT_QUERY = "SELECT chat_id FROM user_params";
    private static final String SELECT_INFO_QUERY = "SELECT * FROM user_params WHERE chat_id=?";
    private static final String INSERT_INFO_QUERY = "INSERT INTO user_params (chat_id, user_name, user_id, sticker_count, gif_count, next_message_reset, restriction_type, restriction_time, know_as) VALUES (?,?,?,?,?,?,?,?,?)";
    private final static String DELETE_INFO_QUERY = "DELETE FROM user_params WHERE chat_id=? AND user_id=?";
    private final static String DELETE_CHAT_INFO_QUERY = "DELETE FROM user_params WHERE chat_id=?";
    private final static String DELETE_ALL_INFO_QUERY = "DELETE FROM user_params";

    private Map<String, Map<String, userInfoTemplate>> _template = new HashMap<>();

    protected userInfoHolder(){
        load();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "Загружено информация пользователях для " + _template.size() + " чатов;");
    }

    private void load(){
        _template.clear();
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
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ID чатов с базы данных - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при получении ID чатов с базы данных - " + e);
        }

        try (Connection con = dbMain.getConnection();
             PreparedStatement st = con.prepareStatement(SELECT_INFO_QUERY)) {
            for (String chat_id_index : chat_id_list) {
                st.setString(1, chat_id_index);
                try (ResultSet rset = st.executeQuery()){
                    Map<String, userInfoTemplate> temporary = new HashMap<>();
                    while (rset.next()){
                        StringTokenizer know_as_tokens = new StringTokenizer(rset.getString("know_as"));
                        List<String> know_as = new ArrayList<>();
                        while ( know_as_tokens.hasMoreTokens() ){
                            know_as.add(know_as_tokens.nextToken());
                        }
                        know_as = null;
                        temporary.put(rset.getString("user_id"),
                                new userInfoTemplate(rset.getString("user_name"), rset.getInt("sticker_count"),
                                        rset.getInt("gif_count"), rset.getString("next_message_reset"),
                                        rset.getInt("restriction_type"), rset.getString("restriction_time"),
                                        know_as));
                    }
                    _template.put(chat_id_index, temporary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeChat(String chat_id){
        try (Connection con = dbMain.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_CHAT_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.execute();
            }
            // Insert all info back.
            try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY)) {
                st.setString(1, chat_id);
                for ( String user_id : _template.get(chat_id).keySet() ) {
                    userInfoTemplate template = _template.get(chat_id).get(user_id);
                    st.setString(2, template.get_user_name() == null ? "" : template.get_user_name());
                    st.setString(3, user_id);
                    st.setInt(4, template.get_sticker_count());
                    st.setInt(5, template.get_gif_count());
                    st.setString(6, template.get_next_message_reset());
                    st.setInt(7, template.get_restriction_type());
                    st.setString(8, template.get_restriction_time());
                    st.setString(9, template.get_know_as() == null ? "" : template.get_know_as().toString());
                    st.addBatch();
                }
                st.executeBatch();
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
            new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");

        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для чата " + chat_id + "\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для чата " + chat_id + " @MrKirill1232 \n" + e);
        }
    }

    public void storeAll(){
        try (Connection con = dbMain.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_ALL_INFO_QUERY))
            {
                st.execute();
            }
            // Insert all info back.
            for ( String chat_id : _template.keySet() ) {
                try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY)) {
                    st.setString(1, chat_id);
                    for (String user_id : _template.get(chat_id).keySet()) {
                        userInfoTemplate template = _template.get(chat_id).get(user_id);
                        st.setString(2, template.get_user_name() == null ? "" : template.get_user_name());
                        st.setString(3, user_id);
                        st.setInt(4, template.get_sticker_count());
                        st.setInt(5, template.get_gif_count());
                        st.setString(6, template.get_next_message_reset());
                        st.setInt(7, template.get_restriction_type());
                        st.setString(8, template.get_restriction_time());
                        st.setString(9, template.get_know_as() == null ? "" : template.get_know_as().toString());
                        st.addBatch();
                    }
                    st.executeBatch();
                }
                System.out.println(getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
                new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
            }
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для всех чатов \n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для всех чатов @MrKirill1232 \n" + e);
        }
    }

    public boolean storeMe(String chat_id, String user_id)
    {
        try (Connection con = dbMain.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.setString(2, user_id);
                st.execute();
            }
            userInfoTemplate template = getInstance().getTemplate(chat_id, user_id);
            // Insert all url back.
            try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.setString(2, template.get_user_name());
                st.setString(3, user_id);
                   st.setInt(4, template.get_sticker_count());
                   st.setInt(5, template.get_gif_count());
                st.setString(6, template.get_next_message_reset());
                   st.setInt(7, template.get_restriction_type());
                st.setString(8, template.get_restriction_time());
                st.setString(9, template.get_know_as() == null ? "" : template.get_know_as().toString());
                st.execute();
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о пользователе " + user_id + " " + template.get_user_name() + " для чата " + chat_id + " сохранена в базе");
            new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о пользователе " + user_id + " " + template.get_user_name() + " для чата " + chat_id + " сохранена в базе");
            return true;
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователе " + user_id + " " + getInstance().getTemplate(chat_id, user_id).get_user_name() + " для чата " + chat_id + "\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователе " + user_id + " " + getInstance().getTemplate(chat_id, user_id).get_user_name() + " для чата " + chat_id + " @MrKirill1232 \n" + e);
            return false;
        }
    }

    public boolean addNewUser(String chat_id, String user_name, String user_id, int sticker_count, int gif_count, String next_message_reset, int restriction_type, String restriction_time, List<String> know_as){
        if ( _template.get(chat_id) != null && _template.get(chat_id).containsKey(user_id) ){
            return false;
        }
        Map<String, userInfoTemplate> template = _template.get(chat_id) == null ? new HashMap<>() : _template.get(chat_id);
        template.put(
                user_id,
                new userInfoTemplate(
                        user_name,
                        sticker_count,
                        gif_count,
                        next_message_reset,
                        restriction_type,
                        restriction_time,
                        know_as));
        _template.putIfAbsent(chat_id, null);
        _template.replace(chat_id, template);
        return storeMe(chat_id, user_id);
        }

    public void updateExistUser(String chat_id, String user_name, String user_id, int sticker_count, int gif_count, String next_message_reset, int restriction_type, String restriction_time, List<String> know_as){
        //_template.replace(chat_id, Map.of(user_id, new userInfoTemplate(user_name, sticker_count, gif_count, next_message_reset, restriction_type, restriction_time, know_as)));
        Map<String, userInfoTemplate> temporary = _template.get(chat_id);
        temporary.replace(user_id, new userInfoTemplate(user_name, sticker_count, gif_count, next_message_reset, restriction_type, restriction_time, know_as));
        _template.replace(chat_id, temporary);
        //System.out.println("ну типо заменил");
        //new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), getClass().getSimpleName()  + ": ну типо заменил");
    }

    public void updateUserName(String chat_id, String user_id, String new_name){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        List<String> know_as = t.get_know_as();
        know_as.add(t.get_user_name());
        updateExistUser(chat_id, new_name, user_id, t.get_sticker_count(), t.get_gif_count(), t.get_next_message_reset(), t.get_restriction_type(), t.get_restriction_time(), know_as);
    }

    public void updateStickerCount(String chat_id, String user_id, int sticker_count){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, sticker_count, t.get_gif_count(), t.get_next_message_reset(), t.get_restriction_type(), t.get_restriction_time(), t.get_know_as());
    }

    public void updateGifCount(String chat_id, String user_id, int gif_count){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, t.get_sticker_count(), gif_count, t.get_next_message_reset(), t.get_restriction_type(), t.get_restriction_time(), t.get_know_as());
    }

    public void updateNextMessageReset(String chat_id, String user_id, String next_message_reset){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, t.get_sticker_count(), t.get_gif_count(), next_message_reset, t.get_restriction_type(), t.get_restriction_time(), t.get_know_as());
    }

    public void updateRestrictionType(String chat_id, String user_id, int restriction_type){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, t.get_sticker_count(), t.get_gif_count(), t.get_next_message_reset(), restriction_type, t.get_restriction_time(), t.get_know_as());
    }

    public void updateRestrictionName(String chat_id, String user_id, String restriction_time){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, t.get_sticker_count(), t.get_gif_count(), t.get_next_message_reset(), t.get_restriction_type(), restriction_time, t.get_know_as());
    }

    public void updateKnowAsList(String chat_id, String user_id, List<String> know_as){
        userInfoTemplate t = getInstance().getTemplate(chat_id, user_id);
        updateExistUser(chat_id, t.get_user_name(), user_id, t.get_sticker_count(), t.get_gif_count(), t.get_next_message_reset(), t.get_restriction_type(), t.get_restriction_time(), know_as);
    }

    public boolean checkUserInDB(String chat_id, String user_id){
        if ( _template.get(chat_id) == null ) {
            return false;
        }
        return _template.containsKey(chat_id) && _template.get(chat_id).containsKey(user_id);
    }

    public userInfoTemplate getTemplate(String chat_id, String user_id){
        return _template.get(chat_id).get(user_id);
    }

    public String getAllTemplate(){
        StringBuilder umu = new StringBuilder();
        for ( String chat : _template.keySet() ){
            umu.append("CHAT: ").append(chat).append("\n").append("---").append("\n");
            for ( String user : _template.get(chat).keySet() ){
                umu.append("USER - ").append(user).append("\n");
                umu.append(_template.get(chat).get(user).getAllInfo());
                umu.append("\n").append("---").append("\n");
            }
        }
        return umu.toString();
    }

    public static class userInfoTemplate
    {
        private final String _user_name;
        private final int _sticker_count;
        private final int _gif_count;
        private final String _next_message_reset;
        private final int _restriction_type;
        private final String _restriction_time;
        private final List<String> _know_as;

        public userInfoTemplate(String user_name, int sticker_count, int gif_count, String next_message_reset, int restriction_type, String restriction_time, List<String> know_as)
        {
            _user_name = user_name;
            _sticker_count = sticker_count;
            _gif_count = gif_count;
            _next_message_reset = next_message_reset;
            _restriction_type = restriction_type;
            _restriction_time = restriction_time;
            _know_as = know_as;
        }
        public String get_user_name(){
            return _user_name;
        }
        public int get_sticker_count(){
            return _sticker_count;
        }
        public int get_gif_count(){
            return _gif_count;
        }
        public String get_next_message_reset(){
            return _next_message_reset;
        }
        public int get_restriction_type(){
            return _restriction_type;
        }
        public String get_restriction_time(){
            return _restriction_time;
        }
        public List<String> get_know_as(){
            return _know_as;
        }
        public String getAllInfo(){
            StringBuilder out = new StringBuilder();
            out.append("User name - ").append(get_user_name()).append("\n");
            out.append("sticker count - ").append(get_sticker_count()).append("\n");
            out.append("gif count - ").append(get_gif_count()).append("\n");
            out.append("next message reset - ").append(get_next_message_reset()).append("\n");
            out.append("restriction type - ").append(get_restriction_type()).append("\n");
            out.append("restriction time - ").append(get_restriction_time()).append("\n");
            out.append("know as - ").append(get_know_as()).append("\n");
            return out.toString();
        }
    }

    public static userInfoHolder getInstance() {
        return userInfoHolder.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final userInfoHolder INSTANCE = new userInfoHolder();
    }
}
