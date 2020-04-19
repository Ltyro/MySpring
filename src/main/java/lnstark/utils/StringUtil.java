package lnstark.utils;

public class StringUtil {
    /**
     * 纯数字
     */
    public static boolean isNumberOnly(String str) {
        return str != null && str.matches("^\\d+$");
    }
}
