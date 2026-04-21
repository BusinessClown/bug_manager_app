package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import model.*;

public class BugDaoImplentation implements BugInterface {

    /**
     * Base SELECT joins:
     *   u  = submitter (LEFT JOIN so bugs survive user deletion)
     *   ed = last_edited_by user (LEFT JOIN, may also be null)
     *   p  = project
     */
    private static final String SELECT_BASE =
        "SELECT b.*, " +
        // submitter columns
        "u.id AS u_id, u.username AS u_username, u.fullname AS u_fullname, " +
        "u.email AS u_email, u.password AS u_password, u.is_admin AS u_is_admin, u.job_title AS u_job_title, " +
        // last_edited_by columns
        "ed.id AS ed_id, ed.username AS ed_username, ed.fullname AS ed_fullname, " +
        "ed.email AS ed_email, ed.password AS ed_password, ed.is_admin AS ed_is_admin, ed.job_title AS ed_job_title, " +
        // project columns
        "p.id AS p_id, p.name AS p_name, p.status AS p_status " +
        "FROM bugs b " +
        "LEFT JOIN users u  ON b.user_id          = u.id " +
        "LEFT JOIN users ed ON b.last_edited_by   = ed.id " +
        "LEFT JOIN projects p ON b.project_id     = p.id ";

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
        String sql = "INSERT INTO bugs " +
                     "(user_id, last_edited_by, title, description, create_date, due_date, " +
                     "status, priority, severity, category, project_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setNullableLong(stmt, 1,  bug.getUser());
            setNullableLong(stmt, 2,  bug.getLastEditedBy());
            stmt.setString(3, bug.getTitle());
            stmt.setString(4, bug.getDescription());
            stmt.setDate(5,   Date.valueOf(bug.getCreateDate()));
            stmt.setDate(6,   bug.getDueDate() != null ? Date.valueOf(bug.getDueDate()) : null);
            stmt.setString(7, bug.getStatus().name());
            stmt.setString(8, bug.getPriority().name());
            stmt.setString(9, bug.getSeverity() != null ? bug.getSeverity().name() : BugSeverity.MAJOR.name());
            stmt.setString(10, bug.getCategory() != null ? bug.getCategory().name() : BugCategory.FUNCTIONAL.name());
            if (bug.getProject() != null) stmt.setLong(11, bug.getProject().getId());
            else stmt.setNull(11, Types.BIGINT);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) { bug.setId(keys.getLong(1)); return bug.getId(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    @Override
    public void update(Bug bug) {
        String sql = "UPDATE bugs SET title=?, description=?, due_date=?, status=?, " +
                     "priority=?, severity=?, category=?, project_id=?, last_edited_by=? WHERE id=?";
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
            // last_edited_by
            if (bug.getLastEditedBy() != null) stmt.setLong(9, bug.getLastEditedBy().getId());
            else stmt.setNull(9, Types.BIGINT);
            stmt.setLong(10, bug.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(long id) {
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM bugs WHERE id=?");
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Mapping ───────────────────────────────────────────────────────────────────

    private Bug mapRowToBug(ResultSet rs) throws SQLException {
        Bug bug = new Bug();
        bug.setId(rs.getLong("id"));

        // Submitter — may be NULL when the user was deleted
        long uid = rs.getLong("u_id");
        if (!rs.wasNull()) {
            User user = buildUser(rs, "u_");
            bug.setUser(user);
        }
        // lastEditedBy — may be NULL
        long edId = rs.getLong("ed_id");
        if (!rs.wasNull()) {
            bug.setLastEditedBy(buildUser(rs, "ed_"));
        }

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

        // Project
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

    /** Build a User from prefixed result-set columns (prefix = "u_" or "ed_"). */
    private User buildUser(ResultSet rs, String prefix) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(prefix + "id"));
        user.setUsername(rs.getString(prefix + "username"));
        user.setFullname(rs.getString(prefix + "fullname"));
        user.setEmail(rs.getString(prefix + "email"));
        user.setPassword(rs.getString(prefix + "password"));
        user.setAdmin(rs.getBoolean(prefix + "is_admin"));
        try { user.setJobTitle(rs.getString(prefix + "job_title")); } catch (SQLException ignored) {}
        return user;
    }

    /** Sets a BIGINT parameter from a nullable User's id. */
    private void setNullableLong(PreparedStatement stmt, int idx, User u) throws SQLException {
        if (u != null) stmt.setLong(idx, u.getId());
        else stmt.setNull(idx, Types.BIGINT);
    }
}