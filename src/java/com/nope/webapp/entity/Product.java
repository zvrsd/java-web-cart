package com.nope.webapp.entity;

/**
 *
 * @author zvr
 */
public class Product {

    String reference;
    int quantity;

    public Product(String reference, int quantity) {
        this.reference = reference;
        this.quantity = quantity;
    }
    
    
    public Product(){
        
    }
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void increaseQuantityBy(int quantity){
        this.quantity += quantity;
    }
    
    public void decreaseQuantityBy(int quantity){
        this.quantity -= quantity;
    }
}
