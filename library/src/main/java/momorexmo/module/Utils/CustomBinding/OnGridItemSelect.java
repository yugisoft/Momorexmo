package momorexmo.module.Utils.CustomBinding;

import android.view.View;

import momorexmo.module.Interfaces.OnResponse;


public interface OnGridItemSelect<T>
{
    void OnSelect(OnResponse<Boolean> isSelect, int positon, T item, View view);
}
