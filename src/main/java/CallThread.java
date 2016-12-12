import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by jun on 16-12-10.
 */
class CallThread implements Callable<List<FileBean>> {
    private String xmlPath;

    public CallThread(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    @Override
    public List<FileBean> call() throws Exception {
        return XmlUtil.getFileBeanFromXml(xmlPath);
    }
}