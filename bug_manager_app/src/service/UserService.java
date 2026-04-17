package service;

import java.util.List;
import java.util.Optional;

import dao.UserDaoImplementation;
import model.User;
import util.PasswordUtil;

public class UserService {

	private final UserDaoImplementation userDao;

	public UserService(UserDaoImplementation userDao) {
		this.userDao = userDao;
	}

	// Validates all registration fields, hashes the password, and saves the user.
	// Returns an error message string on failure, or null on success.
	public String registerUser(String fullname, String username, String email, String password, String confirm) {

		if (fullname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
			return "All fields are required.";
		}

		if (!email.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")) {
			return "Email must be in the format: example@domain.com";
		}

		if (password.length() < 8) {
			return "Password must be at least 8 characters.";
		}
		if (!password.chars().anyMatch(Character::isUpperCase)) {
			return "Password must contain at least one capital letter.";
		}
		if (!password.chars().anyMatch(Character::isDigit)) {
			return "Password must contain at least one number.";
		}
		if (!password.equals(confirm)) {
			return "Passwords do not match.";
		}

		if (userDao.findByUserName(username).isPresent()) {
			return "Username already exists.";
		}
		if (userDao.findByEmail(email).isPresent()) {
			return "Email already registered.";
		}

		User newUser = new User();
		newUser.setFullname(fullname);
		newUser.setUsername(username);
		newUser.setEmail(email);
		newUser.setPassword(PasswordUtil.hash(password));
		newUser.setAdmin(false);

		long newId = userDao.addUser(newUser);
		if (newId == -1) {
			return "Registration failed. Please try again.";
		}

		return null; // null means success
	}

	// Looks up a user by username or email, then verifies the password.
	// Returns the User on success, or null on failure.
	public User login(String usernameOrEmail, String password) {
		Optional<User> found = userDao.findByUserName(usernameOrEmail);
		if (found.isEmpty()) {
			found = userDao.findByEmail(usernameOrEmail);
		}
		if (found.isEmpty())
			return null;

		User user = found.get();
		if (!PasswordUtil.verify(password, user.getPassword()))
			return null;

		return user;
	}

	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	// Admin: save a new user (with hashed password).
	// Returns error string on failure or null on success.
	public String adminAddUser(String fullname, String username, String email, String password, boolean isAdmin, String jobTitle) {
		if (fullname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
			return "All fields are required.";
		}
		if (!email.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")) {
			return "Email must be in the format: example@domain.com";
		}
		if (userDao.findByUserName(username).isPresent()) {
			return "Username already exists.";
		}
		if (userDao.findByEmail(email).isPresent()) {
			return "Email already registered.";
		}

		User newUser = new User();
		newUser.setFullname(fullname);
		newUser.setUsername(username);
		newUser.setEmail(email);
		newUser.setPassword(PasswordUtil.hash(password));
		newUser.setAdmin(isAdmin);
		newUser.setJobTitle(jobTitle != null ? jobTitle : "");

		long newId = userDao.addUser(newUser);
		return newId == -1 ? "Failed to add user. Please try again." : null;
	}

	// Admin: update user info (no password change here).
	// Returns error string on failure or null on success.
	public String adminUpdateUser(User user, String fullname, String username, String email, boolean isAdmin, String jobTitle) {
		if (fullname.isEmpty() || username.isEmpty() || email.isEmpty()) {
			return "All fields are required.";
		}
		if (!email.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")) {
			return "Email must be in the format: example@domain.com";
		}

		// Check duplicates, excluding this user's own values
		Optional<User> existingByUsername = userDao.findByUserName(username);
		if (existingByUsername.isPresent() && existingByUsername.get().getId() != user.getId()) {
			return "Username already taken.";
		}
		Optional<User> existingByEmail = userDao.findByEmail(email);
		if (existingByEmail.isPresent() && existingByEmail.get().getId() != user.getId()) {
			return "Email already registered.";
		}

		user.setFullname(fullname);
		user.setUsername(username);
		user.setEmail(email);
		user.setAdmin(isAdmin);
		user.setJobTitle(jobTitle != null ? jobTitle : "");
		userDao.update(user);
		return null;
	}

	public void deleteUser(long id) {
		userDao.delete(id);
	}

	public Optional<User> findById(long id) {
		return userDao.findById(id);
	}
}
