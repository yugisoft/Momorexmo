package momorexmo.module.Interfaces;

import momorexmo.module.Utils.JsonConverter;

public abstract class ASerializable implements ISerializable
{
    public static <T> T getInstance(String json) { return getInstance(json,""); }
    public static <T> T getInstance(String json,String key) { return getInstance(json,key,0); }
    public static <T> T getInstance(String json,String key,int index) {
        String ClassName =  Thread.currentThread().getStackTrace()[1].getClassName();
        T object = JsonConverter.convertJsonToModel(json,key,index,Thread.currentThread().getStackTrace()[1].getClass());
        return object;
    }
}
