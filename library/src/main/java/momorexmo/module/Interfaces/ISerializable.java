package momorexmo.module.Interfaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import momorexmo.module.DataTable;
import momorexmo.module.DateTime;
import momorexmo.module.Utils.JsonConverter;
import momorexmo.module.Utils.parse;
import momorexmo.module.vList;

public interface ISerializable
{
    default <T> T Deserialize(String json)
    {
        return  Deserialize(json,"");
    }
    default <T> T Deserialize(String json,String key){
        return  Deserialize(json,key,0);
    }
    default <T> T Deserialize(String json,String key,int index) {
        JsonConverter.convertJsonToModel(this,json,key,index);
        return (T)this;
    }
    default <T> T DeserializeNew(String json)
    {
        return DeserializeNew(json,"");
    }
    default <T> T DeserializeNew(String json,String key){
        return DeserializeNew(json,key,0);
    }
    default <T> T DeserializeNew(String json,String key,int index) {
        String Json =JsonConverter.convertToJson(JsonConverter.convertJsonToModel(json,key,index,this.getClass()));
        JsonConverter.convertJsonToModel(this,Json,"",0);
        return (T)this;
    }
    default String Serialize()
    {
        return  JsonConverter.convertToJson(this);
    }


    default JSONObject SerializeJsonObject(Object item) {
        Class cls = item.getClass();
        String name = cls.getSimpleName().toLowerCase();
        String type = cls.getName().toLowerCase();

        JSONArray jsonArray = new JSONArray();
        boolean isArray = (item instanceof List);
        if (isArray) {
            ArrayList list = (ArrayList) item;
            for (Object i : list) {
                try {

                    //if (json.substring(0, 1).equals("["))
                    //    jsonArray.put(new JSONArray(json));
                    //else
                    jsonArray.put(SerializeJsonObject(i));

                } catch (Exception e) {
                }
            }
        } else {
            JSONObject object = new JSONObject();

            for (Field f : vList.Merge(Arrays.asList(cls.getFields()), Arrays.asList(cls.getDeclaredFields()))) {
                f.setAccessible(true);
                if (!f.getName().equals("$change") && !f.getName().equals("serialVersionUID"))
                    try {
                        Object pObject = f.get(item);
                        Class objectClass = pObject.getClass();
                        if (objectClass.equals(Integer.class) || objectClass.equals(int.class))
                            object.put(f.getName(), parse.toInt(pObject));
                        else if (objectClass.equals(Long.class) || objectClass.equals(long.class))
                            object.put(f.getName(), parse.toLong(pObject));
                        else if (objectClass.equals(Double.class) || objectClass.equals(double.class))
                            object.put(f.getName(), parse.toDouble(pObject));
                        else if (objectClass.equals(DataTable.class))
                            object.put(f.getName(), ((DataTable) pObject).getJsonData());
                        else if (objectClass.equals(DateTime.class))
                            object.put(f.getName(), ((DateTime) pObject).toString());
                        else if (objectClass.equals(Boolean.class))
                            object.put(f.getName(), (pObject));
                        else if (objectClass.equals(String.class))
                            object.put(f.getName(), pObject);
                        else if (pObject instanceof List)
                            object.put(f.getName(), new JSONArray(SerializeJsonObject(pObject)));
                        else {

                            if (pObject instanceof ASerializable)
                                object.put(f.getName(), ((ASerializable) pObject).Serialize());
                            else if (pObject instanceof ISerializable)
                                object.put(f.getName(), ((ISerializable) pObject).Serialize());
                            else if (pObject.toString().split("\\.").length > 2 && pObject.toString().indexOf("@") != -1)
                                object.put(f.getName(), (SerializeJsonObject(pObject)));
                            else
                                object.put(f.getName(), pObject);
                        }


                    } catch (Exception ex) {
                    }
                f.setAccessible(false);
            }
            jsonArray.put(object);
        }


        try {
            return  jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    default JSONObject SerializeJsonObject() { return  SerializeJsonObject(this); }



}
