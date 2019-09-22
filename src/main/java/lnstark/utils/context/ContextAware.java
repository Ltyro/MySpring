package lnstark.utils.context;

/**
 * 获取上下文
 */
public class ContextAware {
    private static Context context = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextAware.context = context;
    }
}
