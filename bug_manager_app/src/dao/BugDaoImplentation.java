package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import model.*;

public class BugDaoImplentation implements BugInterface {

    private static final String SELECT_BASE =
        "SELECT b.*, u.id AS u_id, u.username, u.fullname, u.email, u.password, u.is_admin, u.job_title, " +
        "p.id AS p_id, p.name AS p_name, p.status AS p_status " +
        "FROM bugs b JOIN users u ON b.user_id = u.id " +
        "LEFT JOIN projects p ON b.project_id = p.id ";

    @Override
    public List<Bug> findAll() {
        return query(SELECT_BASE + "ORDER BY b.create_date DESC", null);
    }

    @Override
    public List<Bug> findByUserId(long userId) {
        return query(SELECT_BASE + "WHERE b.user_id = ? ORDER BY b.create_date DESC", userId);
    }

    public List<Bug> findByProjectId(long projectId) {
        return query(SELECT_BASE + "WHERE b.project_id = ? ORDER BY b.create_date DESC", projectId);
    }

    private List<Bug> query(String sql, Long param) {
        List<Bug> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (param != null) stmt.setLong(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRowToBug(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public long insert(Bug bug) {
        String sql = "INSERT INTO bugs (user_id, title, description, create_date, due_date, status, priority, severity, category, project_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1,   bug.getUser().getId());
            stmt.setString(2, bug.getTitle());
            stmt.setString(3, bug.getDescription());
            stmt.setDate(4,   Date.valueOf(bug.getCreateDate()));
            stmt.setDate(5,   bug.getDueDate() != null ? Date.valueOf(bug.getDueDate()) : null);
            stmt.setString(6, bug.getStatus().name());
            stmt.setString(7, bug.getPriority().name());
            stmt.setString(8, bug.getSeverity() != null ? bug.getSeverity().name() : BugSeverity.MAJOR.name());
            stmt.setString(9, bug.getCategory() != null ? bug.getCategory().name() : BugCategory.FUNCTIONAL.name());
            if (bug.getProject() != null) stmt.setLong(10, bug.getProject().getId());
            else stmt.setNull(10, Types.BIGINT);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) { bug.setId(keys.getLong(1)); return bug.getId(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    @Override
    public void update(Bug bug) {
        String sql = "UPDATE bugs SET title=?, description=?, due_date=?, status=?, priority=?, severity=?, category=?, project_id=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bug.getTitle());
            stmt.setString(2, bug.getDescription());
            stmt.setDate(3,   bug.getDueDate() != null ? Date.valueOf(bug.getDueDate()) : null);
            stmt.setString(4, bug.getStatus().name());
            stmt.setString(5, bug.getPriority().name());
            stmt.setString(6, bug.getSeverity() != null ? bug.getSeverity().name() : BugSeverity.MAJOR.name());
            stmt.setString(7, bug.getCategory() != null ? bug.getCategory().name() : BugCategory.FUNCTIONAL.name());
            if (bug.getProject() != null) stmt.setLong(8, bug.getProject().getId());
            else stmt.setNull(8, Types.BIGINT);
            stmt.setLong(9, bug.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(long id) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("DELETE FROM bugs WHERE id=?");
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Bug mapRowToBug(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("u_id"));
        user.setUsername(rs.getString("username"));
        user.setFullname(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAdmin(rs.getBoolean("is_admin"));
        try { user.setJobTitle(rs.getString("job_title")); } catch (SQLException ignored) {}

        Bug bug = new Bug();
        bug.setId(rs.getLong("id"));
        bug.setUser(user);
        bug.setTitle(rs.getString("title"));
        bug.setDescription(rs.getString("description"));
        bug.setCreateDate(rs.getDate("create_date").toLocalDate());
        Date due = rs.getDate("due_date");
        if (due != null) bug.setDueDate(due.toLocalDate());
        bug.setStatus(BugStatus.valueOf(rs.getString("status")));
        bug.setPriority(BugPriority.valueOf(rs.getString("priority")));

        try {
            String sev = rs.getString("severity");
            bug.setSeverity(sev != null ? BugSeverity.valueOf(sev) : BugSeverity.MAJOR);
        } catch (SQLException ignored) { bug.setSeverity(BugSeverity.MAJOR); }

        try {
            String cat = rs.getString("category");
            bug.setCategory(cat != null ? BugCategory.valueOf(cat) : BugCategory.FUNCTIONAL);
        } catch (SQLException ignored) { bug.setCategory(BugCategory.FUNCTIONAL); }

        // Map project
        try {
            long pid = rs.getLong("p_id");
            if (!rs.wasNull()) {
                model.Project proj = new model.Project();
                proj.setId(pid);
                proj.setName(rs.getString("p_name"));
                proj.setStatus(model.Project.ProjectStatus.valueOf(rs.getString("p_status")));
                bug.setProject(proj);
            }
        } catch (SQLException ignored) {}

        return bug;
    }
}
