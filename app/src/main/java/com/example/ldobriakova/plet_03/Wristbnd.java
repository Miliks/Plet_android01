package com.example.ldobriakova.plet_03;

public class Wristbnd {
    private String  wrst_mac, wrst_alias, wrst_Id;
    public Wristbnd()
    {
        this.wrst_mac = "";
        this.wrst_alias = "";
        this.wrst_Id = "";
           }
    public void setAlias(String alias){
        this.wrst_alias = alias;
    }
    public void setMac(String mac){
        this.wrst_mac = mac;
    }
    public void setWrst_ID(String wrstId){
        this.wrst_Id = wrstId;
    }
    public String getWrst_alias(){
        return this.wrst_alias;
    }
    public String getWrst_mac(){
        return this.wrst_mac;
    }
    public String getWrst_Id(){
        return this.wrst_Id;
    }
}
