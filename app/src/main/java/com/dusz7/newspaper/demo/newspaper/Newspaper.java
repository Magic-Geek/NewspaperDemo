package com.dusz7.newspaper.demo.newspaper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dusz2 on 2016/8/12 0012.
 */
public class Newspaper {

    private String name;
    private String date;
    private String issue;
    private String totalIssue;

//    private final String newspaperCode = "-\\*-";

    public Newspaper(){

    }

    public Newspaper(String name, String date, String issue, String totalIssue){
        this.name = name;
        this.date = date;
        this.issue = issue;
        this.totalIssue = totalIssue;
    }

    public Newspaper(String jsonCode){

        //已修改为从JSON格式中获取信息

        try {
            JSONObject jsonObject = new JSONObject(jsonCode);
            this.name = jsonObject.get("name").toString();
            this.date = jsonObject.get("pub_date").toString();
            this.issue = jsonObject.get("jou_id").toString();
            this.totalIssue = jsonObject.get("sub_jou_id").toString();

        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    public String encodeNewspaperInformation(){

        String encodeResult = "";

        encodeResult = "{" +
                "\"jou_id\":\""+this.issue+"\"," +
                "\"sub_jou_id\":\""+this.totalIssue+"\"," +
                "\"name\":\""+this.name+"\"," +
                "\"pub_date\":\""+this.date+"\"" +
                "}";

        return encodeResult;
    }

    @Override
    public String toString(){
        String result = encodeNewspaperInformation();
        return result;
    }

//    public String[] decodeNewspaperInformation(String code){
//        String decodeResult[];
//        decodeResult = code.split(newspaperCode);
//
//        return decodeResult;
//    }

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
