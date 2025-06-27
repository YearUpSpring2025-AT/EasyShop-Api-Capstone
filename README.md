# 🛍️ EasyShop – E-Commerce API Capstone Project

EasyShop is a full-stack e-commerce API built with **Spring Boot** and **MySQL**. It includes secure login, JWT authentication, product and category management, and a shopping cart system. Users can filter products and manage their cart, while admins can manage categories and products. The application is tested with Postman and includes a working front-end demo.

---

## 🚀 Features

- JWT-based login and token authentication  
- Role-based access control (admin vs user)  
- Shopping cart with quantity updates  
- Product and category filtering  
- REST API tested with Postman  
- MySQL-backed data persistence  
- MVC architecture with DAO and controller layers  

---

## 📂 Project Structure

```
src
├── configurations
├── controllers
├── data
│   ├── dao
│   └── mysql
├── models
├── security
└── EasyshopApplication.java
```

---

## 🖼️ Screenshots

You can download all screenshots [here](sandbox:/mnt/data/easyshop-readme-images-v2.zip) and upload them to your README or GitHub repo.

**Included Images:**

1. `easyshop-ui-home.png` – Homepage UI with category filters and product cards  
2. `add-to-cart-error.png` – Visual alert when a cart addition fails  
3. `postman-401-error.png` – Postman error when JWT is missing or invalid  
4. `postman-405-error.png` – Incorrect HTTP method used on endpoint  
5. `jwt-token-doc.png` – Documentation showing JWT response after login  
6. `sql-syntax-error-updated.png` – SQL syntax bug in DAO during testing

---

## 💡 Interesting Code Snippet

```java
String sql = """
    INSERT INTO shopping_cart_items (user_id, product_id, quantity)
    VALUES (?, ?, ?)
    ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
    """;

try (Connection connection = getConnection()) {
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, userId);
    statement.setInt(2, productId);
    statement.setInt(3, quantity);
    statement.executeUpdate();
}
```

**What it does:**  
This SQL statement automatically increments the quantity if a user already added the same product to their cart. It combines insert and update in one efficient query.

---

## 🧪 How to Test (Postman)

1. Use this login payload:
```json
{
  "username": "admin",
  "password": "password"
}
```

2. Copy the token returned from `/login` and include it in your headers:
```
Authorization: Bearer <your-token-here>
```

3. Use this token to access protected endpoints like:
- `/cart`
- `/categories`
- `/products`

---

## 🐛 Known Issues & Fixes

| Issue | Fix |
|-------|-----|
| 401 Unauthorized | Make sure the user exists and is activated in the database |
| 405 Method Not Allowed | Double-check the method type used in your request matches the controller annotation |
| SQLSyntaxErrorException | Fix SQL string formatting or missing column/values in your DAO |
| Spring dependency errors | Run `mvn clean install`, and verify `pom.xml` dependencies are intact |

---

## ⚙️ How to Run

```bash
# 1. Clone this repository
git clone https://github.com/your-username/easyshop-api-capstone.git
cd easyshop-api-capstone

# 2. Import MySQL database using the provided SQL script

# 3. Edit `application.properties` with your DB username and password

# 4. Run the app using Maven
mvn spring-boot:run
```

---

## 📜 License

MIT License
