

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * xml文件解析与生成
 * Created by jun on 16-12-10.
 */
public class XmlUtil {
        /**
         * 从xml文件中获取文件信息
         * @param xmlPath
         * @return
         * @throws Exception
         */
        public static List<FileBean> getFileBeanFromXml(String xmlPath) throws Exception {
            List<FileBean> list = null;
            if (TextUtil.isEmpty(xmlPath)) {
                return null;
            }
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream inputStream = null;
            if (xmlPath.startsWith("http://")) {
                URL url = new URL(xmlPath);
                inputStream = url.openStream();
            } else {
                inputStream = new FileInputStream(new File(xmlPath));
            }
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            FileBean bean = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<FileBean>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("file".equals(parser.getName())) {
                            bean = new FileBean();
                            bean.setId(parser.getAttributeValue(0));
                        } else if ("name".equals(parser.getName())) {
                            bean.setName(parser.nextText());
                        } else if ("path".equals(parser.getName())) {
                            bean.setPath(parser.nextText());
                        } else if ("state".equals(parser.getName())) {
                            int state = Integer.parseInt(parser.nextText());
                            bean.setState(state);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // 添加对象到list中
                        if ("file".equals(parser.getName()) && bean != null && bean.getState() == 0) {
                            list.add(bean);
                            bean = null;
                        }
                        break;
                }
                // 当前解析位置结束，指向下一个位置
                eventType = parser.next();
            }
            return list;
        }

        /**
         * 修改xml内容
         *
         * @param xmlPath
         * @param bean
         */
        public static void changeFileState(String xmlPath, FileBean bean) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = null;
                if (xmlPath.startsWith("http://")) {
                    URL url = new URL(xmlPath);
                    doc = db.parse(url.openStream());
                } else {
                    doc = db.parse(new File(xmlPath));
                }
                NodeList list = doc.getElementsByTagName("file");// 根据标签名访问节点
                for (int i = 0; i < list.getLength(); i++) {// 遍历每一个节点
                    Element element = (Element) list.item(i);
                    String id = element.getAttribute("id");
                    if (id.equals(bean.getId())) {
                        element.getElementsByTagName("state").item(0).getFirstChild().setNodeValue(bean.getState()+"");
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);
                // 设置编码类型
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                StreamResult result = new StreamResult(new FileOutputStream(xmlPath));
                transformer.transform(domSource, result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * 生成新的xml文件
         * @param xmlPath
         */
        public static void createFileXml(String xmlPath, FileBean bean) throws Exception {
            XmlSerializer seria = XmlPullParserFactory.newInstance().newSerializer();
            OutputStream out = null;
            if (xmlPath.startsWith("http://")) {
                URL url = new URL(xmlPath);
                URLConnection conn = url.openConnection();
                out = conn.getOutputStream();
            } else {
                out = new FileOutputStream(new File(xmlPath));
            }
            seria.setOutput(out, "UTF-8"); // 设置输出方式，这里使用OutputStream，编码使用UTF-8
            seria.startDocument("UTF-8", true); // 开始生成xml的文件头
            seria.startTag(null, "files");
            seria.startTag(null, "file");
            seria.attribute(null, "id", bean.getId().toString()); // 添加属性，名称为id
            seria.startTag(null, "name");
            seria.text(bean.getName().toString()); // 添加文本元素
            seria.endTag(null, "name");
            seria.startTag(null, "path");
            seria.text(String.valueOf(bean.getPath()));// 这句话应该可以用来
            seria.endTag(null, "path");
            seria.startTag(null, "state");
            seria.text(bean.getState() + "");
            seria.endTag(null, "state");
            seria.endTag(null, "file");
            seria.endTag(null, "files"); // 标签都是成对的
            seria.endDocument();
            out.flush();
            out.close(); // 关闭输出流
        }

        public static void main(String[] args) throws Exception {
            String xmlPath = PropUtil.getProp().getProperty("xmlPath");
            FileBean bean = new FileBean();
            bean.setId("1");
            bean.setState(1);
            XmlUtil.changeFileState(xmlPath, bean);

        /*
         * List<FileBean> list = XmlUtil.getFileBeanFromXml(xmlPath); for (FileBean bean : list) { System.out.println(bean.getName()); }
         */
        }
}
