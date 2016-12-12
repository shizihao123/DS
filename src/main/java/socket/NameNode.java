package socket;

/**
 * Created by jun on 16-12-11.
 */

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;



/**
 * Created by jun on 16-12-11.
 */

/**
 * 服务器
 */
public class NameNode extends ServerSocket {
    private static final int PORT = 2012;
    private ServerSocket server;
    private Socket client;
    private Hashtable<String, Integer> clientNums;

    public NameNode() throws Exception{
        clientNums = new Hashtable<String, Integer>();

        server = new ServerSocket(PORT);
        try {
            while (true) {
                client = server.accept();
                new CreateServerThread(client);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            server.close();
        }
    }

    class CreateServerThread extends Thread {
        private Socket client;
        private DataInputStream dis;
        private FileOutputStream fos;

        public CreateServerThread(Socket s) throws Exception {
            System.out.println("ThreadId: " + this.getId());
            client = s;
            start();
        }

        @Override
        public void run() {
            super.run();
            try {
                try {
                    dis = new DataInputStream(client.getInputStream());
                    //文件名和长度
                    String fileName = dis.readUTF();
                    long fileLength = dis.readLong();
                    fos = new FileOutputStream(new File("/home/jun/Desktop/" + fileName));

                    byte[] sendBytes = new byte[1024];
                    int transLen = 0;
                    System.out.println("ThreadId" + this.getId() + "----开始接收文件<" + fileName + ">,文件大小为<" + fileLength + ">----");
                    while (true) {
                        int read = 0;
                        read = dis.read(sendBytes);
                        if (read == -1)
                            break;
                        transLen += read;
                        System.out.println("ThreadId" + this.getId() + "接收文件进度" + 100 * transLen / fileLength + "%...");
                        fos.write(sendBytes, 0, read);
                        fos.flush();
                    }
                    System.out.println("ThreadId" + this.getId() + "----接收文件<" + fileName + ">成功-------");
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (dis != null)
                        dis.close();
                    if (fos != null)
                        fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new DataNode();
    }
}