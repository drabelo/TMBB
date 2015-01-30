package com.example.tmbb;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.UUID;

public class Person implements java.io.Serializable{
	private static final long serialVersionUID = 7526472295622776147L;

	
	String name = "";
	String phoneNumber = "";
	String date = "";
	UUID mId;
	ArrayList<Body> received = new ArrayList<Body>();
	ArrayList<Body> sent = new ArrayList<Body>();
    Bitmap contact_thumbnail;

    public Bitmap getContact_thumbnail() {
        return contact_thumbnail;
    }

    public void setContact_thumbnail(Bitmap contact_thumbnail) {
        this.contact_thumbnail = contact_thumbnail;
    }


	
	public Person(String address){
		phoneNumber = address;
	}
	public Person(String phoneNumber, String body, String date){
		this.phoneNumber = phoneNumber;
		this.name = name;
		this.date = date;
		mId = UUID.randomUUID();

	}
	
	public void addNewReceived(Body body){
		if(!received.contains(body)){
		received.add(body);
		}
	}
	
	public void addNewSent(Body body){
		if(!sent.contains(body)){
		sent.add(body);
		}
	}



	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setId(UUID id) {
		mId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UUID getId() {
		return mId;
	}
	
	public String toString(){
		return name + "=> " + phoneNumber;
	}
	
	public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Person)
        {
            sameSame = this.phoneNumber.equals(((Person) object).phoneNumber);

        }

        return sameSame;
    }
	
	public String toString2(ArrayList<Body> lol){
		String result = "";
		for(Body o : lol){
			result += o.date + "   " + o.body + "\n";
			
		}
		return result;
	}


}

class Body implements java.io.Serializable{
	String date;
	String body;
	
	public Body(String date, String body){
		this.date= date;
		this.body = body;
	}
	
	public String toString(){
		return body + "     " + date;
	}
	
	public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Body)
        {
            if(this.date.equals(((Body) object).date)
            		&& this.body.equals(((Body) object).body))
            	sameSame = true;
            
        }

        return sameSame;
    }
	
}
