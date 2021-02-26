package com.capg.demo;

public class File1 {
	private int aid;
	private String aname;
	private String value;
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	@Override
	public String toString() {
		return "File1 [aid=" + aid + ", aname=" + aname + "]";
	}
	

}
