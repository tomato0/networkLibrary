package com.jash.networklibrary;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Locale;

public class Tools {
    public static<T> T getInstance(Class<T> type) {
        Object o = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new MyHandler());
//        Proxy.newProxyInstance()使用proxy直接生成一个动态代理对象
        return (T) o;
    }

    /**
     * 执行动态代理对象的所有方法时，都会被替换成执行如下的invoke方法
     */
    private static class MyHandler implements InvocationHandler {

        @Override
        //proxy动态代理对象，method正在执行的方法，args调用目标方法时传入的实参
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d("TGP", "invoke: 正在执行的方法" + method);
//            Log.d("TAG", "invoke: 代理对象" + proxy);
            Log.d("TAG", "invoke: 传入的参数");
            UrlString annotation = method.getAnnotation(UrlString.class);//通过method获得该class的注解
            Log.d("TAG", "invoke: annotation" + annotation);
            if (annotation != null) {
                String url = String.format(Locale.CHINA, annotation.value(), args);//将args的值传给annotation的value
                Class<?> returnType = method.getReturnType();//得到该方法返回值的类型
                if (returnType.equals(NetworkTask.class)) {
                    ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                    //getGenericReturnType()获得泛型类型
                    Type entryType = type.getActualTypeArguments()[0];
                    //返回泛型参数的类型
                    Log.d("TAG", "invoke: entryType" + entryType);
//                    String encode = URLEncoder.encode("热点", "UTF-8");
//                    Log.d("RR", "invoke: " + encode);
//                    String url1 = url + "&keyword=" + encode;
                    return new NetworkTask<>(url, (Class) entryType);
                }
            }
            return null;
        }
    }
}
