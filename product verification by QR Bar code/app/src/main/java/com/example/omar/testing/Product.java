package com.example.omar.testing;

public class Product {
    String company_name, enddata, price, produce_date, weight;

    public Product() {
    }

    public Product(String company_name, String enddata, String price, String produce_date, String weight) {
        this.company_name = company_name;
        this.enddata = enddata;
        this.price = price;
        this.produce_date = produce_date;
        this.weight = weight;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getEnddata() {
        return enddata;
    }

    public void setEnddata(String enddata) {
        this.enddata = enddata;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduce_date() {
        return produce_date;
    }

    public void setProduce_date(String produce_date) {
        this.produce_date = produce_date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
