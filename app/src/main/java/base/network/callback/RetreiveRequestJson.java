package base.network.callback;

import java.util.ArrayList;
import java.util.List;

import base.sqlite.model.ContentModel;

public class RetreiveRequestJson extends  BaseRequest{
    private List<ContentModel> content = new ArrayList<>();

    public List<ContentModel> getContent() {
        return content;
    }

    public void setContent(List<ContentModel> content) {
        this.content = content;
    }
}
