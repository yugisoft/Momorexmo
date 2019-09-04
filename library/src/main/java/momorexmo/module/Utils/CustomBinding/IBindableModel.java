package momorexmo.module.Utils.CustomBinding;


import momorexmo.module.Utils.CustomUtil;

public interface IBindableModel
{
    default Object getValue(String fieldName) { return CustomUtil.getValue(this,fieldName); }
}
