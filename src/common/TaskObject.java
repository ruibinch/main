package common;

public class TaskObject implements Comparable<TaskObject> {

	protected String title = "";
	protected int startDate;
	protected int endDate;
	protected int startTime;
	protected int endTime;
	protected String category; // deadline, event, or floating
	protected String status; //completed, overdue or incomplete
	protected int taskId;
	
	// Constructor for event tasks
	public TaskObject(String title, int startDate, int endDate, int startTime, int endTime, String category, String status, int taskId) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.category = category;
		this.status = status;
		this.taskId = taskId;
	}
	
	// Constructor for deadline tasks
	public TaskObject(String title, int endDate, int endTime, String category, String status, int taskId){
		this.title = title;
		this.startDate = endDate;
		this.endDate = endDate;
		this.startTime = endTime;
		this.endTime = endTime;
		this.category = category;
		this.status = status;
		this.taskId = taskId;
	}
	
	// Constructor for floating tasks
	public TaskObject(String title, String category, String status, int taskId) {
		this.title = title;
		this.startDate = -1;
		this.endDate = -1;
		this.startTime = -1;
		this.endTime = -1;
		this.category = category;
		this.status = status;
		this.taskId = taskId;
	}
	
	// Constructor for search keyword, delete, save and for edit-title functions
	public TaskObject(String title){
		this.title = title;
		this.startDate = -1;
		this.endDate = -1;
		this.startTime = -1;
		this.endTime = -1;
		this.category = null;
		this.status = null;
		this.taskId = -1;
	}
	
	// Constructor for edit-date functions
	public TaskObject(int date) {
		this.title = "";
		this.startDate = date;
		this.endDate = date;
		this.startTime = -1;
		this.endTime = -1;
		this.category = null;
		this.status = null;
		this.taskId = -1;
	}
	
    public TaskObject(String title, int taskId){
        this.title = title;
        this.startDate = 0;
        this.endDate = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.category = "";
        this.status = "";
        this.taskId = taskId;
    }
	
	// empty constructor
	public TaskObject() {
		
	}
	
	public String getTitle() {
		if (title == null)
			return "null";
		return title;
	}
	
	public int getStartDate() {
		return startDate;
	}
	
	public int getEndDate() {
		return endDate;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getEndTime() {
		return endTime;
	}
	
	public String getCategory() {
		if (category == null) 
			return "null";
		return category;
	}
	
	public String getStatus() {
		if (status == null)
			return "null";
		return status;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}
	
	public void setStartDate(int newStartDate) {
		this.startDate = newStartDate;
	}
	
	public void setEndDate(int newEndDate) {
		this.endDate = newEndDate;
	}
	
	public void setStartTime(int newStartTime) {
		this.startTime = newStartTime;
	}
	
	public void setEndTime(int newEndTime) {
		this.endTime = newEndTime;
	}
	
	public void setCategory(String newCategory) {
		this.category = newCategory;
	}
	
	public void setStatus(String newStatus) {
		this.status = newStatus;
	}
	
	public void setTaskId(int newTaskId) {
		this.taskId = newTaskId;
	}
	
	// Checks if title, dates and times are invalid values
	public boolean isSearchKeywordPresent() {
		if (title.equals("") && startDate == -1 && endDate == -1 && startTime == -1 && endTime == -1)
			return false;
		return true;
	}
	
	//for testing purpose
	public void resetAttributes() {
		setTitle(null);
		setStartDate(-1);
		setEndDate(-1);
		setStartTime(-1);
		setEndTime(-1);
	}

    @Override
    public int compareTo(TaskObject task) {
        // TODO Auto-generated method stub
        return 0;
    }

}