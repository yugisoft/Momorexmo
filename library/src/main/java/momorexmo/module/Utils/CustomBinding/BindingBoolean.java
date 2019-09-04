package momorexmo.module.Utils.CustomBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindingBoolean
{
    public String EnableText()  default "Aktif";
    public String DisableText() default "Pasif";
    public BindingBooleanType BooleanType() default BindingBooleanType.TEXT_TYPE;
    public enum BindingBooleanType {
        BOOLEAN_TYPE,TEXT_TYPE
    }
}
