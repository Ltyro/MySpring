package lnstark.entity;

import lnstark.annotations.RequestMapping;

import java.lang.reflect.Method;

public class MethodWrapper {

    private Method method;

    private RequestMapping requestMapping;

    public MethodWrapper() {
    }

    public MethodWrapper(Method method, RequestMapping requestMapping) {
        this.method = method;
        this.requestMapping = requestMapping;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestMapping getRequestMapping() {
        return requestMapping;
    }

    public void setRequestMapping(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }
}
