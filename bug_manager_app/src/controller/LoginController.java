package controller;

import View.BugListView;
import View.LoginView;
import View.RegisterView;
import dao.UserDaoImplementation;
import model.User;
import service.UserService;

public class LoginController {

    private final LoginView loginView;
    private final UserDaoImplementation userDao     = new UserDaoImplementation();
    private final UserService           userService = new UserService(userDao);

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
    }

    public void onLoginButtonClick() {
        String input    = loginView.getTxtUsername().getText().trim();
        String password = new String(loginView.getTxtPassword().getPassword()).trim();

        if (input.isEmpty() || password.isEmpty()) {
            loginView.getLblStatus().setText("Please enter your username / email and password.");
            return;
        }

        User user = userService.login(input, password);

        if (user == null) {
            loginView.getLblStatus().setText("Invalid username / email or password.");
            return;
        }

        BugListView bugListView = new BugListView(user);
        bugListView.setVisible(true);
        loginView.dispose();
    }

    public void onRegisterButtonClick() {
        RegisterView registerView = new RegisterView();
        registerView.setVisible(true);
        loginView.dispose();
    }
}
