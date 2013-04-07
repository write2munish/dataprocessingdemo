package com.wipro.data.processing.messages;

public class InstrumentationMsg {

	int type; //1 - Start of Loading 2 - End of Loading 3 - last of db update
	long time;

	public InstrumentationMsg(int type, long time) {
		this.type = type;
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
