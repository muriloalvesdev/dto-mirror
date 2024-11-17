package io.github.mirror.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a custom mapping logic for a field when converting between an entity and a
 * DTO.
 *
 * <p>The `expression` attribute allows defining a transformation using SpEL (Spring Expression
 * Language) or a bean reference.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapField {
  /**
   * Defines the custom mapping expression to transform the field value. This can be a SpEL
   * expression or a reference to a Spring bean method.
   *
   * @return the transformation expression
   */
  String expression();
}
