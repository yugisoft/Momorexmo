package momorexmo.module.Utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.HashSet;

public class ViewHalper
{

    public static <T extends View> T findViewByClassReference(View rootView, Class<T> clazz) {
        if(clazz.isInstance(rootView)) {
            return clazz.cast(rootView);
        }
        if(rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for(int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                T match = findViewByClassReference(child, clazz);
                if(match != null) {
                    return match;
                }
            }
        }
        return null;
    }
    public static <T extends View> Collection<T> findViewsByClassReference(View rootView, Class<T> clazz, Collection<T> out) {
        if(out == null) {
            out = new HashSet<>();
        }
        if(clazz.isInstance(rootView)) {
            out.add(clazz.cast(rootView));
        }
        if(rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for(int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                findViewsByClassReference(child, clazz, out);
            }
        }
        return out;
    }
    public static <T extends View> Collection<T> findViewsByClassReference(View rootView, Class<T> clazz) {
        return findViewsByClassReference(rootView, clazz, null);
    }
}
