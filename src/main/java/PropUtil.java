import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件工具
 * Created by jun on 16-12-10.
 */

public class PropUtil {

    private static Properties prop;
    static{
        prop = new Properties();
        //InputStream inStream = PropUtil.class.getClassLoader().getResourceAsStream("d:/files/config.properties");
        try {
            InputStream inStream = new FileInputStream(new File("/home/jun/sbin/workspace/distributedFileSystem/src/main/java/config.properties"));
            prop.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProp(){
        return prop;
    }


    public static void main(String[] args) {
        String xmlPath = (String) PropUtil.getProp().get("filePath");
        System.out.println(xmlPath);
    }
}

