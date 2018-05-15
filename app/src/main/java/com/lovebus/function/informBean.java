package com.lovebus.function;

public class informBean {

    private String detailUrl;//链接
    private String imageUrl;//图片
    private String title;//标题
    private String detail;//内容

    public informBean(String detailUrl, String title, String detail){

        this.detail = detail;
        this.detailUrl = detailUrl;
        this.title = title;
    }


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}


