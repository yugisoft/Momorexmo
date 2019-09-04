package momorexmo.module;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import android.view.LayoutInflater;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.RecyclerView;


public abstract class AppRichDialog extends RecyclerView.ViewHolder {
    public Context context;
    public Dialog dialog;
    public  static Dialog mDialog;

    public AppRichDialog(int id) {
        this(AppRichActivity.getActivity(), id);
    }
    public AppRichDialog(Context context, int id) {
        super(LayoutInflater.from(context).inflate(id, null));
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(itemView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Initilialize();
    }
    public abstract void Initilialize();

    public void show()
    {
        mDialog = dialog;
        dialog.show();
    }
    public void dismiss()
    {
        dialog.dismiss();
        AppRichActivity.getActivityIgnoreException().hideKeyboard();
    }

    public static boolean isShowing()
    {
        return mDialog!=null && mDialog.isShowing();
    }

    public void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) AppRichActivity.getActivityIgnoreException().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(dialog.getWindow().peekDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
