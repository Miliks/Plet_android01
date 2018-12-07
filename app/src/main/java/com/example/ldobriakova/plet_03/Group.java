package com.example.ldobriakova.plet_03;

public class Group {
    private String group_id, group_name;
    public Group()
    {
        this.group_name = "";
        this.group_id = "";
    }
    public void setGroupName(String group_name){
        this.group_name = group_name;
    }
    public void setGroupId(String id){
        this.group_id = id;
    }


    public String getGroup_name(){
        return this.group_name;
    }
    public String getGroup_Id(){
        return this.group_id;
    }
}
