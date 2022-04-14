package cs505pubsubcep.Models;

import java.util.*;

class Team {
    private String name;
    private ArrayList<Integer> sid;
    private int status;

    public Team(String name, ArrayList<Integer> sid, int status){
        this.name = name;
        this.sid = sid;
        this.status = status;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }

    public ArrayList<Integer> getSid(){
        return this.sid;
    }

    public void setSid(ArrayList<Integer> sid){
        this.sid=sid;
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int status){
        this.status=status;
    }
}