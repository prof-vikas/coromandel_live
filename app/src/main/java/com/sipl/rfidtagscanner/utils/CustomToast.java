package com.sipl.rfidtagscanner.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.sipl.rfidtagscanner.R;

public class CustomToast {
    public void toastMessage(Context context, String message, int duration){
        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();

        view.getBackground().setColorFilter(ContextCompat.getColor(context,R.color.toast_bg), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(context,R.color.white));

        toast.show();
    }
}
