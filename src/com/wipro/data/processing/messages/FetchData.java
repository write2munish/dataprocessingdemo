package com.wipro.data.processing.messages;

public class FetchData {

	int startRowId;
	int noOfRows;
	
	public FetchData(int inStartRowId, int inNoOfRows){
		startRowId = inStartRowId;
		noOfRows = inNoOfRows;
	}
	
	public int getStartRowId() {
		return startRowId;
	}
	public void setStartRowId(int startRowId) {
		this.startRowId = startRowId;
	}
	public int getNoOfRows() {
		return noOfRows;
	}
	public void setNoOfRows(int noOfRows) {
		this.noOfRows = noOfRows;
	}
	
	

}
