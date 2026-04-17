package dao;

import java.util.List;

import model.Bug;

public interface BugInterface {

	List<Bug> findAll();

	List<Bug> findByUserId(long userId);

	long insert(Bug bug);

	void update(Bug bug);

	void delete(long id);
}
