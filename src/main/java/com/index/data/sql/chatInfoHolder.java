package com.index.data.sql;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.*;
import java.util.*;

public class chatInfoHolder {

    private static final String SELECT_QUERY = "SELECT * FROM chat_info";
    private static final String DELETE_QUERY_BY_CHAT = "DELETE FROM chat_info WHERE chat_id=?";
    private static final String INSERT_QUERY = "INSERT INTO chat_info " +
            "(chat_id,chat_name,chat_url,need_to_store,admins_list,user_moderation,can_send_stickers,can_send_gifs,max_sticker_count,max_gif_count,reset_time)" +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

    private final Map<String, chatInfoTemplate> _template = new HashMap<>();
    public chatInfoHolder () {
        load();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Загружена информация о " + _template.size() + " чатах;");
    }

    public boolean checkChatInDB ( String chat_id ) {
        return _template.containsKey(chat_id);
    }
    public boolean addNewChatInfo ( Update update ) {
        Message message = update.getMessage() != null ?
                update.getMessage() :
                update.getCallbackQuery() != null ?
                        update.getCallbackQuery().getMessage() :
                        null;
        if ( message == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении информации о чате в базу - не нашел message.\n" + update);
            return false;
        }
        if ( message.getChat() == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении информации о чате в базу - не нашел chat.\n" + update);
            return false;
        }
        if ( _template.get(String.valueOf(message.getChatId())) != null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении информации о чате в базу - скорее всего чат уже есть в базе.\n" + update);
            return false;
        }
        Chat chat = message.getChat();
        if ( chat == null ) {
            return false;
        }
        else if ( chat.getId() > 0  ) {
            _template.put(String.valueOf(message.getFrom().getId()), new chatInfoTemplate(
                    message.getChat().getUserName() != null ? message.getFrom().getUserName() : message.getFrom().getFirstName(),
                    String.valueOf(message.getFrom().getId()), false, null, null,
                    true, true, 9999, 9999, 0));
        } else {
            //ChatPermissions perm = chat.getPermissions();
            _template.put(String.valueOf(message.getChatId()), new chatInfoTemplate(
                    chat.getTitle(), chat.getInviteLink(), false, null, null,
                    true, true,9999, 9999, 0));
        }
        return _template.get(String.valueOf(chat.getId())) != null && storeMe(String.valueOf(message.getChatId()));
    }

