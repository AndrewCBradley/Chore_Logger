package com.example.chorelogger;

public enum ChoresEnum {

    FEED_JASPER_AM("Feed Jasper Morning"),
    FEED_JASPER_PM("Feed Jasper Afternoon"),
    WALK_JASPER_AM("Walk Jasper Morning"),
    WALK_JASPER_PM("Walk Jasper Afternoon"),
    GET_MAIL("Get Mail");


    private String mName;

    ChoresEnum(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

}
