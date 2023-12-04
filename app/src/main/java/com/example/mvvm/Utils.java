package com.example.mvvm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mvvm.databinding.CustomDialogCodeBinding;
import com.example.mvvm.databinding.CustomDialogErrorBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.TransactionMoney;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.instacart.library.truetime.TrueTime;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static int calculateDaysBetween(String startDateStr, String endDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long daysBetween = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return (int) daysBetween;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Trả về -1 nếu có lỗi xảy ra
        }
    }
    public static void setDarkMode () {
        if (DataLocalManager.getNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean checkForExistence(String s, List<String> list) {
        for (String phanTu : list) {
            if (phanTu.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap convertQRCode(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            Log.e("QR Code", "Error generating QR code: " + e.getMessage());
            return null;
        }
    }

    public interface BalanceListener {
        void onAmountCalculated(int amount);
    }
    public static void getBalanceFromDatabase(String Uid,BalanceListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int amount = 0;
                if (snapshot.exists()) {
                    // Tham chiếu "Transaction" tồn tại, tiếp tục với logic của bạn
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TransactionMoney transactionMoney = dataSnapshot.getValue(TransactionMoney.class);
                        if (transactionMoney.wallet.equals(Uid) && transactionMoney.status) {
                            if (transactionMoney.transaction_type.equals("deposit")) {
                                amount += transactionMoney.amount;
                            } else {
                                amount -= transactionMoney.amount;
                            }
                        }
                    }
                    // Gọi phương thức của interface để truyền kết quả về
                    listener.onAmountCalculated(amount);
                    // ...
                } else {
                    // Tham chiếu "Transaction" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    public static void showQRCode(Context context, String string){

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogCodeBinding binding = CustomDialogCodeBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        binding.imageview.setImageBitmap(Utils.convertQRCode(string));

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showError(Context context, String error){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogErrorBinding binding = CustomDialogErrorBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        binding.textview.setText(error);

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static long getRealtime() {
        final long[] currentTime = new long[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Khởi tạo TrueTime
                    TrueTime.build().initialize();

                    // Lấy thời gian hện tại
                    currentTime[0] = TrueTime.now().getTime();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return currentTime[0];
    }
    public static Boolean isNumber(String string){
        for (char c : string.toCharArray()){
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    public static String convertPriceToVND(int price){
        DecimalFormat formater = new DecimalFormat("#,### VNĐ");
        String output = formater.format(price);
        return output;
    }
    public static Integer convertVNDToPrice(String price){
        price = price.replace(" VNĐ", "");
        DecimalFormat formater = new DecimalFormat("#,###");
        int number = 0;
        try {
            number = formater.parse(price).intValue();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return number;
    }

    public static List<String> convertStringToList(String string){
        string = string.replace("[", "").replace("]", "").replace(" ","");
        List<String> list = new ArrayList<>(Arrays.asList(string.split(",")));
        return list;
    }

    public static Integer convertSeat(String seat) {
        String first = seat.substring(0,1);
        int last;
        if (seat.length()==2)
        {
            last = Integer.parseInt(seat.substring(seat.length() - 1));
        }else {
            last = Integer.parseInt(seat.substring(seat.length() - 2));
        }

        if (first.equals("B")) {
            return last + 12 -1;
        }
        if (first.equals("C")) {
            return last + 12 * 2 -1;
        }
        if (first.equals("D")) {
            return last + 12 * 3 -1;
        }
        if (first.equals("E")) {
            return last + 12 * 4 -1;
        }
        if (first.equals("F")) {
            return last + 12 * 5 -1;
        }
        if (first.equals("G")) {
            return last + 12 * 6 -1;
        }
        if (first.equals("H")) {
            return last + 12 * 6 +8 -1;
        }
        return last-1;
    }

}
