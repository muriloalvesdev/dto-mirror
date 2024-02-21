/**
 * Aspect for excluding specified fields from the response of controller methods annotated with RestController
 * or Controller annotations.
 */
package dto.core.aspect;

import dto.core.annotation.ExcludeFields;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class ExcludeFieldsAspect {

    /**
     * Pointcut definition for methods annotated with RestController or Controller annotations.
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {
    }

    /**
     * Advice to execute after a method annotated with RestController or Controller annotations returns.
     * This advice will exclude specified fields from the response.
     *
     * @param excludeFields The ExcludeFields annotation specifying fields to be excluded.
     * @param result        The result object returned from the method.
     * @throws IllegalAccessException If an illegal access exception occurs during field modification.
     */
    @AfterReturning(pointcut = "controllerPointcut() && execution(public * *(..)) && @annotation(excludeFields)", returning = "result")
    public void excludeFieldsFromResponse(ExcludeFields excludeFields, Object result) throws IllegalAccessException {
        if (result != null) {
            Class<?> resultClass = result.getClass();
            for (Field field : resultClass.getDeclaredFields()) {
                if (shouldExcludeField(field, excludeFields)) {
                    field.setAccessible(true);
                    field.set(result, null);
                }
            }
        }
    }

    /**
     * Determines whether a given field should be excluded based on the ExcludeFields annotation.
     *
     * @param field         The field to be checked for exclusion.
     * @param excludeFields The ExcludeFields annotation specifying fields to be excluded.
     * @return True if the field should be excluded, false otherwise.
     */
    private boolean shouldExcludeField(Field field, ExcludeFields excludeFields) {
        for (String fieldName : excludeFields.value()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}
