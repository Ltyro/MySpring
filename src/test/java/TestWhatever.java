import java.util.ArrayList;
import java.util.List;

public class TestWhatever {
    public static void main(String[] args) {
        String s = "jafjsaoycviasjfaisdycvjdsfycvdf";
        String [] result = split(s, "ycv");
        System.out.println(result);
    }

    static String[] split(String str, String sp) {
        List<String> result = new ArrayList();
        int strL = str.length(), spL = sp.length();
        if(strL <= spL)
            return new String[0];
        int lastI = 0;
        for(int i = 0; i <= strL - spL; i++) {
            if(str.charAt(i) == sp.charAt(0) && findStrAt(str, sp, i)) {
                result.add(str.substring(lastI, i));
                i += spL;
                lastI = i;
            }
        }
        result.add(str.substring(lastI, strL));
        return result.toArray(new String[result.size()]);
    }

    static boolean findStrAt(String str, String sp, int i) {
//        for(int j = i, spI = 1; spI < sp.length(); j++, spI++) {
//            if(str.charAt(j) != sp.charAt(spI))
//                return false;
//        }
        int spI = 0;
        while (spI < sp.length())
            if(str.charAt(i++) != sp.charAt(spI++))
                return false;
        return true;
    }
}
