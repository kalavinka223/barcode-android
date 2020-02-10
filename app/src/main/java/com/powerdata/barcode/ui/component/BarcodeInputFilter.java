package com.powerdata.barcode.ui.component;

import android.text.InputFilter;
import android.text.Spanned;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (StringUtils.isEmpty(source) || source.length() < 16)
            return source;

        //钢卷号2位数字+字母+11位数字
        Pattern pattern = Pattern.compile("\\d{2}[A-Z]\\d{11}");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find())
            return matcher.group();
        else
            return "";
    }

}
