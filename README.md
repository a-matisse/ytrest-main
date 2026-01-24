# YTRest

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue?style=for-the-badge&logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge&logo=apache&logoColor=white)

![Apache HTTP Client](https://img.shields.io/badge/Apache%20HTTP%20Client-5.6-red?style=flat-square&logo=apache&logoColor=white)
![GSON](https://img.shields.io/badge/GSON-2.13.2-green?style=flat-square&logo=json&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-1.18.42-pink?style=flat-square&logo=lombok&logoColor=white)

**A minimal, type-safe synchronous HTTP client library for Java**

[Features](#features) • [Quick Start](#quick-start) • [API Reference](#api-reference) • [Examples](#quick-start)

</div>

---

YTRest is a lightweight Java library for making HTTP requests and consuming REST APIs. It provides a simple, type-safe interface for synchronous HTTP operations with automatic JSON serialization and deserialization. Perfect for Java developers who need a straightforward alternative to complex HTTP client libraries.

## Features

- 🚀 **Type-safe** - Generic type support with GSON TypeToken
- 🔧 **Simple API** - Clean and intuitive builder pattern
- 📦 **Lightweight** - Minimal dependencies
- ⏰ **Java Time Support** - Built-in adapters for `LocalDate`, `LocalDateTime`, and `LocalTime`
- 🌐 **Full HTTP Support** - All standard HTTP methods (GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE)
- 🎯 **Error Handling** - Structured response with status codes
- 🔄 **REST API Client** - Perfect for consuming REST APIs
- 📡 **HTTP Client Library** - Built on Apache HTTP Client 5
- 🎨 **JSON Serialization** - Automatic JSON serialization/deserialization with GSON

## What is YTRest?

YTRest is a **Java HTTP client library** that simplifies making HTTP requests and working with REST APIs. It's designed as a **wrapper around Apache HTTP Client 5** with built-in **GSON support** for JSON handling. Whether you need to call REST APIs, make HTTP requests, or consume web services, YTRest provides a clean, type-safe interface without the complexity of larger frameworks.

### Use Cases

- Consuming REST APIs in Java applications
- Making HTTP requests with automatic JSON serialization
- Building API clients and SDKs
- Integrating with third-party web services
- Simple HTTP operations without heavy dependencies

## Requirements

- Java 17 or higher
- Maven 3.6+

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>cs.youtrade</groupId>
    <artifactId>ytrest</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Basic Usage

```java
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import cs.youtrade.ytrest.YtSyncRestClient;
import com.google.gson.reflect.TypeToken;

// Initialize the client with a base URL
YtSyncRestClient client = new YtSyncRestClient("https://api.example.com/");

// Make a GET request
RestAnswer<ExampleDto> response = client.fetchFromApi(
    HttpMethod.GET,
    "users/1",
    new TypeToken<ExampleDto>() {}.getType()
);

// Check the response
if (response.getStatus() == 200) {
    ExampleDto data = response.getResponse();
    System.out.println(data);
} else {
    System.err.println("Error: " + response.getStatus());
}
```

### With Request Body

```java
// Create a request object
UserRequest userRequest = new UserRequest("John Doe", "john@example.com");

// Make a POST request
RestAnswer<UserResponse> response = client.fetchFromApi(
    HttpMethod.POST,
    "users",
    userRequest,
    new TypeToken<UserResponse>() {}.getType()
);
```

### With Headers and Query Parameters

```java
import java.util.Map;
import java.util.HashMap;

// Set headers
Map<String, String> headers = new HashMap<>();
headers.put("Authorization", "Bearer token123");
headers.put("X-Custom-Header", "value");

// Set query parameters
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("limit", "10");

// Make a request with headers and parameters
RestAnswer<List<Item>> response = client.fetchFromApi(
    HttpMethod.GET,
    "items",
    headers,
    params,
    new TypeToken<List<Item>>() {}.getType()
);
```

### Using with Jackson (FasterXML)

The library accepts any `Type` object, so you can use it with Jackson as well:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;

ObjectMapper mapper = new ObjectMapper();
JavaType type = mapper.getTypeFactory()
    .constructType(ExampleDto.class)
    .getRawClass();

RestAnswer<ExampleDto> response = client.fetchFromApi(
    HttpMethod.GET,
    "todos/1",
    type
);
```

## API Reference

### YtSyncRestClient

The main client class for making HTTP requests.

#### Constructors

- `YtSyncRestClient(String baseUrl)` - Creates a client with default HTTP client
- `YtSyncRestClient(String baseUrl, CloseableHttpClient httpClient)` - Creates a client with custom HTTP client

#### Methods

- `fetchFromApi(HttpMethod method, String endpoint, Type type)` - Simple GET request
- `fetchFromApi(HttpMethod method, String endpoint, Object body, Type type)` - Request with body
- `fetchFromApi(HttpMethod method, String endpoint, Map<String, String> headers, Object body, Type type)` - Request with headers and body
- `fetchFromApi(HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Type type)` - Request with headers and query parameters
- `fetchFromApi(HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Object body, Type type)` - Full-featured request

### RestAnswer<T>

A wrapper class that contains the HTTP response.

- `int getStatus()` - HTTP status code
- `T getResponse()` - Deserialized response object (null if status is not 2xx)

### HttpMethod

Enum representing HTTP methods:
- `GET`
- `POST` (allows body)
- `PUT` (allows body)
- `PATCH` (allows body)
- `DELETE`
- `HEAD`
- `OPTIONS`
- `TRACE`
- `CONNECT`

## Java Time Support

The library includes built-in GSON adapters for Java Time API:

- `LocalDate`
- `LocalDateTime`
- `LocalTime`

These are automatically registered and will be serialized/deserialized correctly.

## Error Handling

The library handles errors gracefully:

- Network errors return a `RestAnswer` with status code 500
- HTTP errors (non-2xx status codes) return a `RestAnswer` with the actual status code and null response
- Always check `response.getStatus()` before accessing `response.getResponse()`

## Example

See the `example` module for a complete working example.

## Dependencies

- Apache HTTP Client 5.6
- GSON 2.13.2 (optional, for JSON serialization)
- Lombok 1.18.42 (compile-time only)

## License

See [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Keywords

`java` `http-client` `rest-client` `api-client` `apache-httpclient` `gson` `type-safe` `synchronous` `rest-api` `http-request` `json` `java-http-client` `rest-library` `api-wrapper` `http-wrapper` `java-rest-client` `apache-httpclient5` `gson-http-client` `type-safe-http` `java-api-client` `rest-client-library` `http-client-library` `java-rest-api` `synchronous-http-client` `java-17` `maven`
