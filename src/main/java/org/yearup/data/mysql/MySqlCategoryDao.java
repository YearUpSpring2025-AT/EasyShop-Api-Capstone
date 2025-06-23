package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                category_id
                name
                FROM
                categories
                """;
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(allCategorySql);
             ResultSet resultSet = statement.executeQuery();
        ){
            while (resultSet.next())  {
                Category category = new Category();
                category.setCategoryId(resultSet.getInt("category_id"));
                category.setName(resultSet.getString("name"));
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
                category_id
                name
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
                    int categoryId = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    Category category = new Category(categoryId, name);
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
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
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
