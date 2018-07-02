package com.example.ldobriakova.plet_03;

public class Product {
    private String product_id, serialNumber, toyAlias;
    public Product()
    {
        this.product_id = "";
        this.serialNumber = "";
        this.toyAlias = "";
    }
    public void setProdID(String prodID){
        this.product_id = prodID;
    }
    public void setProdAlias(String prodAlias){
        this.toyAlias = prodAlias;
    }
    public void setProd_serial(String prodSerial){
        this.serialNumber = prodSerial;
    }
    public String getProd_id(){
        return this.product_id;
    }
    public String getProd_alias(){
        return this.toyAlias;
    }
    public String getSerial_number(){
        return this.serialNumber;
    }
}
