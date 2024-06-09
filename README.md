# Project Name: My Portfolio and Blog Application (Backend)

This is the backend part of My Portfolio and Blog Application, developed using Spring Boot. This backend API supports the frontend developed with Next.js by providing endpoints for managing blog posts, portfolios, user authentication, and handling contact messages.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Setup](#setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Contact](#contact)

## Features
- **Blog Management**: CRUD operations for blog posts.
- **Portfolio Management**: CRUD operations for portfolios (CVs).
- **User Authentication**: Register, login, and password reset functionalities.
- **Contact Messages**: Receive and manage messages from the contact page.
- **Admin Panel**: Administrative functionalities accessible by admin users.

## Prerequisites
Before you begin, ensure you have met the following requirements:
- Java Development Kit (JDK) (>= 11)
- Maven
- MySQL or any other SQL database
- Git (optional but recommended)

## Installation
To install the backend application, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/your-backend-repo-name.git
    ```

2. Navigate to the project directory:
    ```bash
    cd your-backend-repo-name
    ```

3. Install the dependencies:
    ```bash
    mvn install
    ```

## Setup
1. Create a `application.yaml` file in the `src/main/resources` directory if it doesn't already exist.

2. Configure the necessary settings in the `application.yaml` file. Below is a sample configuration:
    ```yaml
    server:
      port: 8080

    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/your_database_name
        username: your_database_username
        password: your_database_password
        driver-class-name: com.mysql.cj.jdbc.Driver
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true

    jwt:
      secret: your_jwt_secret
      expiration: 86400000

    mail:
      host: smtp.your-email-provider.com
      port: 587
      username: your_email@example.com
      password: your_email_password
      properties:
        mail:
          smtp:
            auth: true
            starttls:
              enable: true

    admin:
      email: admin@example.com
      password: admin_password
    ```

3. Ensure your database is set up and running. Update the `application.yaml` file with your database connection details.

## Running the Application
To run the application, use the following command:

```bash
mvn spring-boot:run
```

## Project Structure
The project structure is as follows:
```
your-backend-repo-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── yourpackage/
│   │   │           ├── controller/     # REST controllers
│   │   │           ├── model/          # Entity models
│   │   │           ├── repository/     # Repository interfaces
│   │   │           ├── service/        # Service layer
│   │   │           └── YourApplication.java # Main application class
│   │   ├── resources/
│   │       ├── application.yaml        # Configuration file
│   │       └── templates/              # Email templates
├── pom.xml                              # Maven configuration file
└── README.md                            # Project documentation
```

## Configuration
All configuration settings for the application, including database connections, admin user credentials, and email service settings, can be found and modified in the `application.yaml` file located in the `src/main/resources` directory.

### Database Configuration
Ensure that your `application.yaml` file contains the correct database URL, username, and password. Example for MySQL:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database_name
    username: your_database_username
    password: your_database_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Admin User Configuration
Set the default admin user email and password:
```yaml
admin:
  email: admin@example.com
  password: admin_password
```

### Email Service Configuration
Configure the email service to send emails (e.g., for password reset):
```yaml
mail:
  host: smtp.your-email-provider.com
  port: 587
  username: your_email@example.com
  password: your_email_password
  properties:
    mail:
      smtp:
        auth: true
        starttls:
          enable: true
```

## Contact
If you have any questions or need further assistance, please feel free to contact me at [your-email@example.com].

Thank you for using My Portfolio and Blog Application Backend!