package momorexmo.module.Utils.CustomBinding;

import android.view.View;

public interface OnGridItemClick<T>
{
    void onClick(int positon, T item, View view);
}
