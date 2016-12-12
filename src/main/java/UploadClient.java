import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 文件上传客户端
 * Created by jun on 16-12-10.
 */
public class UploadClient extends JFrame {
    private static final long serialVersionUID = -8243692833693773812L;
    private String ip;// 服务器ip
    private int port;// 端口
    private Properties prop;
    private List<FileBean> fileBeans = new ArrayList<FileBean>();// 从file.xml读到的文件集合
    private Socket socket; // 客户端socket
    private ByteArrayInputStream bais;
    private SequenceInputStream sis;
    private BufferedOutputStream bos;
    private InputStream fs;

    public UploadClient() {

        // 初始化数据
        initDatas();

        // 开始上传文件
        startUploadFile();

        // 设置窗口属性
        initViews();
    }

    private void initViews() {
        setTitle("上传客户端");
        getContentPane().setLayout(null);
        setBounds(160, 200, 440, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * 开始上传文件....
     */
    public void startUploadFile() {
        try {
            socket = new Socket(ip, port);
            if (!socket.isClosed() && socket.isConnected()) {
                Logger.log("客户端连接成功....");
                System.out.println("客户端连接成功....");
            } else {
                Logger.log("连接失败, 请与开发人员联系....");
                JOptionPane.showMessageDialog(null, "连接失败, 请与开发人员联系");
                return;
            }

            for (FileBean fileBean : fileBeans) {
                String filePath = fileBean.getPath();
                Logger.log("filepath===" + filePath);
                File file =  null;
                if (filePath != null && !"".equals(filePath.trim())) {
                    int indexOf = filePath.lastIndexOf("/");
                    String fileName = filePath.substring(indexOf + 1);

                    if (filePath.startsWith("http://")) {
                        URL url = new URL(filePath);
                        file = new File(filePath);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        if (conn.getResponseCode() == 200) {
                            fs = conn.getInputStream();
                        }else {
                            Logger.log("错误信息:文件网络路径错误:" + filePath);
                        }
                    }else {
                        file = new File(fileBean.getPath());
                        fs = new FileInputStream(file);
                    }
                    Logger.log("filename=" + fileName);
                    long fileSize = file.length();
                    Logger.log("filesize===" + fileSize);
                    if (fileSize / 1024 / 1024 > 70) {
                        Logger.log("文件超过了70M，不能上传");
                        return;
                    }
                    //定义一个256字节的区域来保存文件信息。
                    byte[] b = fileName.getBytes();
                    byte[] info = Arrays.copyOf(b, 256);
                    bais = new ByteArrayInputStream(info);
                    sis = new SequenceInputStream(bais, fs);

                    if (socket.isClosed()) {
                        Logger.log("重新连接成功!");
                        System.out.println("重新连接成功!");
                        socket = new Socket(ip, port);
                    }

                    bos = new BufferedOutputStream(socket.getOutputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = sis.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                        bos.flush();
                    }
                    //关闭
                    socket.shutdownOutput();

                    String result = br.readLine();
                    Logger.log(fileBean.getName() + "上传:" + result);
                    if ("success".equals(result.trim())) {
                        fileBean.setState(1);
                        XmlUtil.changeFileState(prop.getProperty("xmlPath"), fileBean);
                    }
                    br.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (sis != null) {
                    sis.close();
                }
                if (fs != null) {
                    fs.close();
                }
                if (bais != null) {
                    bais.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化数据
     *
     * @throws UnknownHostException
     * @throws Exception
     */
    private void initDatas() {
        try {
            prop = PropUtil.getProp();
            ip = prop.getProperty("ip");
            if (ip.startsWith("http://")) {
                System.out.println(ip);

                ip = InetAddress.getByName(ip).getHostAddress();
                System.out.println(ip);
            }
            ip = "127.0.0.1";
            port = Integer.parseInt(prop.getProperty("port"));
            port = 9000;
//            port = Integer.parseInt(prop.getProperty("port"));
            final String xmlPath = prop.getProperty("xmlPath");
            Logger.log("xmlPath===" + xmlPath);
            System.out.println("xmlPath===" + xmlPath);
            // 因为读的是远程xml,比较耗时，所以放在线程里运行
            ExecutorService executorService = Executors.newCachedThreadPool();
            ArrayList<Future<List<FileBean>>> list = new ArrayList<Future<List<FileBean>>>();
            list.add(executorService.submit(new CallThread(xmlPath)));
            for (Future<List<FileBean>> fs : list) {
                List<FileBean> fileBean = fs.get();
                fileBeans.addAll(fileBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.log("error:" + e.getMessage());
            //JOptionPane.showMessageDialog(null, "error:" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new UploadClient();
                    System.exit(0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

