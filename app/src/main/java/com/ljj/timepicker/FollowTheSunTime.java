package com.ljj.timepicker;

import android.support.annotation.NonNull;

/**
 * Created by Gunter on 2015/01/14.
 */
public class FollowTheSunTime implements Comparable<FollowTheSunTime> {

    public static final String TYPE_AM = "AM";
    public static final String TYPE_PM = "PM";
    public static final FollowTheSunTime POINT_START = new FollowTheSunTime(12, 0, TYPE_AM);//开始点
    public static final FollowTheSunTime POINT_FINAL = new FollowTheSunTime(11, 45, TYPE_PM);//结束点
    private static final int UNIT_MIN = 15;//移动的最小距离为15分钟

    private int hour;
    private int min;
    private String day;
    private int tmp_hour;

    public FollowTheSunTime() {
    }

    public FollowTheSunTime(int hour, int min, String day) {
        this.hour = hour;
        this.min = min;
        this.day = day;
        this.tmp_hour = (this.hour == 12) ? 0 : this.hour;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        this.tmp_hour = (this.hour == 12) ? 0 : this.hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getTmpHour(){
        return tmp_hour;
    }

    @Override
    public int compareTo(@NonNull FollowTheSunTime another) {
//        if ((!another.getDay().equals(TYPE_AM) && !another.getDay().equals(TYPE_PM))
//                || (!this.day.equals(TYPE_AM) && !this.day.equals(TYPE_PM))) {
//            return 0;
//        }

        if (this.day.equals(another.getDay())) {
            if (this.tmp_hour == another.getTmpHour()) {
                if (this.min == another.getMin()){
                    return 0;
                }else {
                    return this.min < another.getMin() ? -1 : 1;
                }
            } else {
                return this.tmp_hour < another.getTmpHour() ? -1 : 1;
            }
        } else {
            return this.day.equals(TYPE_AM) ? -1 : 1;
        }
    }

    //获取下一个时间点，如果下一个时间点为开始点的话，返回null
    public FollowTheSunTime getNextTime(){
            FollowTheSunTime nextTime = new FollowTheSunTime();
            if (min == 45){
                nextTime.setMin(0);
                if (hour == 11){
                    nextTime.setHour(hour + 1);
                    if (TYPE_AM.equals(day))
                        nextTime.setDay(TYPE_PM);
                    else if (TYPE_PM.equals(day))
                        nextTime.setDay(TYPE_AM);
                }else if(hour == 12){
                    nextTime.setHour(1);
                    nextTime.setDay(day);
                }else {
                    nextTime.setHour(hour + 1);
                    nextTime.setDay(day);
                }
            } else {
                nextTime.setMin(min + UNIT_MIN);
                nextTime.setHour(hour);
                nextTime.setDay(day);
            }
            return nextTime;
    }

    public FollowTheSunTime getLastTime(){
            FollowTheSunTime lastTime = new FollowTheSunTime();
            if (min == 0){
                lastTime.setMin(45);
                if (hour == 1){
                    lastTime.setHour(12);
                    lastTime.setDay(day);
                }else if (hour == 12){
                    lastTime.setHour(hour - 1);
                    if (TYPE_AM.equals(day))
                        lastTime.setDay(TYPE_PM);
                    else if (TYPE_PM.equals(day))
                        lastTime.setDay(TYPE_AM);
                }else {
                    lastTime.setHour(hour -1);
                    lastTime.setDay(day);
                }
            }else {
                lastTime.setMin(min - UNIT_MIN);
                lastTime.setHour(hour);
                lastTime.setDay(day);
            }
            return lastTime;
    }
}
