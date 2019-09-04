package momorexmo.module.Utils.CustomBinding;


import library.yugisoft.module.Utils.CustomUtil;

public interface IBindableModel
{
    default Object getValue(String fieldName) { return CustomUtil.getValue(this,fieldName); }
}
