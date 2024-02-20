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

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {}

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

    private boolean shouldExcludeField(Field field, ExcludeFields excludeFields) {
        for (String fieldName : excludeFields.value()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}

