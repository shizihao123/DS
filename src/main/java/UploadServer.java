import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件上传服务器端
 * Created by jun on 16-12-10.
 */

public class UploadServer extends JFrame {
    private static final long serialVersionUID = -5622620756755192667L;
    private JTextField port;
    private ServerSocket server;
    private volatile boolean isRun = false;
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    public UploadServer() {
        setTitle("上传服务器");
        getContentPane().setLayout(null);

        JLabel label = new JLabel("监听窗口：");
        label.setBounds(86, 80, 74, 15);
        getContentPane().add(label);

        port = new JTextField("9000");
        port.setBounds(178, 77, 112, 21);
        getContentPane().add(port);
        port.setColumns(10);
        /**
         * 启动服务
         */
        final JButton btnStart = new JButton("启动服务器");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                port.setEnabled(false); // 控件黑白不可变
                btnStart.setEnabled(false);
                isRun = true;
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            server = new ServerSocket(Integer.parseInt(port.getText()));
                            System.out.println("服务器开始启动了........");
                            while (isRun) {
                                pool.execute(new UploadThread(server.accept()));
                            };
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            pool.shutdown();
                        }
                    }
                }).start();
            }
        });
        btnStart.setBounds(73, 145, 93, 23);
        getContentPane().add(btnStart);

        JButton btnStop = new JButton("停止服务器");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                port.setEnabled(true);
                btnStart.setEnabled(true);
                isRun = false;
                server = null;
            }
        });
        btnStop.setBounds(209, 145, 93, 23);
        getContentPane().add(btnStop);
        setBounds(600, 200, 380, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new UploadServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 进行文件传输耗内存，所以必须放在线程中运行
     */
    class UploadThread implements Runnable {
        private Socket socket;
        private SimpleDateFormat sdf;
        private BufferedInputStream bis;
        private BufferedOutputStream bos;

        UploadThread(Socket socket) {
            this.socket = socket;
            sdf = new SimpleDateFormat("yyyyMMddHH");
        }


        public void run() {
            try {
                bis = new BufferedInputStream(socket.getInputStream());
                byte[] info = new byte[256];
                bis.read(info);
                String fileName = new String(info).trim();
                String filePath = PropUtil.getProp().getProperty("filePath");
                String fullPath = filePath + "/" + sdf.format(new Date());
                File dirs = new File(fullPath);
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }

                PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
                if (!TextUtil.isEmpty(fileName)) {
                    bos = new BufferedOutputStream(new FileOutputStream(fullPath  + "/" + fileName));
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = bis.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                        bos.flush();
                    }

                    System.out.println(fileName + "  文件传输成功");

                    pw.println("success");
                }else {
                    pw.println("failure");
                }
                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

