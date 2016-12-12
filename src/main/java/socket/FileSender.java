package socket;

/**
 * Created by jun on 16-12-11.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Socket;

public class FileSender {
    private ServerSocket ss=null;
    public FileSender(){

    }
    public void sendFile(String filePath, int port){
        DataOutputStream dos=null;
        DataInputStream dis=null;

        Socket socket=null;
        try {
            File file=new File(filePath);
            ss=new ServerSocket(port);
            socket=ss.accept();
            dos=new DataOutputStream(socket.getOutputStream());
            dis=new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));

            int buffferSize=1024;
            byte[]bufArray=new byte[buffferSize];
            dos.writeUTF(file.getName());
            dos.flush();
            dos.writeLong((long) file.length());
            dos.flush();
            while (true) {
                int read = 0;
                if (dis!= null) {
                    read = dis.read(bufArray);
                }

                if (read == -1) {
                    break;
                }
                dos.write(bufArray, 0, read);
            }
            dos.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭所有连接
            try {
                if (dos != null)
                    dos.close();
            } catch (IOException e) {
            }
            try {
                if (dis != null)
                    dis.close();
            } catch (IOException e) {
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
            }
            try {
                if (ss != null)
                    ss.close();
            } catch (IOException e) {
            }
        }

    }
    public static void main(String []args){
        new FileSender().sendFile("E:/VITA文档记录.doc", 8821);
    }
}


