package socket;

/**
 * Created by jun on 16-12-11.
 */


import java.io.*;
import java.net.Socket;


public class FileTransport {
    Socket s;
    public FileTransport(Socket s){
        this.s=s;
    }
    public void run(){
        try {
            System.out.println("Thread running>>>");
            DataInputStream dis = new DataInputStream(s.getInputStream());
            long length=dis.readLong();
            String name=dis.readUTF();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(name)));
            int count=-1,sum=0;
            byte[] buffer=new byte[1024*1024];
            while((count=dis.read(buffer))!=-1){
                dos.write(buffer,0,count);
                sum+=count;
                System.out.println("已结收" + sum + "比特");
                if(sum==length)
                    break;
            }
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}