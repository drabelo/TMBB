package com.example.tmbb;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.UUID;

public class Person implements java.io.Serializable {
    private static final long serialVersionUID = 7526472295622776147L;


    String name = "";
    String phoneNumber = "";
    String date = "";
    UUID mId;
    //Arrays holding the received and sent SMS's
    ArrayList<Body> received = new ArrayList<Body>();
    ArrayList<Body> sent = new ArrayList<Body>();
    //the contact thumbnail
    Bitmap contact_thumbnail;

    public Person(String address) {
        phoneNumber = address;
    }


    /**
     * @param body the body of the SMS
     */
    public void addNewReceived(Body body) {
        if (!received.contains(body)) {
            received.add(body);
        }
    }

    /**
     * @param body add new sent SMS
     */
    public void addNewSent(Body body) {
        if (!sent.contains(body)) {
            sent.add(body);
        }
    }

    /**
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }


    /**
     *
     * @param date sets the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *
     * @return this person objects toString()
     */
    public String toString() {
        return name + "=> " + phoneNumber;
    }

    /**
     *
     * @param object another person
     * @return true if equal and false if not
     */
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Person) {
            sameSame = this.phoneNumber.equals(((Person) object).phoneNumber);

        }

        return sameSame;
    }




}

/**
 * Making body seriabliable
 */
class Body implements java.io.Serializable {
    String date;
    String body;

    public Body(String date, String body) {
        this.date = date;
        this.body = body;
    }

    public String toString() {
        return body + "     " + date;
    }

    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Body) {
            if (this.date.equals(((Body) object).date)
                    && this.body.equals(((Body) object).body))
                sameSame = true;

        }

        return sameSame;
    }

}
