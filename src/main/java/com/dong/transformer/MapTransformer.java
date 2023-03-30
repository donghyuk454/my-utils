package main.java.com.dong.transformer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MapTransformer implements Transformer {

    public <T> T transformTo (Map<String, String> map, Class<T> className) throws Exception {
        if (map == null)
            return null;
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        T obj = null;
        try {
            obj = className.getConstructor().newInstance(); // 빈 객체 생성
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        if (obj == null)
            return null;

        // < key, value > 연산
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            String setterName = "set" +
                    key.substring(0, 1).toUpperCase() +
                    key.substring(1);
            Field field = className.getDeclaredField(key); // key 와 같은 field 찾기
            Class<?> fieldType = field.getType();
            try {
                Method setter = obj.getClass().getMethod(setterName, fieldType);
                if (setter.getParameterCount() != 1) {
                    throw new Exception("not invalid setter");
                }
                invokeSetter(obj, setter, fieldType, value);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private static <T> void invokeSetter(T obj, Method setter, Class<?> fieldType, Object value) throws InvocationTargetException, IllegalAccessException {
        String fieldTypeName = fieldType.getTypeName();

        if (fieldTypeName.equals(Integer.class.getTypeName()) || fieldTypeName.equals("int")) {
            setter.invoke(obj, Integer.parseInt(value));
        } else if (fieldTypeName.equals(Long.class.getTypeName()) || fieldTypeName.equals("long")) {
            setter.invoke(obj, Long.parseLong(value));
        } else if (fieldTypeName.equals(Float.class.getTypeName()) || fieldTypeName.equals("float")) {
            setter.invoke(obj, Float.parseFloat(value));
        } else if (fieldTypeName.equals(Double.class.getTypeName()) || fieldTypeName.equals("double")) {
            setter.invoke(obj, Double.parseDouble(value));
        } else if (fieldTypeName.equals(Short.class.getTypeName()) || fieldTypeName.equals("short")) {
            setter.invoke(obj, Short.parseShort(value));
        } else if (fieldTypeName.equals(Boolean.class.getTypeName()) || fieldTypeName.equals("boolean")) {
            setter.invoke(obj, Boolean.getBoolean(value));
        } else {
            setter.invoke(obj, fieldType.cast(value));
        }
    }
}
