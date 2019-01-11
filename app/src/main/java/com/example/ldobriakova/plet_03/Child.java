package com.example.ldobriakova.plet_03;

public class Child {
    private String child_alias, child_gender, child_birthDay, child_Id;
    public Child()
    {
        this.child_alias = "";
        this.child_birthDay = "";
        this.child_gender = "";
        this.child_Id = "";

    }
    public void setAlias(String alias){
        this.child_alias = alias;
    }

    public void setGender(String gender){
        this.child_gender = gender;
    }
    public void setChild_birthDay(String birthDay){
        this.child_birthDay = birthDay;
    }
    public void setChild_ID(String childId){
        this.child_Id = childId;
    }
    public String getChild_alias(){
        return this.child_alias;
    }
    public String getChild_gender(){
        return this.child_gender;
    }
    public String getChild_birthDay(){
        return this.child_birthDay;
    }
    public String getChild_Id(){
        return this.child_Id;
    }

}
