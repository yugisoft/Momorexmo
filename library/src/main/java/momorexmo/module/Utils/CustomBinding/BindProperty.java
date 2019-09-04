package momorexmo.module.Utils.CustomBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindProperty
{
    public boolean isNullSetInvisible() default false;
    public boolean isEmptySetInvisible() default false;
    public String DisplayIdName() default "";
    public String Format() default "";
    public boolean IgnoreViewFormat() default true;

}
