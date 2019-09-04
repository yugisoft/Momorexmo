package momorexmo.module.Utils.CustomBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindingDateTime
{
    public BindingDateTimeType DateTimeType() default BindingDateTimeType.ShortDate;
    public enum BindingDateTimeType
    {
        ShortDate,LongDate,ShortTime,LongTime,ShortDateTime,LongDateTime
    }
}
