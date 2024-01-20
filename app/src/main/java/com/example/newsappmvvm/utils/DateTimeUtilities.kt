package com.example.newsappmvvm.utils

import androidx.annotation.Nullable
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




class DateTimeUtilities {
    companion object {
        /*
        G   Era designator  Text    AD
        y   Year    Year    1996; 96
        Y   Week year   Year    2009; 09
        M   Month in year   Month   July; Jul; 07
        w   Week in year    Number  27
        W   Week in month   Number  2
        D   Day in year Number  189
        d   Day in month    Number  10
        F   Day of week in month    Number  2
        E   Day name in week    Text    Tuesday; Tue
        u   Day number of week (1 = Monday, ..., 7 = Sunday)    Number  1
        a   Am/pm marker    Text    PM
        H   Hour in day (0-23)  Number  0
        k   Hour in day (1-24)  Number  24
        K   Hour in am/pm (0-11)    Number  0
        h   Hour in am/pm (1-12)    Number  12
        m   Minute in hour  Number  30
        s   Second in minute    Number  55
        S   Millisecond Number  978
        z   Time zone   General time zone   Pacific Standard Time; PST; GMT-08:00
        Z   Time zone   RFC 822 time zone   -0800
        X   Time zone   ISO 8601 time zone  -08; -0800; -08:00


        "yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy"  Wed, Jul 4, '01
        "h:mm a"    12:08 PM
        "hh 'o''clock' a, zzzz" 12 o'clock PM, Pacific Daylight Time
        "K:mm a, z" 0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z"    Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ" 010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"   2001-07-04T12:08:56.235-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"   2001-07-04T12:08:56.235-07:00
        "YYYY-'W'ww-u"  2001-W27-3

        Ref: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
    */

        /*
        G   Era designator  Text    AD
        y   Year    Year    1996; 96
        Y   Week year   Year    2009; 09
        M   Month in year   Month   July; Jul; 07
        w   Week in year    Number  27
        W   Week in month   Number  2
        D   Day in year Number  189
        d   Day in month    Number  10
        F   Day of week in month    Number  2
        E   Day name in week    Text    Tuesday; Tue
        u   Day number of week (1 = Monday, ..., 7 = Sunday)    Number  1
        a   Am/pm marker    Text    PM
        H   Hour in day (0-23)  Number  0
        k   Hour in day (1-24)  Number  24
        K   Hour in am/pm (0-11)    Number  0
        h   Hour in am/pm (1-12)    Number  12
        m   Minute in hour  Number  30
        s   Second in minute    Number  55
        S   Millisecond Number  978
        z   Time zone   General time zone   Pacific Standard Time; PST; GMT-08:00
        Z   Time zone   RFC 822 time zone   -0800
        X   Time zone   ISO 8601 time zone  -08; -0800; -08:00


        "yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy"  Wed, Jul 4, '01
        "h:mm a"    12:08 PM
        "hh 'o''clock' a, zzzz" 12 o'clock PM, Pacific Daylight Time
        "K:mm a, z" 0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z"    Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ" 010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"   2001-07-04T12:08:56.235-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"   2001-07-04T12:08:56.235-07:00
        "YYYY-'W'ww-u"  2001-W27-3

        Ref: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
    */
        private val format24Hour: SimpleDateFormat = SimpleDateFormat("HH:mm")
        private val format12Hour: SimpleDateFormat = SimpleDateFormat("hh:mm aa")
        private val formatDate: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        fun format(format: String?, date: Date?): String? {
//        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
            val df: DateFormat = SimpleDateFormat(format)
            return df.format(date)
        }

        fun format(format: String?, date: Date?, locale: Locale?): String? {
            return SimpleDateFormat(format, locale).format(date)
        }

        @Nullable
        fun parse(dateStr: String?, format: String?): Date? {
            return try {
                SimpleDateFormat(format, Locale.ENGLISH).parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }

        fun get12HourFormat(time: String?): String? {
            var time = time
            try {
                val date: Date = format24Hour.parse(time)
                time = format12Hour.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return time
        }

        fun changeFormat(date: String?, format: String?, finalFormat: String?): String? {
            var dateFormat = SimpleDateFormat(format)
            var dateString = ""
            try {
                val date1: Date = dateFormat.parse(date)
                dateFormat = SimpleDateFormat(finalFormat)
                dateString = dateFormat.format(date1)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateString
        }
    }
}