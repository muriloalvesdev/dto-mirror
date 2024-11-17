package io.github.mirror.dto.aspect;

import io.github.mirror.dto.annotation.ExcludeFields;
import io.github.mirror.dto.annotation.MapField;
import java.lang.reflect.Field;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DTOMirrorAspect {

  @Autowired
  private ApplicationContext applicationContext;

  @Pointcut(
          "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
  public void controllerPointcut() {}

  @AfterReturning(
          pointcut = "controllerPointcut() && execution(public * *(..)) && @annotation(excludeFields)",
          returning = "result")
  public void apply(ExcludeFields excludeFields, Object result) throws IllegalAccessException {
    if (result != null) {
      Class<?> resultClass = result.getClass();
      SpelExpressionParser parser = new SpelExpressionParser();

      StandardEvaluationContext context = new StandardEvaluationContext(result); // Define o objeto raiz como `result`
      context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));

      for (Field field : resultClass.getDeclaredFields()) {
        field.setAccessible(true);

        if (shouldExcludeField(field, excludeFields)) {
          exclude(result, field);
          continue;
        }

        var mapFieldAnnotation = field.getAnnotation(MapField.class);
        if (mapFieldAnnotation != null) {
          String expression = mapFieldAnnotation.expression();
          try {
            context.setVariable(field.getName(), field.get(result));
            Object transformedValue = parser.parseExpression(expression).getValue(context);

            field.set(result, transformedValue);
          } catch (Exception e) {
            throw new IllegalAccessException("Error processing MapField expression: " + expression + " - " + e.getMessage());
          }
        }
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
