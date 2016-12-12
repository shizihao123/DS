/**
 * Created by jun on 16-12-10.
 */
public class TextUtil {
    /**
     * 文本工具
     * @author chen.lin
     *
     */

    public static boolean isEmpty(String text){
        if (text == null || "".equals(text.trim())) {
            return true;
        }
        return false;
    }

}
