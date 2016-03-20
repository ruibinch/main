package common;

import java.time.LocalDateTime;

public class Interval {
	
	public final String FREQ_HOURLY = "HOURLY";
	public final String FREQ_DAILY = "DAILY";
	public final String FREQ_WEEKLY = "WEEKLY";
	public final String FREQ_MONTHLY ="MONTHLY";
	public final String FREQ_YEARLY = "YEARLY";
	
	private final String MESSAGE_INVALID_FREQUENCY = "Invalid frequency";
	
	private String freq;
	private int interval;
	private int count;
	private LocalDateTime until;
	private String byDay = "";
	
	public Interval() {
	}
	
	public Interval(String freq, int interval, int count, String byDay) throws Exception{
		setFrequency(freq);
		this.interval = interval;
		this.count = count;
		this.byDay = byDay;
		this.until = LocalDateTime.MAX;
		// Until cannot be a valid value if count exists
	}
	
	public Interval(String freq, int interval, LocalDateTime until, String byDay) throws Exception{
		setFrequency(freq);
		this.interval = interval;
		this.until = until;
		this.byDay = byDay;
		this.count = -1;
		// Count cannot be a valid value if until exists
	}
	
	public String getFrequency() {
		return freq;
	}
	
	public int getTimeInterval() {
		return interval;
	}
	
	public int getCount() {
		return count;
	}
	
	public LocalDateTime getUntil() {
		return until;
	}
	
	public String getByDay() {
		return byDay;
	}
	
	public void setFrequency(String freq) throws Exception{
		switch (freq) {
		
		case FREQ_HOURLY :
			this.freq = FREQ_HOURLY;
			break;
			
		case FREQ_DAILY :
			this.freq = FREQ_DAILY;
			break;
			
		case FREQ_WEEKLY :
			this.freq = FREQ_WEEKLY;
			break;
			
		case FREQ_MONTHLY :
			this.freq = FREQ_MONTHLY;
			break;
			
		case FREQ_YEARLY :
			this.freq = FREQ_YEARLY;
			break;
			
		default :
			Exception e = new Exception(MESSAGE_INVALID_FREQUENCY);
			throw e;
		}
	}
	
	public void setTimeInterval(int interval) {
		this.interval = interval;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setUntil(LocalDateTime until) {
		this.until = until;
	}
	
	public void setByDay(String byDay) {
		this.byDay = byDay;
	}
}