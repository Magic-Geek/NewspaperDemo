package com.dusz7.newspaper.demo.newspaper;

/**
 * Created by dusz2 on 2016/8/24 0024.
 */
public class GettingNewspaper {
    private Newspaper newspaper;
    private String phone = "test1";
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
                "\"name\":\""+this.newspaper.getName()+"\"," +
                "\"jou_id\":\""+this.newspaper.getTotalIssue()+"\"," +
//                "\"phone\":\""+this.phone+"\"," +
                "\"station\":\""+this.location+"\"" +
//                "\"time\":\""+this.time+"\"" +
                "}";

        return gettingInformation;
    }

    public GettingNewspaper(){

    }

    @Override
    public String toString(){
        if(phone == "test1"){
            return "error";
        }else {
            String result = getGettingInformation();
            return result;
        }

    }
}
