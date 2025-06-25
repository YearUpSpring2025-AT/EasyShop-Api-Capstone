package org.yearup.data.mysql;

import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
                shopping_cart
                WHERE
                user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt(1);
                int quantity = rs.getInt(2);

                Product product = productDao.getById(productId);

                if (product == null) {
                    ShoppingCartItem item = new ShoppingCartItem(product, quantity);
                    cart.add(item);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cart;
    }
}
