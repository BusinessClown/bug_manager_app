package service;

import java.util.List;
import java.util.stream.Collectors;

import dao.BugDaoImplentation;
import model.Bug;
import model.User;

public class BugService {

    private final BugDaoImplentation bugDao;

    public BugService(BugDaoImplentation bugDao) { this.bugDao = bugDao; }

    public List<Bug> getAllBugs()              { return bugDao.findAll(); }
    public List<Bug> getBugsByUser(long userId){ return bugDao.findByUserId(userId); }
    public List<Bug> getBugsByProject(long projectId) { return bugDao.findByProjectId(projectId); }
    public long      createBug(Bug bug)        { return bugDao.insert(bug); }
    public void      updateBug(Bug bug)        { bugDao.update(bug); }
    public void      deleteBug(long id)        { bugDao.delete(id); }

    /**
     * In-memory filter. Pass "Any" to skip a field.
     * ownerFilter: "All Bugs" or "My Bugs".
     * showClosedProjects: if false, bugs in closed projects are excluded.
     */
    public List<Bug> getFilteredBugs(String status, String priority, String severity,
                                     String category, String project,
                                     String ownerFilter, User loggedInUser,
                                     String keyword, boolean showClosedProjects) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();

        return bugDao.findAll().stream()
            .filter(b -> showClosedProjects || !b.isInClosedProject())
            .filter(b -> status.equals("Any")   || b.getStatus().name().equals(status))
            .filter(b -> priority.equals("Any") || b.getPriority().name().equals(priority))
            .filter(b -> severity.equals("Any") || (b.getSeverity() != null && b.getSeverity().name().equals(severity)))
            .filter(b -> category.equals("Any") || (b.getCategory() != null && b.getCategory().name().equals(category)))
            .filter(b -> project.equals("Any")  || (b.getProject() != null && b.getProject().getName().equals(project)))
            .filter(b -> ownerFilter.equals("All Bugs") || b.getUser().getId() == loggedInUser.getId())
            .filter(b -> kw.isEmpty()
                    || b.getTitle().toLowerCase().contains(kw)
                    || (b.getDescription() != null && b.getDescription().toLowerCase().contains(kw)))
            .collect(Collectors.toList());
    }

    /** Backwards compat overload without project / showClosed filters */
    public List<Bug> getFilteredBugs(String status, String priority, String severity,
                                     String category, String ownerFilter,
                                     User loggedInUser, String keyword) {
        return getFilteredBugs(status, priority, severity, category, "Any",
                ownerFilter, loggedInUser, keyword, false);
    }

    public Bug findById(List<Bug> bugs, long id) {
        return bugs.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }
}
