import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录日志信息
 * Created by jun on 16-12-10.
 */
public class Logger {
    public static void log(String name){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            String path = "d:/upload_log/" + sdf.format(new Date()) ;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(path + "/log.txt"), true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(name + "\r\n");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
