package momorexmo.module.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import momorexmo.module.AppSettings;
import momorexmo.module.DataTable;
import momorexmo.module.DateTime;
import momorexmo.module.FiledAttributes.Json;
import momorexmo.module.Generic;
import momorexmo.module.vList;


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
                        f.setInt(item, parse.toInt(value));
                    } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                        f.setLong(item, parse.toLong(value));
                    } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                        f.setDouble(item, parse.toDouble(value));
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

    public static <E> E convertJsonToModel(String jsonData, Class<E> eClass) {
        E item = null;
        try {
            JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
            item = jsonConverter.convertToClass();
        } catch (Exception ex) {
            AppSettings.Print("e", "JsonConverter.convertJsonToModel", ex.getMessage());
        }
        return item;
    }

    public static <E> E convertJsonToModel(String jsonData, String key, Class<E> eClass) {
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

    public static <E> E convertJsonToModel(String jsonData, String key, int index, Class<E> eClass) {
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

    public static <E> E convertJsonToModel(String jsonData, int index, Class<E> eClass) {
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


    public static <E> List<E> convertJsonToList(String jsonData, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, String key, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        jsonConverter.setKey(key);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, String key, int index, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData, eClass);
        jsonConverter.setKey(key);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(String jsonData, int index, Class<E> eClass) {
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


    public static <E> List<E> convertJsonToList(Object object, String jsonData, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, String key, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setKey(key);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, String key, int index, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setKey(key);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }

    public static <E> List<E> convertJsonToList(Object object, String jsonData, int index, Class<E> eClass) {
        JsonConverter<E> jsonConverter = new JsonConverter<>(jsonData,eClass);
        jsonConverter.setTargetObject(object);
        jsonConverter.setIndex(index);
        return jsonConverter.convertToClassList();
    }

    //endregion

}
