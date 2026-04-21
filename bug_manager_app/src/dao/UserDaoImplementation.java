package dao;

import java.sql.*;
import java.util.*;

import database.DatabaseConnection;
import model.User;

public class UserDaoImplementation implements UserDaoInterface {

    @Override
    public long addUser(User user) {
        String sql = "INSERT INTO users (username, fullname, email, password, is_admin, job_title) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullname());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.isAdmin());
            stmt.setString(6, user.getJobTitle() != null ? user.getJobTitle() : "");
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) { user.setId(keys.getLong(1)); return user.getId(); }
        } catch (SQLException e) { System.out.println("Error adding user: " + e.getMessage()); e.printStackTrace(); }
        return -1;
    }

    @Override
    public Optional<User> findById(long id) {
        return querySingle("SELECT * FROM users WHERE id = ?", stmt -> stmt.setLong(1, id));
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return querySingle("SELECT * FROM users WHERE username = ?", stmt -> stmt.setString(1, username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return querySingle("SELECT * FROM users WHERE email = ?", stmt -> stmt.setString(1, email));
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            ResultSet rs = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM users ORDER BY fullname").executeQuery();
            while (rs.next()) users.add(mapRowToUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    /** Updates all user fields EXCEPT password. */
    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username=?, fullname=?, email=?, is_admin=?, job_title=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullname());
            stmt.setString(3, user.getEmail());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setString(5, user.getJobTitle() != null ? user.getJobTitle() : "");
            stmt.setLong(6, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Updates ONLY the password column for the given user id.
     * Call this after hashing the new password.
     */
    public void updatePassword(long userId, String hashedPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hashedPassword);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(long id) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM users WHERE id=?");
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FunctionalInterface interface ParamSetter { void set(PreparedStatement s) throws SQLException; }

    private Optional<User> querySingle(String sql, ParamSetter p) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            p.set(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setFullname(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAdmin(rs.getBoolean("is_admin"));
        try { user.setJobTitle(rs.getString("job_title")); } catch (SQLException ignored) {}
        return user;
    }
}