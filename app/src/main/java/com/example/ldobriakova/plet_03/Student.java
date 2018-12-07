package com.example.ldobriakova.plet_03;

public class Student {
    private String st_name, st_surname, st_birthDay, st_Id, st_alias;
    public Student()
    {
        this.st_name = "";
        this.st_surname = "";
        this.st_birthDay = "";
        this.st_Id = "";
        this.st_alias = "";
    }
    public void setSTName(String name){
        this.st_name = name;
    }
    public void setSTAlias(String alias){
        this.st_alias = alias;
    }
    public void setSurname(String surname){
        this.st_surname = surname;
    }
    public void setSt_birthDay(String birthDay){
        this.st_birthDay = birthDay;
    }
    public void setSt_ID(String StId){
        this.st_Id = StId;
    }
    public String getSt_name(){
        return this.st_name;
    }
    public String getSt_alias(){
        return this.st_alias;
    }
    public String getSt_surname(){
        return this.st_surname;
    }
    public String getSt_birthDay(){
        return this.st_birthDay;
    }
    public String getSt_Id(){
        return this.st_Id;
    }
}
