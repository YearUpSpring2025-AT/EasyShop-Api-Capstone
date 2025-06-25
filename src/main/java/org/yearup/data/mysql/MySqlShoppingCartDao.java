package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String sql = """
                SELECT
                *
                FROM
                shopping_cart sc
                JOIN products p ON sc.product_id = p.product_id
                WHERE
                user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = mapRow(rs);
                int quantity = rs.getInt("quantity");


                if (product != null) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    cart.add(item);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cart;
    }
    @Override
    public void addItem(int userId, int productId, int quantity){

        String sql = """
                INSERT INTO shopping_cart (user_id, product_id, quantity)
                VALUES (?, ?, ?);
                ON DUPLICATE KEY UPDATE quantity = quantity + ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.setInt(4, quantity);
            statement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    };
    @Override
    public void removeItem(int userId, int productId){
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
    @Override
    public void clearCart(int userId){
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }


}
