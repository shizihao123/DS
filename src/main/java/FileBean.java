/**
 * 文件bean
 * Created by jun on 16-12-10.
 */
public class FileBean {
        private String id; //id
        private String name;//文件名称
        private String path;//文件路径
        private int state;//上传结果0未上传,1已经上传,2上传错误


        public FileBean() {
        }

        public FileBean(String id, int state) {
            this.id = id;
            this.state = state;
        }

        public FileBean(String id, String name, String path, int state) {
            this.id = id;
            this.name = name;
            this.path = path;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "FileBean [id=" + id + ", name=" + name + ", path=" + path + ", state=" + state + "]";
        }
}
