package SQLPrototype2;

public class Tasks {
	private long id;
	private String name;
	private String comment;
//	private long timeEnd;
	private long dueDate;
	private int colorID;
	private int priority;
	
	public long getId() 
	{
		return id;
	}
	
	public void setId(long id) 
	{
		this.id = id;
	}
	
	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getComment() 
	{
		return comment;
	}
	
	public void setComment(String comment) 
	{
		this.comment = comment;
	}

//	public long getTimeEnd() {
//		return timeEnd;
//	}
//
//	public void setTimeEnd(long timeEnd) {
//		this.timeEnd = timeEnd;
//	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public int getColorID() {
		return colorID;
	}

	public void setColorID(int colorID) {
		this.colorID = colorID;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() 
	{
		return comment;
	}
	
}
