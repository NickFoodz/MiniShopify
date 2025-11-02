# Mini-Shopify

## Project Description
Mini-Shopify is a web-based e-commerce platform built with Spring Boot that allows merchants to create online shops and manage their product inventory. Customers can browse shops by name or category and purchase products through a shopping cart system.

## Deployment
**Live Application:** [minishopify-b8gtdzgpawc9fdan.eastus2-01.azurewebsites.net](http://minishopify-b8gtdzgpawc9fdan.eastus2-01.azurewebsites.net)

## Current Features (Milestone 1)
- Merchant can create new shops with names
- Merchant can add products to shops with name, description, price, and stock quantity
- View all shops in the system
- View products associated with each shop
- Basic error handling for invalid shop IDs
- RESTful API endpoints for data access

## Technology Stack
- **Backend:** Spring Boot 3.5.6, Spring MVC, Spring Data JPA
- **Database:** H2 (in-memory)
- **Frontend:** Thymeleaf templates, HTML/CSS
- **Build Tool:** Maven
- **CI/CD:** GitHub Actions
- **Deployment:** Azure Web Apps

## Database Schema

### Entity Relationship Diagram
```
┌─────────────────┐
│    Merchant     │
├─────────────────┤
│ id (PK)         │
│ name            │
│ email           │
│ shops           │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐
│      Shop       │
├─────────────────┤
│ id (PK)         │
│ name            │
│ catigories      │
│ products        │
│ merchant_id(FK) │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐
│    Product      │
├─────────────────┤
│ id (PK)         │
│ name            │
│ description     │
│ price           │
│ stock           │
│ shop_id (FK)    │
└─────────────────┘
```

### ORM Patterns Used
- **One-to-Many:** Merchant → Shop (bidirectional)
- **One-to-Many:** Shop → Product (bidirectional)
- **Element Collection:** Shop → Categories (list of strings)
- **Cascade Operations:** CascadeType.ALL on Shop products and Merchant shops

## UML Class Diagram
See `Diagrams/UMLClass_Diagram.png` for the complete PlantUML model diagram.

### Class Diagram Legend
![img.png](img.png)

## Project Structure
```
src/
├── main/
│   ├── java/org/example/
│   │   ├── ShopAppApplication.java
│   │   ├── controllers/
│   │   │   ├── GuiController.java
│   │   │   ├── MerchantController.java
│   │   │   ├── ProductController.java
│   │   │   └── ShopController.java
│   │   ├── models/
│   │   │   ├── Merchant.java
│   │   │   ├── Shop.java
│   │   │   └── Product.java
│   │   └── repository/
│   │       ├── MerchantRepository.java
│   │       ├── ShopRepository.java
│   │       └── ProductRepository.java
│   └── resources/
│       └── templates/
│           ├── index.html
│           ├── shops.html
│           ├── add-product.html
│           └── error.html
└── test/
    └── java/org/example/
        ├── controllers/
        │    ├── GuiControllerTest.java
        │    └── RestApiIntegrationTest.java
        ├── models/
        │   ├── MerchantTest.java
        │   ├── ShopTest.java
        │   └── ProductTest.java
        └── repository/
            ├── MerchantRepositoryIntegrationTest.java
            ├── ProductRepositoryIntegrationTest.java
            └── ShopRepositoryIntegrationTest.java
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Local Development
1. Clone the repository:
   ```bash
   git clone <https://github.com/ashkkumar/MiniShopify>
   cd mini-shopify
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn package
java -jar target/Lab1-1.0-SNAPSHOT.jar
```

## API Endpoints

### REST API (Spring Data REST)
- `GET /merchants` - List all merchants
- `POST /merchants` - Create a merchant
- `GET /shops` - List all shops
- `POST /shops` - Create a shop
- `GET /products` - List all products
- `POST /products` - Create a product

### GUI Endpoints
- `GET /gui/` - Home page
- `GET /gui/shops` - View all shops
- `POST /gui/shops` - Add a new shop
- `GET /gui/add-product` - Add product form
- `POST /gui/add-product` - Submit new product

## Current Sprint Status (Milestone 1)

### Completed
- Project setup with CI/CD pipeline
- Azure deployment configuration
- Basic models (Merchant, Shop, Product)
- Repository layer with Spring Data JPA
- GUI for shop and product management
- Unit tests for all models
- Integration tests for controllers and repositories
- Product-Shop relationship implementation

### Planned for Next Sprint
- Shop categories functionality
- Customer shopping cart
- Product search by category
- Merchant authentication and login
- Customer user accounts
- Shopping cart implementation
- Checkout process (simulated payment)
- Search functionality (by shop name and category)

## Team Members
- Ajen Srisivapalan
- Andrew Roberts
- Ashwin Kumar
- Jason Keah
- Nick Fuda

## Project Management
- **[Kanban Board](https://github.com/users/ashkkumar/projects/4)** 
- **Weekly Scrums:** See GitHub Issues for weekly updates
- **CI/CD:** Automated via GitHub Actions

## License
This project is created as part of SYSC 4806 coursework.