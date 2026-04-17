package dao;

import java.util.List;
import java.util.Optional;

import model.*;

public interface UserDaoInterface {

	long addUser(User user);

	Optional<User> findById(long id);

	Optional<User> findByUserName(String username);

	Optional<User> findByEmail(String email);

	List<User> findAll();

	void update(User user);

	void delete(long id);
}
