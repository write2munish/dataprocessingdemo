package org.demo.data.processing.messages;

public class MockData {

	int id;
	String productName;
	String productType;
	double rate;
	double calculatedValue;
	int timesProcessed;
	
	public MockData(int inID,String inProductName, String inProductType,double inRate, double inCalculatedValue, int inTimesProcessed){
		id = inID;
		productName = inProductName;
		productType = inProductType;
		rate = inRate;
		calculatedValue = inCalculatedValue ;
		timesProcessed = inTimesProcessed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(double calculatedValue) {
		this.calculatedValue = calculatedValue;
	}

	public int getTimesProcessed() {
		return timesProcessed;
	}

	public void setTimesProcessed(int timesProcessed) {
		this.timesProcessed = timesProcessed;
	}

}
