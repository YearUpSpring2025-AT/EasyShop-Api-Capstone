package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.ShoppingCart;


public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
//    void addItem(int userId, int productId, int quantity);
//    void removeItem(int userId, int productId);
//    void clearCart(int userId);
}
