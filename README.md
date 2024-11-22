# DTO Mirror

#### Disclaimer: This project is experimental and is currently being developed as a hobby. While it is not yet production-ready, it has the potential to evolve into a fully-fledged library in the future.

DTO Mirror is a utility library for Spring Boot that simplifies the creation and transformation of DTOs from entities. It helps ensure secure and flexible data transfer in API responses by automating field exclusion and data mapping.

### Features
- Facilitates Security: Exclude sensitive fields from DTOs to ensure only necessary data is exposed in API responses.
- Reduces Code Repetition: Avoid manual DTO creation by automating entity-to-DTO transformations.
- Data Transformation: Use expressions to transform field values dynamically during the DTO creation process.
- Simple Integration: Easy to integrate with Spring Boot projects using annotations like `@ExcludeFields` and `@MapField`.

### Annotations
## @ExcludeFields Annotation

The `@ExcludeFields` annotation allows you to exclude specific fields from the response DTO. It is applied to methods within classes annotated with `@RestController` or `@Controller`.

Example: 
```java
@ExcludeFields({"id"})
@GetMapping("users/{id}")
public Person getPerson(@PathVariable String id) {
    return repository.findById(id);
}
```
In this example, the id field will be excluded from the Person object in the API response by setting it to null.

## @MapField Annotation

The `@MapField` annotation enables dynamic transformation of field values using SpEL (Spring Expression Language) expressions. It can be applied to fields within entities or DTO classes.

```java
public class User {

  @MapField(expression = "fullName?.toUpperCase()")
  private String fullName;

  @MapField(
      expression = "fullName?.split(' ')?.length > 0 ? fullName?.split(' ')[0]?.toLowerCase() : null")
  private String username;

  // Getters and setters
}
```

- fullName: Converts the fullName field to uppercase in the resulting DTO. 
- username: Extracts the first name from fullName (split by space) and converts it to lowercase.

Combined Example: 
```java
@RestController
@DTOProcessor
@RequestMapping("/api/")
public class ExampleController {

  @ExcludeFields(value = {"city"})
  @GetMapping("users")
  public User getUser() {
    User user = new User();
    user.setFullName("Murilo Alves");
    user.setCity("São Paulo");

    return user;
  }
}

public class User {
  @MapField(expression = "fullName?.toUpperCase()")
  private String fullName;

  @MapField(
      expression =
          "fullName?.split(' ')?.length > 0 ? fullName?.split(' ')[0]?.toLowerCase() : null")
  private String username;

  private String city;

  // Getters and setters
}
```

- The city field is excluded from the API response.
- The fullName field is transformed to uppercase.
- The username field is generated dynamically from the first name in fullName.

## Installation
To use DTO Mirror in your Spring Boot project, add the following dependency:

```xml
<dependency>
  <groupId>io.github.muriloalvesdev</groupId>
  <artifactId>dto-mirror</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Benefits of DTO Mirror
- Security: Exclude sensitive data easily with `@ExcludeFields`.
- Flexibility: Transform field values dynamically with `@MapField`.
- Time-Saving: Automate repetitive DTO creation tasks, reducing boilerplate code.

Simplify and secure your API data handling with DTO Mirror!
