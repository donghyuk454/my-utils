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
            obj = className.getConstructor().newInstance(); // 빈 객체 생성
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        // < key, value > 연산
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            String setterName = new StringBuilder()
                    .append("set")
                    .append(key.toString().substring(0,1).toUpperCase())
                    .append(key.toString().substring(1))
                    .toString();
            Field field = className.getDeclaredField(key.toString()); // key 와 같은 field 찾기
            Class<?> fieldType = field.getType();
            try {
                Method setter = obj.getClass().getMethod(setterName, fieldType);
                if (setter.getParameterCount() != 1) {
                    throw new Exception("not invalid setter");
                }
                System.out.println("setter = " + setter);
                System.out.println("field = " + field);
                String fieldTypeName = fieldType.getTypeName();
                System.out.println("type = " + fieldTypeName);
                System.out.println(Integer.class.getTypeName());

                invokeSetter(obj, setter, fieldType, value);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private static <T> void invokeSetter(T obj, Method setter, Class<?> fieldType, Object value) throws InvocationTargetException, IllegalAccessException {
        String fieldTypeName = fieldType.getTypeName();
        
        if (fieldTypeName.equals(Integer.class.getTypeName())) {
            setter.invoke(obj, Integer.parseInt((String) value));
        } else if (fieldTypeName.equals(Long.class.getTypeName())) {
            setter.invoke(obj, Long.parseLong((String) value));
        } else if (fieldTypeName.equals(Float.class.getTypeName())) {
            setter.invoke(obj, Float.parseFloat((String) value));
        } else if (fieldTypeName.equals(Double.class.getTypeName())) {
            setter.invoke(obj, Double.parseDouble((String) value));
        } else {
            setter.invoke(obj, fieldType.cast(value));
        }
    }
}
