package momorexmo.module.Utils.CustomBinding;

import android.view.View;

import java.util.List;

public interface OnRowDrawing<T>
{
    default void onDraw(int index, View view, T item) {}
    default void onDraw(int index, View view, T item, List<View> views) {}
}
