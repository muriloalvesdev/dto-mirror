/**
 * Annotation used to specify fields that should be excluded from the response of methods annotated
 * with RestController or Controller annotations.
 */
package io.github.mirror.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcludeFields {

  /**
   * Specifies the names of fields that should be excluded from the response.
   *
   * @return An array of field names to be excluded.
   */
  String[] value() default {};
}
