package model;

public class Project {

    public enum ProjectStatus { OPEN, CLOSED }

    private long          id;
    private String        name;
    private ProjectStatus status;

    public Project() {}

    public Project(long id, String name, ProjectStatus status) {
        this.id     = id;
        this.name   = name;
        this.status = status;
    }

    public long          getId()     { return id; }
    public void          setId(long id)          { this.id = id; }
    public String        getName()   { return name; }
    public void          setName(String name)     { this.name = name; }
    public ProjectStatus getStatus() { return status; }
    public void          setStatus(ProjectStatus s) { this.status = s; }

    public boolean isClosed() { return status == ProjectStatus.CLOSED; }

    @Override public String toString() { return name; }
}
