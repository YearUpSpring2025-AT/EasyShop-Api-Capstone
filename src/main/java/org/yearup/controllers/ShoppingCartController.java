package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged-in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin()
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping("")
    public ShoppingCart getCart(Principal principal)
    {


        try
        {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shopping-cartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added

    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal  principal) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            shoppingCartDao.addItem(user.getId(), productId, 1);

            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product to cart.");
        }
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{productId}")
    public void updateProductInCart(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal){
        try {
            // Get the current user
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            // Update the quantity for the specified product in the cart
            shoppingCartDao.addItem(user.getId(), productId, item.getQuantity());

        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product quantity.");
        }
    }


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping("")
    public ShoppingCart clearCart(Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            shoppingCartDao.clearCart(user.getId());
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @DeleteMapping("{productId}")
    public ShoppingCart removeItem(Principal principal, @PathVariable int productId){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            shoppingCartDao.removeItem(user.getId(), productId);
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
