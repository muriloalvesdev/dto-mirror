# DTO Mirror

The DTO Mirror library is a utility for Spring Boot that allows the automatic creation of DTOs from entities, facilitating the secure transfer of data in API responses.


### @ExcludeFields annotation

The @ExcludeFields annotation allows you to specify which fields should be excluded when mirroring an entity to a DTO. 
It can be applied to methods in classes annotated with @RestController or @Controller.
_________________________________________________________________________________
Example of use:
```java
@ExcludeFields({"id"})
public Person getPerson(String id){
    return repository.findById(id);
}
```

In this example, the getPerson method returns a Person object, but the id field will be set to null in the resulting DTO.
_________________________________________________________________________________

### Aspect ExcludeFieldsAspect
The ExcludeFieldsAspect aspect is responsible for intercepting the methods annotated with @ExcludeFields and performing the exclusion of the specified fields.

It operates on the methods of classes annotated with @RestController or @Controller, ensuring that only the desired fields are included in API responses.


### Features

- Facilitates Security: Excluding sensitive fields from DTOs ensures that only necessary data is exposed in API responses, improving security.
- Reduces Code Repetition: Eliminates the need to manually create DTOs for each entity, saving development time and effort.
- Simple Integration: Integration with Spring Boot projects is simple and straightforward, requiring only the annotation of relevant methods with @ExcludeFields.

With the DTO Mirror library, you can simplify the creation of DTOs in your Spring Boot projects and ensure secure data transfer across your APIs.
