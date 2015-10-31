package com.serverus.paroah.models;

import java.util.List;

/**
 * Created by alvinvaldez on 9/16/15.
 */
public class ListInfo {

    public  String  title;
    public  String  desc;
    public  String  date;
    public  String  time;
    public  int     _id;

    public ListInfo(){

    }

    public ListInfo(String title, String desc, String date){
        this.title  = title;
        this.date   = date;
        this.desc   = desc;
    }

    public void set_id(int _id){
        this._id = _id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }
}
