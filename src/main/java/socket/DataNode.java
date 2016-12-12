package socket;

/**
 * Created by jun on 16-12-11.
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器
 */
public class DataNode extends ServerSocket{
    private static final int PORT = 2013;
    private ServerSocket server;
    private Socket client;

    public DataNode() throws Exception{
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
//        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(name)));

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
                        //文件名和长度
//                    ObjectInputStream ois=new ObjectInputStream(client.getInputStream());
                    dis = new DataInputStream(client.getInputStream());
                    while (true) {
//                        ObjectInputStream ois=new ObjectInputStream(client.getInputStream());
//                        String type=(String)ois.readObject();
//                        ois.close();
//                        System.out.println(type);
//                        dis = new DataInputStream(client.getInputStream());

                        String fileName = dis.readUTF();
                        System.out.println(fileName.toString());
                        long fileLength = dis.readLong();

                        fos = new FileOutputStream(new File("/home/jun/Desktop/" + fileName));

                        byte[] sendBytes = new byte[1024];
                        long transLen = 0;
                        System.out.println("ThreadId" + this.getId() + "----开始接收文件<" + fileName + ">,文件大小为<" + fileLength + ">----");

                        long count = 0, sum = 0;
                        while(sum + 1024 <= fileLength && (count = dis.read(sendBytes))!=-1){
                            fos.write(sendBytes,0,(int)count);
                            sum+=count;
                            System.out.println("已结收" + count + "比特");
                            if(sum >= fileLength)
                                break;
                        }
                        byte[] bufferLeft=new byte[(int)(fileLength - sum)];
                        dis.read(bufferLeft);
                        fos.write(bufferLeft, 0, (int)(fileLength - sum));
                        System.out.println("已结收" +  (fileLength - sum) + "比特");
                        fos.flush();


//
//                        while (true) {
//                            long read = 0 ;
//                            read = dis.read(sendBytes);
////                            for (byte b: sendBytes){
////                                System.out.println((char)b);
////                            }
////                          if (read == -1)
////                               break;
//                            transLen += read;
//                            System.out.println(transLen);
//                            System.out.println("ThreadId" + this.getId() + "接收文件进度" + 100 * transLen / fileLength + "%...");
//                            if(transLen >= fileLength){
//                                System.out.println((int)(read - (transLen - fileLength)));
//
//                                int i =  0;
//                                for (byte b: sendBytes) {
//                                    System.out.println((char) b);
//                                    i ++;
//                                    if (i == (int)(read - (transLen - fileLength))){
//                                        break;
//                                    }
//                                }
//                                fos.write(sendBytes, 0, (int)(read - (transLen - fileLength)));
//                                fos.flush();
//                                break;
//
//                            }
//                            fos.write(sendBytes, 0, sendBytes.length);
//                            fos.flush();
////                                ByteArrayInputStream bais=new ByteArrayInputStream(sendBytes); //把刚才的部分视为输入流
////                                DataInputStream dis_2 =new DataInputStream(bais);
//
//
//                        }
                        fos.close();
                        System.out.println("ThreadId" + this.getId() + "----接收文件<" + fileName + ">成功-------");
                        System.out.println(client.isConnected());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (dis != null)
                        dis.close();
                    if (fos != null)
                        fos.close();
                    client.close();
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