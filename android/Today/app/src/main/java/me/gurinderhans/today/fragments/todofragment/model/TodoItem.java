package me.gurinderhans.today.fragments.todofragment.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItem extends RealmObject {

    protected static final String TAG = TodoItem.class.getSimpleName();

    @PrimaryKey
    private String text;

    private int orderNumber;

    private boolean done;

    private Date createdAt = new Date();

    private Date setForDate;

    public TodoItem() {/**/}

    public TodoItem(String text, Date forDate) {
        this.text = text;
        this.setForDate = forDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSetForDate() {
        return setForDate;
    }

    public void setSetForDate(Date setForDate) {
        this.setForDate = setForDate;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
