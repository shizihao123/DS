package socket;

/**
 * Created by jun on 16-12-11.
 */
import java.io.*;
import java.net.Socket;

/**
 * 客户端
 */
public class Client extends Socket{

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2013;

    private Socket client;
    private FileInputStream fis;
    private DataOutputStream dos;
    private ObjectOutputStream oos;

    public Client(){
        try {
            try {
                client = new Socket(SERVER_IP, SERVER_PORT);
                File file = new File("/home/jun/Desktop/data mining/Assignment6/sample_submission.csv");
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());

                //文件名和长度
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                System.out.println(file.length());
                dos.flush();

                //传输文件
                byte[] sendBytes = new byte[1024];
                int length = 0;
                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                    System.out.println(length);
                    dos.write(sendBytes, 0, length);
                }
                dos.flush();
                fis.close();
                File file1 = new File("/home/jun/sbin/workspace/distributedFileSystem/src/main/java/socket/FileIncepter.java");
                fis = new FileInputStream(file1);

                    //文件名和长度
                dos.writeUTF(file1.getName());
                System.out.println(file1.getName());
                dos.writeLong(file1.length());
                System.out.println(file1.length());

                //传输文件
                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                    System.out.println(length);
                    dos.write(sendBytes, 0, length);
                }
                dos.flush();
//                dos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(fis != null)
                    fis.close();
                if(dos != null)
                    dos.close();
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client();
    }
}