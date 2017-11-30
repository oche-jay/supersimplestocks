package model.util;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper Class from https://stackoverflow.com/questions/21626690/gson-optional-and-required-fields
 *
 * @param <T>
 */
public class AnnotatedDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        T pojo = new Gson().fromJson(je, type);

        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getAnnotation(Required.class) != null) {
                try {
                    f.setAccessible(true);
                    if (f.get(pojo) == null) {
                        throw new JsonParseException("required field missing from sent JSON message: " + f.getName());
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (f.getAnnotation(PositiveValue.class) != null) {
                try {
                    f.setAccessible(true);
                    Number val = (Number) f.get(pojo);
                    if (val.doubleValue() <= 0) {
                        throw new JsonParseException("Zero and Negative values not allowed in field: " + f.getName());
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }


            if (f.getAnnotation(NonNegative.class) != null) {
                try {
                    f.setAccessible(true);
                    Number val = (Number) f.get(pojo);
                    if (val.doubleValue() < 0) {
                        throw new JsonParseException("Negative values not allowed in field: " + f.getName());
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        }
        return pojo;

    }
}