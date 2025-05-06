package com.example.fedatingapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Tiện ích định dạng thời gian hiển thị cho tin nhắn
 */
public class DateFormatter {

    /**
     * Định dạng thời gian hiển thị cho tin nhắn
     * @param date Thời gian gửi tin nhắn
     * @return Chuỗi hiển thị phù hợp với ngữ cảnh thời gian
     */
    public static String formatMessageTime(Date date) {
        if (date == null) {
            return "";
        }

        Date now = new Date();
        long diffInMillis = now.getTime() - date.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (diffInMinutes < 1) {
            return "Vừa xong";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + " phút trước";
        } else if (diffInHours < 24) {
            return timeFormat.format(date);
        } else if (diffInDays < 7) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
            return dayFormat.format(date);
        } else {
            return dateFormat.format(date);
        }
    }

    /**
     * Kiểm tra xem tin nhắn có cần hiển thị header ngày không
     * @param current Tin nhắn hiện tại
     * @param previous Tin nhắn trước đó
     * @return true nếu cần hiển thị header ngày
     */
    public static boolean shouldShowDateHeader(Date current, Date previous) {
        if (previous == null) {
            return true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return !dateFormat.format(current).equals(dateFormat.format(previous));
    }
}