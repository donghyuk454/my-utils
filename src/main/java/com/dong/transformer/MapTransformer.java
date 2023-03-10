package main.java.com.dong.transformer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapTransformer implements Transformer {

    public static <T> T transformTo (Map map, Class<T> className) throws Exception{
        if (map == null)
            return null;

        Set<Object> keySet = map.keySet();
        Iterator<Object> iterator = keySet.iterator();
        T obj = null;
        try {
            obj = className.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            key.toString().substring(0,1).toUpperCase();
            String setterName = new StringBuilder()
                    .append("set")
                    .append(key.toString().substring(0,1).toUpperCase())
                    .append(key.toString().substring(1))
                    .toString();
            Field field = className.getDeclaredField(key.toString());
            try {
                Method setter = obj.getClass().getMethod(setterName, field.getType());
                if (setter.getParameterCount() != 1) {
                    throw new Exception("not invalid setter");
                }
                System.out.println("setter = " + setter);
                setter.invoke(obj, value); // TODO: casting 이슈 해결 필요
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }
}
