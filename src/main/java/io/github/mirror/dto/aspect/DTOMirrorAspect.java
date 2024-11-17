package io.github.mirror.dto.aspect;

import io.github.mirror.dto.annotation.DTOProcessor;
import io.github.mirror.dto.annotation.ExcludeFields;
import io.github.mirror.dto.annotation.MapField;
import java.lang.reflect.Field;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * Aspect responsible for processing DTO transformations based on annotations such as {@link
 * MapField} and {@link ExcludeFields}, applied only to classes annotated with {@link DTOProcessor}.
 *
 * <p><b>Author:</b> muriloalvesdev
 */
@Aspect
@Component
public class DTOMirrorAspect {

  @Autowired private ApplicationContext applicationContext;

  /** Pointcut that matches any method within classes annotated with {@link DTOProcessor}. */
  @Pointcut("@within(io.github.mirror.dto.annotation.DTOProcessor)")
  public void dtoProcessorAnnotatedClass() {}

  /**
   * Pointcut that matches any public method within classes annotated with {@link RestController} or
   * {@link Controller}.
   */
  @Pointcut(
      "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
  public void controllerPointcut() {}

  /**
   * Executes only for public methods in classes annotated with both {@link RestController} (or
   * {@link Controller}) and {@link DTOProcessor}, and applies transformations or exclusions as
   * specified in the annotations.
   */
  @AfterReturning(
      pointcut =
          "dtoProcessorAnnotatedClass() && controllerPointcut() && execution(public * *(..)) && @annotation(excludeFields)",
      returning = "result")
  public void apply(ExcludeFields excludeFields, Object result) throws IllegalAccessException {
    if (result != null) {
      Class<?> resultClass = result.getClass();
      SpelExpressionParser parser = new SpelExpressionParser();

      StandardEvaluationContext context =
          new StandardEvaluationContext(result);
      context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));

      for (Field field : resultClass.getDeclaredFields()) {
        field.setAccessible(true);
        processMapFields(result, field, context, parser);
        processExcludeFields(excludeFields, result, field);
      }
    }
  }

  private void processExcludeFields(ExcludeFields excludeFields, Object result, Field field)
      throws IllegalAccessException {
    if (shouldExcludeField(field, excludeFields)) {
      exclude(result, field);
    }
  }

  private void processMapFields(
      Object result, Field field, StandardEvaluationContext context, SpelExpressionParser parser)
      throws IllegalAccessException {
    var mapFieldAnnotation = field.getAnnotation(MapField.class);
    if (mapFieldAnnotation != null) {
      String expression = mapFieldAnnotation.expression();
      try {
        context.setRootObject(result);

        Object transformedValue = parser.parseExpression(expression).getValue(context);

        field.setAccessible(true);
        field.set(result, transformedValue);
      } catch (Exception e) {
        throw new IllegalAccessException(
            "Error processing MapField expression: " + expression + " - " + e.getMessage());
      }
    }
  }

  private void exclude(Object result, Field field) throws IllegalAccessException {
    field.set(result, null);
  }

  private boolean shouldExcludeField(Field field, ExcludeFields excludeFields) {
    for (String fieldName : excludeFields.value()) {
      if (field.getName().equals(fieldName)) {
        return true;
      }
    }
    return false;
  }
}
