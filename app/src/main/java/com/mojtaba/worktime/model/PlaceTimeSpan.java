package com.mojtaba.worktime.model;


import java.util.Date;

/**
 * Created by mojtaba on 6/4/15.
 */
public class PlaceTimeSpan {
    private long id;
    private long placeId;
    private long enterDate;
    private long exitDate;
    private boolean ended ;

    public PlaceTimeSpan()
    {
        enterDate = (new Date()).getTime();
        exitDate = Long.MAX_VALUE;
        ended = false;
    }
    public PlaceTimeSpan(long id)
    {
        this();
        this.id = id;
    }
    public PlaceTimeSpan(long id, long placeId)
    {
        this();
        this.id = id;
        this.placeId = placeId ;
    }
    public boolean isEnded()
    {
        return ended;
    }
    public long getId() {
        return id;
    }

    public long getPlaceId() {
        return placeId;
    }

    public long getEnterDate() {
        return enterDate;
    }

    public long getExitDate() {
        return exitDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public void setEnterDate(long enterDate) {
        this.enterDate = enterDate;
    }

    public void setExitDate(long exitDate) {
        this.exitDate = exitDate;
        ended =true;
    }
    public long getSpan()
    {
        return exitDate - enterDate ;
    }
}
