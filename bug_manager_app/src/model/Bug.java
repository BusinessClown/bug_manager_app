package model;

import java.time.LocalDate;

public class Bug {

    private long id;
    private User user;           // submitter — may be null if the user was deleted
    private User lastEditedBy;   // last person to save an edit — may be null
    private String title, description;
    private LocalDate createDate, dueDate;
    private BugStatus   status;
    private BugPriority priority;
    private BugSeverity severity;
    private BugCategory category;
    private Project     project;

    public Bug() { super(); }

    public Bug(long id, User user, String title, String description,
               LocalDate createDate, LocalDate dueDate,
               BugStatus status, BugPriority priority,
               BugSeverity severity, BugCategory category) {
        this.id          = id;
        this.user        = user;
        this.title       = title;
        this.description = description;
        this.createDate  = createDate;
        this.dueDate     = dueDate;
        this.status      = status;
        this.priority    = priority;
        this.severity    = severity;
        this.category    = category;
    }

    public long        getId()             { return id; }
    public void        setId(long id)      { this.id = id; }
    public User        getUser()           { return user; }
    public void        setUser(User u)     { this.user = u; }
    public User        getLastEditedBy()                  { return lastEditedBy; }
    public void        setLastEditedBy(User u)            { this.lastEditedBy = u; }
    public String      getTitle()          { return title; }
    public void        setTitle(String t)  { this.title = t; }
    public String      getDescription()    { return description; }
    public void        setDescription(String d) { this.description = d; }
    public LocalDate   getCreateDate()     { return createDate; }
    public void        setCreateDate(LocalDate d) { this.createDate = d; }
    public LocalDate   getDueDate()        { return dueDate; }
    public void        setDueDate(LocalDate d)    { this.dueDate = d; }
    public BugStatus   getStatus()         { return status; }
    public void        setStatus(BugStatus s)     { this.status = s; }
    public BugPriority getPriority()       { return priority; }
    public void        setPriority(BugPriority p) { this.priority = p; }
    public BugSeverity getSeverity()       { return severity; }
    public void        setSeverity(BugSeverity s) { this.severity = s; }
    public BugCategory getCategory()       { return category; }
    public void        setCategory(BugCategory c) { this.category = c; }
    public Project     getProject()        { return project; }
    public void        setProject(Project p)      { this.project = p; }

    /** True if the bug belongs to a closed project. */
    public boolean isInClosedProject() {
        return project != null && project.isClosed();
    }

    /** True if the bug is overdue (not completed and past due date). */
    public boolean isOverdue() {
        return status != BugStatus.COMPLETED
                && dueDate != null
                && dueDate.isBefore(LocalDate.now());
    }

    /**
     * Returns a display label for the submitter.
     * Falls back to "Unknown User" when the original submitter was deleted.
     */
    public String getSubmitterDisplay() {
        if (user == null) return "Unknown User";
        return user.getFullname();
    }
}