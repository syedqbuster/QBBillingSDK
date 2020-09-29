package co.queuebuster.qbbillingutils;

import android.os.Parcel;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Check {

    public static final int DOUBLE_DECIMAL_PLACES = 4;

    public static int parseInt(String value) {
        int reply;
        try {
            reply = Integer.parseInt(value);
        } catch (Exception e) {
            reply = -1;
        }

        return reply;
    }

    public static int parseIntWithzeroReturn(String value) {
        int reply;
        try {
            reply = Integer.parseInt(value);
        } catch (Exception e) {
            reply = 0;
        }

        return reply;
    }

    public static boolean parseBoolean(String value) {
        boolean reply = false;
        try {
            reply = (Integer.parseInt(value) != 0);
        } catch (Exception e) {
            reply = false;
        }

        return reply;
    }

    public static boolean parseBoolean(int value) {
        boolean reply = false;

        try {
            reply = (value == 1);
        } catch (Exception e) {
            // do nothing
        }

        return reply;
    }

    public static long parseLong(String value) {
        long reply = 0;
        try {
            reply = Long.parseLong(value);
        } catch (Exception e) {
            // do nothing
        }

        return reply;
    }

    public static double parseDouble(String value) {
        if (value == null || value.equalsIgnoreCase("null"))
            return 0.0;

        double reply = 0.00;

        try {
            reply = Double.parseDouble(value);
        } catch (Exception e) {
            reply = 0.00;
        } finally {
            reply = roundDouble(reply, DOUBLE_DECIMAL_PLACES);
        }

        return reply;
    }

    /**
     * Compare if two doubles are equal
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean isDoubleEqual(double x, double y) {
        int result = Double.compare(x, y);

        return (result == 0) ? true : false;
    }


    /**
     * Rounding of double value to certain decimal places.
     *
     * @param value
     * @param places
     * @return
     */

    public static double roundDouble(Double value, int places) {
        // Serialization concept
        try {
            if (value == null)
                return 0.0;

            if (places <= 0)
                places = DOUBLE_DECIMAL_PLACES;

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (NumberFormatException e) {

        }
        return 0.0;
    }

    public static String[] stringSplitter(String value) {
        String[] array;

        try {
            array = value.split(",");
        } catch (Exception e) {
            array = new String[]{""};
        }

        return array;
    }

    public static int[] stringIntSplitter(String value) {

        int[] result = new int[]{};

        if (value == null || value.isEmpty())
            return result;

        try {
            String[] array = value.split(",");

            result = new int[array.length];

            int i = 0;
            for (i = 0; i < result.length; i++) {

                result[i] = Integer.parseInt(array[i]);
            }
        } catch (Exception e) {

            result = new int[]{};
        }

        return result;
    }

    public static Integer[] stringIntegerSplitter(String value) {

        Integer[] result = new Integer[]{};

        if (value == null || value.isEmpty())
            return result;

        try {
            String[] array = value.split(",");

            result = new Integer[array.length];

            int i = 0;
            for (i = 0; i < result.length; i++) {

                result[i] = Integer.parseInt(array[i]);
            }
        } catch (Exception e) {

            result = new Integer[]{};
        }

        return result;
    }

    public static boolean stringToBoolean(String value) {
        boolean reply = false;

        try {
            reply = (Integer.parseInt(value) != 0);
        } catch (Exception e) {
            reply = false;
        }

        return reply;
    }

    public static boolean intToBoolean(Parcel value) {
        boolean reply = false;

        try {
            reply = (value.readInt() != 0);
        } catch (Exception e) {
            reply = false;
        }

        return reply;
    }

    public static boolean intToBoolean(int value) {
        boolean reply = false;

        try {
            reply = (value == 1);
        } catch (Exception e) {
            reply = false;
        }

        return reply;
    }

    public static String checkNull(String value) {
        String response = "";
        try {
            if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("null"))
                response = value;
        } catch (Exception e) {
            response = "";
        }

        return response;
    }

    public static String IntArrayToString(int[] intArray) {
        String response = "";

        int i = 0;
        for (i = 0; i < intArray.length; i++) {
            response = response + intArray[i] + ",";
        }

        return removeEndInterval(response);
    }

    public static String StringArrayToString(String[] stringArray) {
        String response = "";

        int i = 0;
        for (i = 0; i < stringArray.length; i++) {
            response = response + stringArray[i] + ",";
        }

        return removeEndInterval(response);
    }

    public static String removeEndInterval(String input) {
        return input.endsWith(",") ? input.replaceAll(",$", "") : input;
    }

    /**
     * Checks if String is null or empty.
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        try {

            if (string == null || string.length() == 0 || string.isEmpty() || string.equalsIgnoreCase("null")) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Checks if Integer is null or empty
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Integer value) {
        try {

            if (value == null || value == -1 || value == 0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Checks if Integer is null or empty
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Double value) {

        try {

            if (value == null || value == -1 || value == 0.0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Returns byte size of the file
     *
     * @param file
     * @return
     */
    public static int getFileSize(File file) {
        int size = 0; // size should be in bytes

        try {
            size = Integer.parseInt(String.valueOf(file.length()));
        } catch (Exception e) {
            // do nothing
        }
        return size;
    }

//
//    public static String getCurrencyFormate(double value, String countryCode) {
//
//        Locale locale = new Locale("en", countryCode);
//
//        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
//        String pattern = ((DecimalFormat) nf).toPattern();
//        String newPattern = pattern.replace("\u00A4", "").trim();
//        NumberFormat newFormat = new DecimalFormat(newPattern);
//
//        return QBApp.getInstance().getCurrencySymbol() + newFormat.format(value);
//    }
//
//    public static String getCurrencyFormatWithoutSymbol(double value, String countryCode) {
//
//        Locale locale = new Locale("en", countryCode);
//
//        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
//
//        return QBApp.getInstance().getCurrencySymbol() + nf.format(value);
//    }

    public static Boolean isEqual(String strOne, String strTwo){

        return strOne.trim().equalsIgnoreCase(strTwo.trim());
    }

}


