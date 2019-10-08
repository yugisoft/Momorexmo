package momorexmo.module.Utils;

import org.json.*;
import java.lang.reflect.Field;
import java.util.*;
import momorexmo.module.*;
import momorexmo.module.FiledAttributes.Json;
import momorexmo.module.Interfaces.ASerializable;
import momorexmo.module.Interfaces.ISerializable;

import static momorexmo.module.Utils.parse.*;



public class JsonConverter<T> {
    public JsonConverter() {
    }

    public JsonConverter(String jsonData, Class<T> tClass) {
        setJsonData(jsonData);
        settClass(tClass);
    }


    //region jsonData
    private String jsonData;

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
    //endregion

    //region targetObject
    private Object targetObject;

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }
    //endregion

    //region tClass
    private Class<T> tClass;

    public void settClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    public Class<T> gettClass() {
        return tClass;
    }
    //endregion

    //region key
    private String key = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    //endregion

    //region index
    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    //endregion

    public T convertToClass() {
        T item = null;

        try {
            if (getJsonData().trim().substring(0, 1).equals("[")) {
                JSONArray jsonArray = new JSONArray(getJsonData());
                JSONObject jsonObject = jsonArray.getJSONObject(getIndex());

                if (getKey().length() > 0) {
                    try {
                        jsonObject = jsonObject.getJSONObject(getKey());
                    } catch (Exception ignored) {
                        try {
                            jsonObject = jsonObject.getJSONArray(getKey()).getJSONObject(0);
                        } catch (Exception ignored2) {
                            return null;
                        }
                    }
                }

                item = jsonObjectCaster(jsonObject);

            } else {
                JSONObject jsonObject = new JSONObject(getJsonData());
                item = jsonObjectCaster(jsonObject);
            }
        }

        //region Exception
        catch (JSONException jex) {
            AppSettings.Print("e", "JsonConverter.convertToClass", jex.getMessage());
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertToClass", ex.getMessage());
        }
        //endregion

        return item;
    }

    public List<T> convertToClassList() {
        List<T> items = new ArrayList<>();
        try {
            if (!getJsonData().trim().substring(0, 1).equals("["))
                setJsonData("[" + getJsonData() + "]");
            JSONArray jsonArray = new JSONArray(getJsonData());

            if (getKey().length() > 0) {

                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(getIndex());
                    jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);

                } catch (Exception ignored) {
                    try {
                        jsonArray = jsonArray.getJSONArray(getIndex());
                    } catch (Exception ignored2) {
                        return null;
                    }
                }
            }

            items = jsonArrayCaster(jsonArray);
        } catch (JSONException jex) {
            AppSettings.Print("e", "JsonConverter.convertToClass", jex.getMessage());
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertToClass", ex.getMessage());
        }

        return items;
    }

    private T jsonObjectCaster(JSONObject jsonObject) {


        T item = null;
        try {
            item = (T) (targetObject == null ? tClass.newInstance() : targetObject);
            vList<Field> fields = new vList();
            fields.list = CustomUtil.getFields(item);

            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                Field f = CustomUtil.getFieldForJsonObject(fields, key);

                if (f != null) {
                    f.setAccessible(true);
                    String fType = f.getType().getSimpleName().toLowerCase();
                    if (f.isAnnotationPresent(Json.class)) {
                        Json json = f.getAnnotation(Json.class);
                        if (json.ignoreJsonTo())
                            continue;
                    }
                    Object value = jsonObject.get(key);
                    Class<?> clazz = f.getType();
                    if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                        f.setInt(item, toInt(value));
                    } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                        f.setLong(item, toLong(value));
                    } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                        f.setDouble(item, toDouble(value));
                    } else if (clazz.equals(DataTable.class)) {
                        f.set(item, parse.toDataTable(value));
                    } else if (clazz.equals(List.class) || fType.equals("list")) {
                        f.set(item, JsonConverter.convertJsonToList(value.toString(), Generic.getGenericInstance(f).getClass()));
                    } else if (clazz.equals(vList.class) || fType.equals("vlist")) {
                        vList l = new vList();
                        l.list = JsonConverter.convertJsonToList(value.toString(), Generic.getGenericInstance(f).getClass());
                        f.set(item, l);
                    } else if (clazz.equals(DateTime.class) || fType.equals("datetime")) {
                        f.set(item, parse.toDateTime(value));
                    } else if (clazz.equals(Boolean.class) || fType.equals("boolean"))
                        f.setBoolean(item, parse.toBoolean(value));
                    else if (clazz.equals(String.class) || fType.equals("string")) {
                        if (value.equals("null")) value = null;
                        f.set(item, String.valueOf(value));
                    }
                }
            }

        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.jsonObjectCaster", ex.getMessage());
        }
        return item;
    }

    private List<T> jsonArrayCaster(JSONArray jsonArray) {

        List<T> list = getTargetObject() ==null ? new ArrayList() : (List<T>)getTargetObject();
        for (int ds = 0; ds < jsonArray.length(); ds++) {
            try {
                list.add(jsonObjectCaster(jsonArray.getJSONObject(ds)));
            } catch (Exception ex) {
                AppSettings.Print("e", "JsonConverter.jsonArrayCaster", ex.getMessage());
            }
        }
        return list;
    }

    //region static

    public static <E> E convertJsonToModel(String jsonData, Class eClass) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(String jsonData, String key, Class eClass) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
            jsonConverter.setKey(key);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(String jsonData, String key, int index, Class eClass) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
            jsonConverter.setKey(key);
            jsonConverter.setIndex(index);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(String jsonData, int index, Class eClass) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
            jsonConverter.setIndex(index);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }


    public static <E> List<E> convertJsonToList(String jsonData, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, String key, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        jsonConverter.setKey(key);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, String key, int index, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        jsonConverter.setKey(key);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, int index, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }


    public static <E> E convertJsonToModel(Object object, String jsonData ) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>();
            jsonConverter.setJsonData(jsonData);
            jsonConverter.setTargetObject(object);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(Object object, String jsonData, String key ) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>();
            jsonConverter.setJsonData(jsonData);
            jsonConverter.setTargetObject(object);
            jsonConverter.setKey(key);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(Object object, String jsonData, String key, int index ) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>();
            jsonConverter.setJsonData(jsonData);
            jsonConverter.setTargetObject(object);
            jsonConverter.setKey(key);
            jsonConverter.setIndex(index);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(Object object, String jsonData, int index ) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>();
            jsonConverter.setJsonData(jsonData);
            jsonConverter.setTargetObject(object);
            jsonConverter.setIndex(index);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }


    public static <E> List<E> convertJsonToList(Object object, String jsonData, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, String key, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setKey(key);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, String key, int index, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setKey(key);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, int index, Class eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }


    public static String convertToJson(Object item)
    {
        Class cls = item.getClass();
        String name = cls.getSimpleName().toLowerCase();
        String type = cls.getName().toLowerCase();

        JSONArray jsonArray = new JSONArray();
        boolean isArray = (item instanceof List || item instanceof vList);
        if (isArray)
        {
            ArrayList list = (item instanceof vList ? (ArrayList)((vList)item).list : (ArrayList) item);

            if (list.size()>0)
            {
                Object listFirst = list.get(0);

                //region toJsonOfArray
                if (       cls.equals(Integer.class)
                        || cls.equals(int.class)
                        || cls.equals(Long.class)
                        || cls.equals(long.class)
                        || cls.equals(Double.class)
                        || cls.equals(double.class)
                        || cls.equals(DateTime.class)
                        || cls.equals(Boolean.class)
                        || cls.equals(String.class)
                )
                {
                    return convertToJsonOfArray(list);
                }
                //endregion
                else
                    for (Object i : list)
                    {

                        try {
                            String json = convertToJson(i);
                            if (json.substring(0, 1).equals("["))
                                jsonArray.put(new JSONArray(json));
                            else
                                jsonArray.put(new JSONObject(json));

                        } catch (Exception e) {
                        }
                    }
            }


        } else {
            JSONObject object = new JSONObject();

            try
            {

                {
                    for (Field f : CustomUtil.getFields(item)) {
                        f.setAccessible(true);
                        if (!f.getName().equals("$change") && !f.getName().equals("serialVersionUID"))
                            try {

                                String fName = f.getName();
                                if(f.isAnnotationPresent(Json.class))
                                {
                                    Json json = f.getAnnotation(Json.class);

                                    if (json.ignoreToJson())
                                        continue;

                                    String jName = f.getAnnotation(Json.class).name();
                                    if (jName.length()>0)
                                        fName = jName;

                                }

                                Object pObject = f.get(item);
                                Class objectClass = pObject.getClass();
                                if (objectClass.equals(Integer.class) || objectClass.equals(int.class))
                                    object.put(fName, toInt(pObject));
                                else if (objectClass.equals(Long.class) || objectClass.equals(long.class))
                                    object.put(fName, toLong(pObject));
                                else if (objectClass.equals(Double.class) || objectClass.equals(double.class))
                                    object.put(fName, toDouble(pObject));
                                else if (objectClass.equals(DataTable.class))
                                    object.put(fName, ((DataTable) pObject).getJsonData());
                                else if (objectClass.equals(DateTime.class))
                                    object.put(fName, ((DateTime) pObject).toString());
                                else if (objectClass.equals(Boolean.class))
                                    object.put(fName, (pObject));
                                else if (objectClass.equals(String.class))
                                    object.put(fName, pObject);
                                else if (pObject instanceof List)
                                    object.put(fName, new JSONArray(convertToJson(pObject)));
                                else {

                                    if (pObject instanceof ASerializable)
                                        object.put(fName, ((ASerializable) pObject).SerializeJsonObject());
                                    else if (pObject instanceof ISerializable)
                                        object.put(fName, ((ISerializable) pObject).SerializeJsonObject());
                                    else if (pObject.toString().split("\\.").length > 2 && pObject.toString().indexOf("@") != -1)
                                        object.put(fName, new JSONObject(JsonConverter.convertToJson(pObject)));
                                    else
                                        object.put(fName, pObject);
                                }


                            } catch (Exception ex) {
                            }
                        f.setAccessible(false);
                    }
                    jsonArray.put(object);
                }
            }
            catch (Exception ex)
            {

            }

        }


        try {
            return isArray ? jsonArray.toString() : jsonArray.length() > 0 ? jsonArray.getJSONObject(0).toString() : "{}";
        } catch (JSONException e) {
            return "{}";
        }
    }
    public static String convertToJsonOfArray(List item)
    {
        String data="";
        boolean virgul = false;
        for (Object o:item)
        {
            Class cls = o.getClass();

            if (virgul)
                data+=",";
            virgul=true;

            if (       cls.equals(Integer.class)
                    || cls.equals(int.class)
                    || cls.equals(Long.class)
                    || cls.equals(long.class)
                    || cls.equals(Double.class)
                    || cls.equals(double.class)
                    || cls.equals(Boolean.class)

            )
                data+=parse.Join("{0}",o);
            else if (cls.equals(String.class) || cls.equals(DateTime.class))
                data+=parse.Join("\"{0}\"",o);


        }
        return parse.Join("[{0}]",data);
    }
    //endregion

}
