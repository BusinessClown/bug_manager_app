package controller;

import java.util.List;
import java.util.stream.Collectors;

import View.BugListView;
import View.UserManagementPanel;
import dao.BugDaoImplentation;
import dao.UserDaoImplementation;
import model.Bug;
import model.User;
import service.BugService;
import service.UserService;
import util.PasswordUtil;

public class UserManagementController {

    private final UserManagementPanel panel;
    private final BugListView         parentView;

    private final UserDaoImplementation userDao     = new UserDaoImplementation();
    private final UserService           userService = new UserService(userDao);
    private final BugDaoImplentation    bugDao      = new BugDaoImplentation();
    private final BugService            bugService  = new BugService(bugDao);

    private List<User> allUsers;
    private List<User> currentUsers;

    public UserManagementController(UserManagementPanel panel, BugListView parentView) {
        this.panel      = panel;
        this.parentView = parentView;
    }

    // ── Load / search ─────────────────────────────────────────────────────────────
    public void loadUsers() {
        allUsers     = userService.getAllUsers();
        currentUsers = allUsers;
        panel.displayUsers(currentUsers);
        parentView.showInfo("User Management — " + currentUsers.size() + " user(s).");
    }

    public void onSearchClick() {
        if (allUsers == null) loadUsers();
        String keyword     = panel.getSearchText().toLowerCase();
        String adminFilter = panel.getAdminFilter();

        currentUsers = allUsers.stream()
            .filter(u -> keyword.isEmpty()
                || u.getFullname().toLowerCase().contains(keyword)
                || u.getUsername().toLowerCase().contains(keyword)
                || u.getEmail().toLowerCase().contains(keyword)
                || (u.getJobTitle() != null && u.getJobTitle().toLowerCase().contains(keyword)))
            .filter(u -> switch (adminFilter) {
                case "Admins Only" -> u.isAdmin();
                case "Non-Admins"  -> !u.isAdmin();
                default            -> true;
            })
            .collect(Collectors.toList());

        panel.displayUsers(currentUsers);
        parentView.showInfo("Search — " + currentUsers.size() + " result(s).");
    }

    public void onClearSearchClick() {
        panel.clearSearchFields();
        loadUsers();
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────────
    public void onAddClick()  { panel.showAddForm(); parentView.showInfo("Fill in the form to add a new user."); }

    public void onReadClick() {
        User user = getSelectedUser();
        if (user == null) { parentView.showError("Please select a user to view."); return; }
        List<Bug> bugs = bugService.getBugsByUser(user.getId());
        parentView.displayBugsInSidebar(bugs, user.getFullname());
        panel.showReadPanel(user, bugs);
    }

    public void onEditClick() {
        User user = getSelectedUser();
        if (user == null) { parentView.showError("Please select a user to edit."); return; }
        panel.showEditForm(user);
    }

    public void onDeleteClick() {
        User user = getSelectedUser();
        if (user == null) { parentView.showError("Please select a user to delete."); return; }
        if (BugListController.showThemedConfirm(parentView, "Delete User",
                "Delete user \"" + user.getUsername() + "\"?\n" +
                "Their submitted bugs will be kept and shown as 'Unknown User'.")) {
            userService.deleteUser(user.getId());
            parentView.restoreSidebar();
            loadUsers();
            parentView.showSuccess("User \"" + user.getUsername() + "\" deleted.");
        }
    }

    public void onSaveUserClick(boolean editMode) {
        View.UserFormPanel form = panel.getFormPanel();
        String fullname  = form.getFullName();
        String username  = form.getUsername();
        String email     = form.getEmail();
        String jobTitle  = form.getJobTitle();
        String password  = form.getPassword();
        boolean isAdmin  = form.isAdmin();

        if (editMode) {
            User user = form.getCurrentUser();
            if (user == null) { parentView.showError("No user selected for editing."); return; }

            // Update profile fields (no password column touched here)
            String error = userService.adminUpdateUser(user, fullname, username, email, isAdmin, jobTitle);
            if (error != null) { parentView.showError(error); return; }

            // If a new password was provided, update it separately via its own SQL
            if (!password.isEmpty()) {
                String hashed = PasswordUtil.hash(password);
                userDao.updatePassword(user.getId(), hashed);
            }

            parentView.showSuccess("User \"" + username + "\" updated.");
        } else {
            if (password.isEmpty()) { parentView.showError("Password is required for new users."); return; }
            String error = userService.adminAddUser(fullname, username, email, password, isAdmin, jobTitle);
            if (error != null) { parentView.showError(error); return; }
            parentView.showSuccess("User \"" + username + "\" added.");
        }
        loadUsers();
        panel.showInnerCard(UserManagementPanel.CARD_TABLE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────
    private User getSelectedUser() {
        int row = panel.getTblUsers().getSelectedRow();
        if (row == -1 || currentUsers == null) return null;
        long id = (long) panel.getTableModel().getValueAt(row, 0);
        return currentUsers.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}