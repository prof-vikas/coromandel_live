package com.sipl.rfidtagscanner.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class Concatenator {

    public void multiStringConcatenate(TextView textview, String one, String two) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1 = new SpannableString(one);
        str1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str1.length(), 0);
        builder.append(str1);
        SpannableString str2 = new SpannableString(two);
        str2.setSpan(new ForegroundColorSpan(Color.RED), 0, str2.length(), 0);
        builder.append(str2);
        textview.setText(builder, TextView.BufferType.SPANNABLE);
    }

}
