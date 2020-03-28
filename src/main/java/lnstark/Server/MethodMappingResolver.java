package lnstark.Server;

import lnstark.annotations.RequestMapping;
import lnstark.entity.RequestHandler;
import lnstark.exception.RequestMappingException;
import lnstark.utils.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MethodMappingResolver {

    private static MethodMappingResolver mmResolver;

    private Map<String, RequestHandler> servletPath2Method;

    private MethodMappingResolver() {
        servletPath2Method = new HashMap();
    }

    public static MethodMappingResolver getInstance() {
        if (mmResolver == null)
            mmResolver = new MethodMappingResolver();
        return mmResolver;
    }

    /**
     * controller解析
     * resolve controller
     *
     * @param clz   controller类
     * @param cValue controller name
     */
    public void resolveController(Class clz) {
//        cPath = handlePath(cPath);
        Method[] methods = clz.getDeclaredMethods();
        Annotation anno = clz.getAnnotation(RequestMapping.class);
        List<String> controllerPaths = null;
        if(anno instanceof RequestMapping)
            controllerPaths = getRequestMappingPaths(anno);
        try {
            for (Method method : methods) {
                Annotation a = method.getAnnotation(RequestMapping.class);
                if (!(a instanceof RequestMapping))
                    continue;
                RequestMapping rma = (RequestMapping) a;
                List<String> rmaValues = getRequestMappingPaths(a);// 方法注解上的路径
                RequestHandler mw = new RequestHandler(method, rma);

                Set<String> pathSet = new HashSet();
                for (String s : rmaValues)
                    pathSet.add(s);
                if(!CollectionUtils.isEmpty(controllerPaths)) {
                    for (String cp : controllerPaths) {
                        addServletPath(pathSet, cp, mw);
                    }
                } else {
                    addServletPath(pathSet, "", mw);
                }
            }
        } catch (RequestMappingException e) {
            e.printStackTrace();
        }
    }

    private void addServletPath(Set<String> pathSet, String cp, RequestHandler mw) throws RequestMappingException {
        for (String p : pathSet) {
            String path = cp == null || cp.equals("") ? "" : Constants.PATH_SEPERATOR + cp;
            path += Constants.PATH_SEPERATOR + handlePath(p);
            if (servletPath2Method.containsKey(path))
                throw new RequestMappingException("path \"" + path + "\" repeated!");
            servletPath2Method.put(path, mw);
        }
    }

    private List<String> getRequestMappingPaths(Annotation anno) {

        List<String> l = new ArrayList<>();
        RequestMapping rma = (RequestMapping) anno;
        String rmav[] = rma.value();
        l.addAll(Arrays.asList(rmav));
        return l;
    }

    private boolean matchMethod(RequestMethod[] rms, String method) {
        for (RequestMethod rm : rms) {
            if (rm.name().equals(method))
                return true;
        }
        return false;
    }

    /**
     * 去头尾的斜杠
     * remove the '\' and '/' at the beginning and the end
     * @return
     */
    private String handlePath(String p) {

        while (p.startsWith("\\") || p.startsWith("/"))
            p = p.substring(1, p.length());

        while (p.endsWith("\\") || p.endsWith("/"))
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
        if (!matchMethod(methods, req.getMethod())) {
            throw new RequestMappingException("请求方式“" + req.getMethod() + "”不被允许");
        }
        return handler;
    }

    public static void main(String[] args) {
//        MethodMappingResolver mmr = new MethodMappingResolver();
//        System.out.println(mmr.handlePath("\\sdasf/\\/"));
    }

}
