package acidsore;

import java.io.File;
import java.io.Serializable;

public class FileNames implements Serializable {
    private File[] content;

    public FileNames() {
    }

    public File[] getContent() {
        return content;
    }

    public void setContent(File[] content) {
        this.content = content;
    }
}
