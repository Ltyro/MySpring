package lnstark.utils;

public class StringUtil {
    /**
     * 纯数字
     */
    public static boolean isNumberOnly(String str) {
        return str != null && str.matches("^\\d+$");
    }

    public static boolean isEmpty(Object o) {
        if(o == null)
            return true;
        if(o instanceof String)
            return o.toString().isEmpty();
        return false;
    }
}
