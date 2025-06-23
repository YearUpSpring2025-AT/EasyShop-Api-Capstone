package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    private BasicDataSource basicDataSource;

    public MySqlCategoryDao(DataSource dataSource, BasicDataSource basicDataSource) {
        super(dataSource);
        this.basicDataSource = basicDataSource;
    }

    @Override
    public List<Category> getAllCategories()
    {
        ArrayList<Category> categories = new ArrayList<>();

        String allCategorySql = """
                SELECT
                category_id,
                name,
                description
                FROM
                categories
                """;
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(allCategorySql);
             ResultSet resultSet = statement.executeQuery();
        ){
            while (resultSet.next())  {
                Category category = new Category();
                category.setCategoryId(resultSet.getInt(1));
                category.setName(resultSet.getString(2));
                category.setDescription(resultSet.getString(3));
                categories.add(category);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String idByQuery = """
                SELECT
                category_id,
                name,
                description
                FROM
                categories
                WHERE category_id = ?
                """;

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(idByQuery);

        ){
            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    Category category = new Category();
                    category.setCategoryId(resultSet.getInt(1));
                    category.setName(resultSet.getString(2));
                    category.setDescription(resultSet.getString(3));
                    return category;
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String createSql = "INSERT INTO categories (name) VALUE (?)";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement =  connection.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS);
        ){
            statement.setString(1, category.getName());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    category.setCategoryId(generatedId);
                    return category;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String updateSql = "UPDATE categories AET  name = ? WHERE category_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement =  connection.prepareStatement(updateSql);
        ){
            statement.setString(1, category.getName());
            statement.setInt(2, categoryId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String deleteSql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSql);
        ){
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
