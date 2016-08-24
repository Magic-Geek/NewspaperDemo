package com.dusz7.newspaper.demo.newspaper;

/**
 * Created by dusz2 on 2016/8/24 0024.
 */
public class GettingNewspaper {
    private Newspaper newspaper;
    private String phone;
    private String location;
    private String time;

    public GettingNewspaper(Newspaper newspaper, String phone, String location, String time){
        this.newspaper = newspaper;
        this.phone = phone;
        this.location = location;
        this.time = time;
    }

    public String getGettingInformation(){
        String gettingInformation = "";

        gettingInformation = "{" +
                "\"sub_jou_id\":\""+this.newspaper.getTotalIssue()+"\"," +
                "\"name\":\""+this.newspaper.getName()+"\"," +
                "\"phone\":\""+this.phone+"\"," +
                "\"location\":\""+this.location+"\"," +
                "\"time\":\""+this.time+"\"" +
                "}";

        return gettingInformation;
    }

    @Override
    public String toString(){
        String result = getGettingInformation();
        return result;
    }
}
