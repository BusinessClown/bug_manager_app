package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import model.Project;
import model.Project.ProjectStatus;

public class ProjectDao {

    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM projects ORDER BY name ASC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Project> findOpen() {
        List<Project> list = new ArrayList<>();
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM projects WHERE status='OPEN' ORDER BY name ASC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Project findById(long id) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM projects WHERE id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public long insert(String name) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("INSERT INTO projects (name, status) VALUES (?, 'OPEN')",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void updateStatus(long id, ProjectStatus status) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE projects SET status=? WHERE id=?");
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean nameExists(String name) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM projects WHERE name=?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Project map(ResultSet rs) throws SQLException {
        return new Project(
            rs.getLong("id"),
            rs.getString("name"),
            ProjectStatus.valueOf(rs.getString("status"))
        );
    }
}
