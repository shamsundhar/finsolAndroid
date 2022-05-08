package com.finsol.tech.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finsol.tech.R;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class Utilities {
    public static boolean check12DigitLogic(String barcode) {
        boolean flag = false;
        try {
            int totalValue = 0;
            int[] values = {5, 1, 7, 5, 1, 7, 5, 1, 7, 5, 1};
            int lastCounter = 0;
            for (int i = 0; i < 11; i++) {
                char valueAtIndex = barcode.charAt(i);
                int intValueAtIndex = Integer.parseInt(valueAtIndex + "");
                int multiplyValue = values[i] * intValueAtIndex;
                totalValue = multiplyValue + totalValue;
                lastCounter = i;
            }
            lastCounter++;

            char lastDig = barcode.charAt(lastCounter);
            int lastDigInt = Integer.parseInt(lastDig + "");

            int remainder = totalValue % 11;
            if (remainder == 10)
                remainder = 0;
            if (remainder == lastDigInt) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
            return flag;
        }
        return flag;
    }


    public static boolean check15DigitLogic(String barcode) {
        if(barcode.length() == 15)
            return true;
        else
            return false;
    }


    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
    public static boolean isNetworkAvailable(Activity activity) {
        Boolean value = false;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        value = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        return value;
    }
//    public static void displayErrorMessage(Context context, Throwable e){
//        if (e instanceof HttpException) {
//            ResponseBody body = ((HttpException) e).response().errorBody();
//            Gson gson = new Gson();
//            TypeAdapter<GenericResponseModel> adapter = gson.getAdapter
//                    (GenericResponseModel
//                            .class);
//            try {
//                GenericResponseModel errorParser =
//                        adapter.fromJson(body.string());
//
//                Utilities.showDialogWithOneButton(context, errorParser.getMessage() + "", null);
//
//            } catch (IOException ee) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static void showDialogWithOneButton(Context context, String message, DialogInterface.OnDismissListener dismissListener){
       final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_generic);
        dialog.setCanceledOnTouchOutside(true);
        ((TextView)dialog.findViewById(R.id.message)).setText(message);
        dialog.show();
        dialog.findViewById(R.id.oneButtonLayout).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.twoButtonLayout).setVisibility(View.GONE);
        dialog.setOnDismissListener(dismissListener);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+M=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static void showDialogWithTwoButton(Context context, String message, View.OnClickListener positiveListener, View.OnClickListener negativeListener, DialogInterface.OnDismissListener dismissListener){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_generic);
        dialog.setCanceledOnTouchOutside(false);
        ((TextView) dialog.findViewById(R.id.message)).setText(message);

        dialog.findViewById(R.id.oneButtonLayout).setVisibility(View.GONE);
        dialog.findViewById(R.id.twoButtonLayout).setVisibility(View.VISIBLE);
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                positiveListener.onClick(null);

            }
        });
        dialog.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                negativeListener.onClick(null);
            }
        });
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
    }
    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
    public static int dpToPx(float dp, Context context) {
        return dpToPx(dp, context.getResources());
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static void hideSoftKeyboard(Context context, EditText input) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
    public static String calculatePercentage(){

        return "";
    }
}
