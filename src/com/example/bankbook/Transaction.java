package com.example.bankbook;


public class Transaction {
	String headline;
	String description;
	double magnitude;
	String date;
	
	public Transaction(String headline, String date, double magnitude) {
		this.headline = headline;
		this.magnitude = magnitude;
		this.date = date;
	}
	
	public String getHeadline() {
		return this.headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public String getDate() {
		return this.date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public double getMagnitude() {
		return this.magnitude;
	}
	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	
}
