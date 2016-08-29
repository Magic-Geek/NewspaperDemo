package com.dusz7.newspaper.demo.newspaper;

/**
 * Created by dusz2 on 2016/8/24 0024.
 */
public class GettingNewspaper {
    private Newspaper newspaper;
    private String phone = "error";
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

    public String getLastGetting(){
        String gettingInformation = "";

        if (phone !="error"){

            gettingInformation = "{" +
                    "\"name\":\""+this.newspaper.getName()+"\"," +
                    "\"jou_id\":\""+this.newspaper.getTotalIssue()+"\"," +
//                "\"phone\":\""+this.phone+"\"," +
                    "\"station\":\""+this.location+"\"," +
                    "\"date\":\""+this.time+"\"" +
                    "}";
        }else {
            gettingInformation = "error";
        }

        return gettingInformation;
    }

    public GettingNewspaper(){

    }

    @Override
    public String toString(){

        String result = getGettingInformation();
        return result;

    }
}