    private boolean updateChatInfo ( String chat_id, String chat_name, String chat_url, boolean need_to_store, List<String> admins_list,
                                     List<String> user_moderation, boolean can_send_stickers, boolean can_send_gifs, int max_sticker_count,
                                     int max_gif_count, long reset_time) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        chatInfoTemplate template_old = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении информации о чате - не могу найти чат в базе.");
            return false;
        }
        _template.replace(chat_id, new chatInfoTemplate(chat_name, chat_url, need_to_store, admins_list,
                                    user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time));
        if ( storeMe(chat_id) ){
            return true;
        }
        else {
            _template.replace(chat_id, template_old);
            return false;
        }
    }

    public boolean updateChatName ( String chat_id, String chat_name ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении имени для чата - не могу найти чат в базе.");
            return false;
        }
        //String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatUrl ( String chat_id, String chat_url ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении ссылки для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        //String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatParam_NeedToStore ( String chat_id, boolean need_to_store ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"need_to_store\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        //boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatAdminsList ( String chat_id, List<String> admins_list ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении админов для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        //List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatAddAdminToList ( String chat_id, String user_id ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении админа для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        if ( admins_list.contains(user_id) ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении админа для чата - похоже он уже в списке.");
            return false;
        }
        admins_list.add(user_id);
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatUserModerationList ( String chat_id, List<String> user_moderation ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении админов для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        //List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatAddModerationUserToList ( String chat_id, String user_id ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении админа для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        if ( user_moderation.contains(user_id) ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при добавлении пользователя-модератора для чата - похоже он уже в списке.");
            return false;
        }
        user_moderation.add(user_id);
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    public boolean updateChatChatParam_CanSendStickers ( String chat_id, boolean can_send_stickers ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"can_send_stickers\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        //boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }
    public boolean updateChatChatParam_CanSendGif ( String chat_id, boolean can_send_gifs ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"can_send_gifs\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        //boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }
    public boolean updateChatChatParam_MaxStickerCount ( String chat_id, int max_sticker_count ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"max_sticker_count\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        //int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }
    public boolean updateChatChatParam_MaxGifCount ( String chat_id, int max_gif_count ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"max_gif_count\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        //int max_gif_count = template.get_max_gif_count();
        long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }
    public boolean updateChatChatParam_ResetTime ( String chat_id, long reset_time ) {
        chatInfoTemplate template = _template.get(chat_id) != null ? _template.get(chat_id) : null;
        if ( template == null ) {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": ошибка при обновлении параметра \"reset_time\" для чата - не могу найти чат в базе.");
            return false;
        }
        String chat_name = template.get_chat_name();
        String chat_url = template.get_chat_url();
        boolean need_to_store = template.get_need_to_store();
        List<String> admins_list = template.get_admins_list();
        List<String> user_moderation = template.get_user_moderation();
        boolean can_send_stickers = template.get_can_send_stickers();
        boolean can_send_gifs = template.get_can_send_gifs();
        int max_sticker_count = template.get_max_sticker_count();
        int max_gif_count = template.get_max_gif_count();
        //long reset_time = template.get_reset_time();
        return updateChatInfo ( chat_id, chat_name, chat_url, need_to_store, admins_list,
                user_moderation, can_send_stickers, can_send_gifs, max_sticker_count, max_gif_count, reset_time );
    }

    protected void load () {
        _template.clear();
        try (Connection con = dbMain.getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_QUERY))
        {
            while (rset.next()){
                StringTokenizer admin_token = new StringTokenizer(rset.getString("admins_list"));
                List<String> admins_list = new ArrayList<>();
                while ( admin_token.hasMoreTokens() ) {
                    admins_list.add(admin_token.nextToken());
                }
                StringTokenizer user_token = new StringTokenizer(rset.getString("user_moderation"));
                List<String> users_list = new ArrayList<>();
                while ( user_token.hasMoreTokens() ) {
                    users_list.add(user_token.nextToken());
                }
                _template.put(rset.getString("chat_id"), new chatInfoTemplate(rset.getString("chat_name"), rset.getString("chat_url"),
                        rset.getBoolean("need_to_store"), admins_list, users_list, rset.getBoolean("can_send_stickers"),
                        rset.getBoolean("can_send_gifs"), rset.getInt("max_sticker_count"),
                        rset.getInt("max_gif_count"), rset.getLong("reset_time")));
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при загрузке информации о чатах с базы - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при загрузке информации о чатах с базы - " + e);
        }
    }

    public boolean storeMe ( String chat_id ) {
        try (Connection con = dbMain.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(DELETE_QUERY_BY_CHAT)) {
                st.setString(1, chat_id);
                st.execute();
            }
            chatInfoTemplate template = _template.get(chat_id);
            try (PreparedStatement st = con.prepareStatement(INSERT_QUERY)) {
                st.setString(1, chat_id);
                st.setString(2, template.get_chat_name());
                st.setString(3, template.get_chat_url());
                st.setBoolean(4, template.get_need_to_store());
                StringBuilder admin_list = new StringBuilder();
                if ( template.get_admins_list()!=null ) {
                    for (String list : template.get_admins_list()) {
                        admin_list.append(admin_list.isEmpty() ? "" : ", ").append(list);
                    }
                }
                StringBuilder moder_list = new StringBuilder();
                if ( template.get_user_moderation()!=null ) {
                    for (String list : template.get_user_moderation()) {
                        moder_list.append(moder_list.isEmpty() ? "" : ", ").append(list);
                    }
                }
                st.setString(5, admin_list.toString());
                st.setString(6, moder_list.toString());
                st.setBoolean(7, template.get_can_send_stickers());
                st.setBoolean(8, template.get_can_send_gifs());
                st.setInt(9, template.get_max_sticker_count());
                st.setInt(10, template.get_max_gif_count());
                st.setLong(11, template.get_reset_time());
                st.execute();
                System.out.println(getClass().getSimpleName() + ": Информация о чате " + template.get_chat_name() + " сохранена в базе");
                new IndexMain().SendAnswer(Long.parseLong(chat_id), getClass().getSimpleName(), getClass().getSimpleName() + ": Информация о чате " + template.get_chat_name() + " сохранена в базе");
                return true;
            }
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о чате " + _template.get(chat_id).get_chat_name() + " сохранена в базе\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о чате " + _template.get(chat_id).get_chat_name() + " сохранена в базе @MrKirill1232 \n" + e);
            return false;
        }
    }

    public chatInfoHolder.chatInfoTemplate getTemplate(String chat_id){
        return _template.get(chat_id);
    }

    public static class chatInfoTemplate {
        //private final String _chat_id;
        private final String _chat_name;
        private final String _chat_url;
        private final boolean _need_to_store;
        private final List<String> _admins_list;
        private final List<String> _user_moderation;
        private final boolean _can_send_stickers;
        private final boolean _can_send_gifs;
        private final int _max_sticker_count;
        private final int _max_gif_count;
        private final long _reset_time;

        public chatInfoTemplate ( String chat_name, String chat_url, boolean need_to_store, List<String> admins_list, List<String> user_moderation,
                                  boolean can_send_stickers, boolean can_send_gifs, int max_sticker_count, int max_gif_count, long reset_time) {
            _chat_name = chat_name;
            _chat_url = chat_url;
            _need_to_store = need_to_store;
            _admins_list = admins_list;
            _user_moderation = user_moderation;
            _can_send_stickers = can_send_stickers;
            _can_send_gifs = can_send_gifs;
            _max_sticker_count = max_sticker_count;
            _max_gif_count = max_gif_count;
            _reset_time = reset_time;
        }

        public String get_chat_name () {
            return _chat_name;
        }
        public String get_chat_url () {
            return _chat_url;
        }
        public boolean get_need_to_store () {
            return _need_to_store;
        }
        public List<String> get_admins_list () {
            return _admins_list;
        }
        public boolean checkAdminList ( String user_id ) {
            return _admins_list != null && _admins_list.contains(user_id);
        }
        public List<String> get_user_moderation () {
            return _user_moderation;
        }
        public boolean checkUserModerList ( String user_id ) {
            return _user_moderation != null && _user_moderation.contains(user_id);
        }
        public boolean get_can_send_stickers () {
            return _can_send_stickers;
        }
        public boolean get_can_send_gifs () {
            return _can_send_gifs;
        }
        public int get_max_sticker_count () {
            return _max_sticker_count;
        }
        public int get_max_gif_count () {
            return _max_gif_count;
        }
        public long get_reset_time () {
            return _reset_time;
        }
    }

    public static chatInfoHolder getInstance() {
        return chatInfoHolder.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final chatInfoHolder INSTANCE = new chatInfoHolder();
    }
}
