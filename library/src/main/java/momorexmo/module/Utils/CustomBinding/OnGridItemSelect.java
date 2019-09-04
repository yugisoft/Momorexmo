package momorexmo.module.Utils.CustomBinding;

import android.view.View;

import library.yugisoft.module.INTERFACES;

public interface OnGridItemSelect<T>
{
    void OnSelect(INTERFACES.OnResponse<Boolean> isSelect, int positon, T item, View view);
}
