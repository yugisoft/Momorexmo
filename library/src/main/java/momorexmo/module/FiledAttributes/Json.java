package momorexmo.module.FiledAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json
{
    public String name();
    public boolean ignoreToJson() default  false;
    public boolean ignoreJsonTo() default  false;
}