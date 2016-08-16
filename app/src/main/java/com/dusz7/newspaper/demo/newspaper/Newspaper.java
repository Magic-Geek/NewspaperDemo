package com.dusz7.newspaper.demo.newspaper;

/**
 * Created by dusz2 on 2016/8/12 0012.
 */
public class Newspaper {

    private String name;
    private String date;
    private String issue;
    private String totalIssue;

    private final String newspaperCode = "-\\*-";

    public Newspaper(){

    }

    public Newspaper(String name, String date, String issue, String totalIssue){
        this.name = name;
        this.date = date;
        this.issue = issue;
        this.totalIssue = totalIssue;
    }

    public Newspaper(String code){

        //需要修改为从JSON格式中获取信息
        String[] temp = null;
        temp = this.decodeNewspaperInformation(code);
        this.name = temp[0];
        this.date = temp[1];
        this.issue = temp[2];
        this.totalIssue = temp[3];

    }

    public String encodeNewspaperInformation(){

        String encodeResult = "";

        encodeResult = this.name + newspaperCode
                + this.date + newspaperCode
                + this.issue + newspaperCode
                + this.totalIssue;

        return encodeResult;
    }

    public String[] decodeNewspaperInformation(String code){
        String decodeResult[];
        decodeResult = code.split(newspaperCode);
        return decodeResult;
    }

    public void saveNewspaperInformation(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getTotalIssue() {
        return totalIssue;
    }

    public void setTotalIssue(String totalIssue) {
        this.totalIssue = totalIssue;
    }
}
