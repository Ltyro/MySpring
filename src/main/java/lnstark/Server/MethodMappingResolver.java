package lnstark.Server;

import lnstark.annotations.RequestMapping;
import lnstark.entity.RequestHandler;
import lnstark.exception.RequestMappingException;
import lnstark.utils.Constants;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodMappingResolver {

    private static MethodMappingResolver mmResolver;

    private Map<String, RequestHandler> servletPath2Method;

    private MethodMappingResolver() {
        servletPath2Method = new HashMap();
    }

    public static MethodMappingResolver getInstance() {
        if(mmResolver == null)
            mmResolver = new MethodMappingResolver();
        return mmResolver;
    }

    /**
     * controller解析
     * @param clz controller类
     * @param cPath controller path
     */
    public void resolveController(Class clz, String cPath) {
        cPath = handlePath(cPath);
        Method[] methods = clz.getDeclaredMethods();
        try {
            for (Method method : methods) {
                Annotation a = method.getAnnotation(RequestMapping.class);
                if (!(a instanceof RequestMapping))
                    continue;
                RequestMapping rma = (RequestMapping) a;
                String rmaValues[] = rma.value();// 方法注解上的路径
                RequestHandler mw = new RequestHandler(method, rma);

                Set<String> pathSet = new HashSet();
                for (String s : rmaValues)
                    pathSet.add(s);

                for (String p : pathSet) {
                    String path = Constants.PATH_SEPERATOR + cPath + Constants.PATH_SEPERATOR + handlePath(p);
                    if (servletPath2Method.containsKey(path))
                        throw new RequestMappingException("路径“" + path + "”重复!");
                    servletPath2Method.put(path, mw);
                }
            }
        } catch (RequestMappingException e) {
            e.printStackTrace();
        }
    }

    private boolean matchMethod(RequestMethod[] rms, String method) {
        for(RequestMethod rm : rms) {
            if(rm.name().equals(method))
                return true;
        }
        return false;
    }

    /**
     * 去头尾的斜杠
     * @return
     */
    private String handlePath(String p) {

        while(p.startsWith("\\") || p.startsWith("/"))
            p = p.substring(1, p.length());

        while(p.endsWith("\\") || p.endsWith("/"))
            p = p.substring(0, p.length() - 1);

        return p;

    }

    public RequestHandler getHandler(HttpServletRequest req) throws RequestMappingException {
        String servletPath = req.getServletPath();
        // url过滤
        if (!servletPath2Method.containsKey(servletPath)) {
            throw new RequestMappingException("404 找不到路径");
        }
        RequestHandler handler = servletPath2Method.get(servletPath);
        RequestMapping rma = handler.getRequestMapping();
        RequestMethod[] methods = rma.method();
        // 请求方式过滤
        if(!matchMethod(methods, req.getMethod())) {
            throw new RequestMappingException("请求方式“" + req.getMethod() + "”不被允许");
        }
        return handler;
    }

    public static void main(String[] args) {
//        MethodMappingResolver mmr = new MethodMappingResolver();
//        System.out.println(mmr.handlePath("\\sdasf/\\/"));
    }

}
