package controller;

import View.LoginView;
import View.RegisterView;
import dao.UserDaoImplementation;
import service.UserService;
import util.AppTheme;

public class RegisterController {

    private final RegisterView          registerView;
    private final UserDaoImplementation userDao     = new UserDaoImplementation();
    private final UserService           userService = new UserService(userDao);

    public RegisterController(RegisterView registerView) {
        this.registerView = registerView;
    }

    public void onRegisterButtionClick() {
        String fullname  = registerView.getTxtFullName().getText().trim();
        String username  = registerView.getTxtUsername().getText().trim();
        String email     = registerView.getTxtEmail().getText().trim();
        String password  = new String(registerView.getTxtPassword().getPassword()).trim();
        String confirm   = new String(registerView.getTxtConfirmPassword().getPassword()).trim();

        String error = userService.registerUser(fullname, username, email, password, confirm);
        if (error != null) {
            registerView.getLblStatus().setText(error);
            return;
        }

        // Success — show message in status label then go back to login
        registerView.getLblStatus().setForeground(AppTheme.SUCCESS);
        registerView.getLblStatus().setText("Registration successful! Please sign in.");
        javax.swing.Timer t = new javax.swing.Timer(1500, e -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
            registerView.dispose();
        });
        t.setRepeats(false);
        t.start();
    }

    public void onLoginButtionClick() {
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
        registerView.dispose();
    }
}
