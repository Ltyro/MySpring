package lnstark.entity;

import lnstark.annotations.RequestMapping;
import lnstark.annotations.RequestParam;
import lnstark.exception.ContextException;
import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class RequestHandler {

    private Method method;

    private RequestMapping requestMapping;

    public RequestHandler() {
    }

    public RequestHandler(Method method, RequestMapping requestMapping) {
        this.method = method;
        this.requestMapping = requestMapping;
    }

    public String handle(HttpServletRequest request) {
        String result = null;
        Context ctx = ContextAware.getContext();

        try {
            // 从容其中获取controller
            List beans = ctx.getBeanByType(method.getDeclaringClass());
            if (beans.size() == 0)
                throw new ContextException("target bean not found");
            Object controller = beans.get(0);

            // 获取并设置参数
            Object[] paramValues = extractRequestParams(request, method);

            // 执行controller方法
            Object invokeResult = method.invoke(controller, paramValues);

            result = method.getReturnType() == Void.class || invokeResult == null ?
                    null : invokeResult.toString();

        } catch (InvocationTargetException | IllegalAccessException | ContextException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取并设置参数（String和基本类型）的实现
     *
     * @param request
     * @param method
     * @return
     */
    private Object[] extractRequestParams(HttpServletRequest request, Method method) {
        Parameter[] params = method.getParameters();
        Object[] paramValues = new Object[params.length];
        Map<String, String[]> paramMap = request.getParameterMap();
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            if (p.getType() == HttpServletRequest.class) {
                paramValues[i] = request;
                continue;
            }
//            // 这里编译的时候添加“-parameters”参数才能保留原参数名
//            String pName = p.getName();
            // 从注解中获取参数名
            RequestParam rpa = p.getAnnotation(RequestParam.class);
            String[] paramValue = paramMap.get(rpa.value());
            paramValues[i] = convertParam(paramValue, p.getType());
        }
        return paramValues;
    }

    /**
     * 参数转化，将传入的字符串转成方法的参数
     *
     * @param paramValue 传入的字符串
     * @param paramType  参数类型
     * @return
     */
    private Object convertParam(String[] paramValue, Class<?> paramType) {

        if (paramValue == null || paramValue.length == 0)
            return null;

        String strParamValue = paramValue[0];

        if (paramType == String.class)
            return strParamValue;

        if (paramType == Character.class || paramType == char.class)
            return strParamValue.length() == 1 ? strParamValue.charAt(0) : null;

        if (paramType == Integer.class || paramType == int.class)
            return Integer.valueOf(strParamValue);

        if (paramType == Float.class || paramType == float.class)
            return Float.valueOf(strParamValue);

        if (paramType == Double.class || paramType == double.class)
            return Double.valueOf(strParamValue);

        if (paramType == Byte.class || paramType == byte.class)
            return Byte.valueOf(strParamValue);

        if (paramType == Short.class || paramType == short.class)
            return Short.valueOf(strParamValue);

        if (paramType == Boolean.class || paramType == boolean.class)
            return Boolean.valueOf(strParamValue);

        if (paramType == Long.class || paramType == long.class)
            return Long.valueOf(strParamValue);

        return null;// 其他类型暂时不处理
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
