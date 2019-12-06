package com.example.cwpar.mymp3playerproject2;

public class MyDataDAO {
    private String singer;
    private String title;
    private String janre;
    private int rate;
    private String fileName;

    public MyDataDAO(String singer, String title, String janre, int rate) {
        this.singer = singer;
        this.title = title;
        this.janre = janre;
        this.rate = rate;
    }

    public MyDataDAO(String singer, String title, String janre, int rate, String fileName) {
        this.singer = singer;
        this.title = title;
        this.janre = janre;
        this.rate = rate;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJanre() {
        return janre;
    }

    public void setJanre(String janre) {
        this.janre = janre;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
