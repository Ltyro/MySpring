package lnstark.utils.context;

public class ContextAware {
    private static Context context = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextAware.context = context;
    }
}
