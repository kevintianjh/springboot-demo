package kevintian.springbootdemo;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch(Exception e) {
            return "";
        }
    }
}
