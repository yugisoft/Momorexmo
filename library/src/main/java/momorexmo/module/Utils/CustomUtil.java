package momorexmo.module.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import momorexmo.module.AppSettings;
import momorexmo.module.DateTime;
import momorexmo.module.FiledAttributes.Json;
import momorexmo.module.Utils.CustomBinding.BindProperty;
import momorexmo.module.Utils.CustomBinding.BindingBoolean;
import momorexmo.module.Utils.CustomBinding.BindingDateTime;
import momorexmo.module.Utils.CustomBinding.BindingImage;
import momorexmo.module.Utils.CustomBinding.IBindableModel;
import momorexmo.module.vList;


public class CustomUtil
{

    public static Field getField(Object ob, String fieldName) {
        Field field = null;
        try
        {
            Class cls = ob.getClass();
            do
            {
                try{field =  cls.getField(fieldName);}catch (Exception ignored){}
                if (field == null)
                    try{field =  cls.getDeclaredField(fieldName);}catch (Exception ignored){}
                cls = cls.getSuperclass();
            }
            while (field==null && cls != null && cls != Object.class);

        }
        catch (Exception ignored){}

        return field;
    }


    private static Object getValue(Object ob, Field field) {

        try
        {
            if (field != null)
            {
                Class typeClass = field.getType();
                field.setAccessible(true);
                Object pValue = field.get(ob);
                if (typeClass.equals(Integer.class) ||typeClass.equals(int.class))
                {
                    if (field.isAnnotationPresent(BindingImage.class))
                    {
                        int ret = field.getAnnotation(BindingImage.class).defaultResource();
                        if (parse.toInt(pValue) == 0 && ret > 0)
                            return  ret;
                    }
                    return parse.NF(pValue,0);
                }

                if (typeClass.equals(Long.class) ||typeClass.equals(long.class))
                {
                    return parse.NF(pValue,0);
                }
                if (typeClass.equals(Double.class) ||typeClass.equals(double.class))
                {
                    return parse.NF(pValue,2);
                }
                if (typeClass.equals(Boolean.class) ||typeClass.equals(boolean.class))
                {
                    if (field.isAnnotationPresent(BindingBoolean.class))
                    {
                        switch (field.getAnnotation(BindingBoolean.class).BooleanType())
                        {

                            case BOOLEAN_TYPE:
                                return pValue;
                            case TEXT_TYPE:
                                return  pValue.equals(true) ? field.getAnnotation(BindingBoolean.class).EnableText() : field.getAnnotation(BindingBoolean.class).DisableText();
                        }
                    }
                    else
                    {
                        return pValue;
                    }
                }
                if (typeClass.equals(DateTime.class))
                {
                    if (field.isAnnotationPresent(BindingDateTime.class))
                    {
                        switch (field.getAnnotation(BindingDateTime.class).DateTimeType())
                        {

                            case ShortDate:
                                return ((DateTime)pValue).toShortDateString();
                            case LongDate:
                                return ((DateTime)pValue).toLongDateString();
                            case ShortTime:
                                return ((DateTime)pValue).toShortTimeString();
                            case LongTime:
                                return ((DateTime)pValue).toLongTimeString();
                            case ShortDateTime:
                                return ((DateTime)pValue).toShortDateTimeString();
                            case LongDateTime:
                                return ((DateTime)pValue).toLongDateTimeString();
                        }
                    }
                    else
                        return ((DateTime)pValue).toShortDateString();
                }
                else if(pValue instanceof IBindableModel)
                {
                    return  ((IBindableModel)pValue).getValue(field.getName());
                }
                else if (typeClass.equals(String.class))
                {
                    if (field.isAnnotationPresent(BindingImage.class))
                    {
                        String ret = field.getAnnotation(BindingImage.class).defaultUrl();
                        if (String.valueOf(pValue).length() ==0 && ret.length() > 0)
                            return  ret;
                    }
                    return pValue;
                }
                else
                {
                    return pValue;
                }
            }
        }
        catch (Exception ignored)
        {}
        return  null;

    }
    public static Object getValue(Object ob, String fieldName) {
        try
        {
            Field field =  getField(ob,fieldName);

            Object value = getValue(ob,field);

            BindProperty prop = getFieldProperty(field);

            if (prop.Format().length()>0)
            {
                try{ return parse.Formatter.purify(prop.Format(),ob );}catch (Exception ignored){}
            }
            return  value;

        }
        catch (Exception ignored){}
        return  null;
    }
    public static Object getFieldValue(Object ob, String fieldName) {
        Object pValue = null;
        try
        {
            Field field =  getField(ob,fieldName);
            if (field != null)
            {
                field.setAccessible(true);
                pValue = field.get(ob);
                field.setAccessible(false);
            }
        }
        catch (Exception ignored) { }
        finally
        {
            return pValue;
        }

    }
    public static List<Field> getFields(Object object) {
        Class cls = object.getClass();
        List<Field> list = new ArrayList<>();
        try
        {
            do
            {
                list =  vList.Merge(list, Arrays.asList(cls.getFields())).list;
                list =  vList.Merge(list, Arrays.asList(cls.getDeclaredFields())).list;
                cls = cls.getSuperclass();
            }
            while (cls != null && !cls.equals(Object.class) && cls!=cls.getSuperclass());
        }
        catch (Exception ignored){}

        return list;
    }
    public static Field getFieldForJsonObject(vList<Field> fields, String name) {
        Field field = null;
        try{field = fields.Filter(f-> f.getName().equals(name)).get(0);}catch (Exception ex){}
        if (field == null)
        {
            try
            {
                field = fields.Filter(f->
                {
                    Json js = f.getAnnotation(Json.class);
                    if (js == null)
                        return false;
                    return js.name().equals(name);
                }).get(0);
            }
            catch (Exception ex)
            {

            }
        }
        return field;

    }
    public static BindProperty getFieldProperty(Object ob, String fieldName) {
        return getFieldProperty(getField(ob,fieldName));
    }
    public static BindProperty getFieldProperty(Field field) {
        if (field.isAnnotationPresent(BindProperty.class))
            return  field.getAnnotation(BindProperty.class);
        else
            return new BindProperty()
            {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public boolean isNullSetInvisible() {
                    return false;
                }

                @Override
                public boolean isEmptySetInvisible() {
                    return false;
                }

                @Override
                public String DisplayIdName() {
                    return "";
                }

                @Override
                public String Format() {
                    return "";
                }

                @Override
                public boolean IgnoreViewFormat() {
                    return true;
                }
            };
    }
}
