package com.example.to_dolist;

public class DataModel {

    String title;
    String todo;
    String date;
    String check;

    public DataModel() {
    }

    public DataModel(String title, String todo, String date, String check) {
        this.title = title;
        this.todo = todo;
        this.date = date;
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public String getTodo() {
        return todo;
    }

    public String getDate() {
        return date;
    }

    public String getCheck() {
        return check;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
