package com.nope.webapp.util;

import com.nope.webapp.entity.Product;
import java.util.List;
import java.util.Random;

/**
 *
 * @author zvr
 */
public class ProductUtil {
    
    private static ProductUtil instance;
    
    private ProductUtil(){
        
    }
    
    public static ProductUtil getInstance(){
        
        if(instance == null){
            instance = new ProductUtil();
        }
        return instance;
    }
    
    public Product getProductByReference(List<Product> products, String reference){
        
        for(Product product : products){
            if(product.getReference().equals(reference)){
                return product;
            }
        }
        return null;
    }
    
    public void addTestProducts(List<Product> products){
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            products.add(new Product("ref"+i, random.nextInt(25)));
        }
    }
}
