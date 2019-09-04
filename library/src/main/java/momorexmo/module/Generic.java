package momorexmo.module;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class Generic<T> implements IGeneric<T>
{
    public static Object getGenericInstance(Field field) {
        try
        {
            Class<?> cl = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
            return  cl.newInstance();
        }
        catch (Exception ex)
        {
            return  null;
        }
    }

    List<T> asda;

    public Class<T> genericClass=null;


    @Override
    public Class<T> getGenericClass() {

        try
        {
            Type parametrizedType = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (parametrizedType instanceof Class) {

            } else if (parametrizedType instanceof WildcardType) {

            } else if (parametrizedType instanceof TypeVariable)
            {
                TypeVariable variable = (TypeVariable)parametrizedType;
                Type boundType = ((TypeVariable<?>) variable).getBounds()[0];
                return (Class<T>) boundType;

            } else {

            }
           this.getClass().getTypeParameters();
            genericClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        catch (Exception ex)
        {
            AppSettings.Print("e","genericClass Can Not be Created! : in Constructer");
        }
        return genericClass;
    }

    @Override
    public T getGenecericInstance() {
        try
        {
            Method method = this.getClass().getMethod("getGenecericInstance");
            method.getGenericParameterTypes();
            return  getGenericClass().newInstance();
        }
        catch (Exception ex)
        {
            AppSettings.Print("e","genericClass Can Not be Created! : "+ex.getMessage());
            return  null;
        }
    }
}
