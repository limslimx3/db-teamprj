package domain;

public class Schedule {
    private int scheduleId;
    private String day;
    private String time;

    public Schedule(int scheduleId, String day, String time) {
        this.scheduleId = scheduleId;
        this.day = day;
        this.time = time;
    }

    public int getScheduleId() { return scheduleId; }
    public String getDay() { return day; }
    public String getTime() { return time; }
}
