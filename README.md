# URL Shortener

A backend application built with **Java**, **Spring Boot**, and **PostgreSQL** that allows users to create shortened URLs and track how many times each link has been visited.

## Features

- Create short URLs from long URLs
- Redirect short URLs to the original URL
- Track click count for each shortened link
- Rate limiting to prevent abuse
- REST API tested using Postman

## Tech Stack

- Java
- Spring Boot
- PostgreSQL
- Maven
- Postman

## How It Works

1. User sends a long URL to the API
2. The server generates a unique short code
3. The short code is stored in the database with the original URL
4. When the short URL is visited, the server redirects to the original URL
5. Click count is updated on each successful redirect

## API Endpoints

- **POST** `/api`  
  Creates a short URL from the original long URL.

  **Request Body**
  ```json
  {
    "url": "https://example.com/very/long/link"
  }

- **GET** `/api/{shortCode}`  
  Redirects to the original URL for the given short code.
