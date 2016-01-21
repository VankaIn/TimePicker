package com.ljj.timepicker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ljj.timepicker.view.FollowTheSunView;
import com.ljj.timepicker.widget.ArrayWheelAdapter;
import com.ljj.timepicker.widget.NumericWheelAdapter;
import com.ljj.timepicker.widget.OnWheelScrollListener;
import com.ljj.timepicker.widget.WheelView;

/**
 * Created by Gunter on 2015/01/14.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tv_am;
    private TextView tv_day;
    private TextView tv_pm;
    private FollowTheSunView sunView;
    private PopupWindow timePicker;
    private WheelView wheel_hour;
    private WheelView wheel_mins;
    private WheelView wheel_day;

    private FollowTheSunTime amTime;
    private FollowTheSunTime dayTime;
    private FollowTheSunTime pmTime;
    private FollowTheSunTime selectedTime = new FollowTheSunTime();

    private int type;
    private static final int TYPE_AM = 0;
    private static final int TYPE_DAY = 1;
    private static final int TYPE_PM = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        amTime = new FollowTheSunTime();
        dayTime = new FollowTheSunTime();
        pmTime = new FollowTheSunTime();
        String new_am = "400";
        String new_day = "1000";
        String new_pm = "1600";
        if (new_am.length() == 3) {
            String str_am_hour = new_am.substring(0, 1);
            String str_am_min = new_am.substring(1, new_am.length());
            amTime.setDay(FollowTheSunTime.TYPE_AM);
            amTime.setHour(Integer.parseInt(str_am_hour));
            amTime.setMin(Integer.parseInt(str_am_min));
        } else {
            String str_am_hour = new_am.substring(0, 2);
            String str_am_min = new_am.substring(2, new_am.length());

            if (Integer.parseInt(str_am_hour) < 13) {
                amTime.setHour(Integer.parseInt(str_am_hour));
                amTime.setMin(Integer.parseInt(str_am_min));
                amTime.setDay(FollowTheSunTime.TYPE_AM);
            } else {
                amTime.setHour(Integer.parseInt(str_am_hour)-12);
                amTime.setMin(Integer.parseInt(str_am_min));
                amTime.setDay(FollowTheSunTime.TYPE_PM);
            }
        }

        if (new_day.length() == 3) {
            String str_day_hour = new_day.substring(0, 1);
            String str_day_min = new_day.substring(1, new_day.length());
            dayTime.setDay(FollowTheSunTime.TYPE_AM);
            dayTime.setHour(Integer.parseInt(str_day_hour));
            dayTime.setMin(Integer.parseInt(str_day_min));
        } else {
            String str_day_hour = new_day.substring(0, 2);
            String str_day_min = new_day.substring(2, new_day.length());

            if (Integer.parseInt(str_day_hour) < 13) {
                dayTime.setHour(Integer.parseInt(str_day_hour));
                dayTime.setMin(Integer.parseInt(str_day_min));
                dayTime.setDay(FollowTheSunTime.TYPE_AM);
            } else {
                dayTime.setHour(Integer.parseInt(str_day_hour)-12);
                dayTime.setMin(Integer.parseInt(str_day_min));
                dayTime.setDay(FollowTheSunTime.TYPE_PM);
            }
        }
        if (new_pm.length() == 3) {
            String str_pm_hour = new_pm.substring(0, 1);
            String str_pm_min = new_pm.substring(1, new_pm.length());
            pmTime.setDay(FollowTheSunTime.TYPE_AM);
            pmTime.setHour(Integer.parseInt(str_pm_hour));
            pmTime.setMin(Integer.parseInt(str_pm_min));
        } else {
            String str_pm_hour = new_pm.substring(0, 2);
            String str_pm_min = new_pm.substring(2, new_pm.length());

            if (Integer.parseInt(str_pm_hour) < 13) {
                pmTime.setDay(FollowTheSunTime.TYPE_AM);
                pmTime.setHour(Integer.parseInt(str_pm_hour));
                pmTime.setMin(Integer.parseInt(str_pm_min));
            } else {
                pmTime.setHour(Integer.parseInt(str_pm_hour) - 12);
                pmTime.setMin(Integer.parseInt(str_pm_min));
                pmTime.setDay(FollowTheSunTime.TYPE_PM);
            }
        }
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);

        sunView = (FollowTheSunView) findViewById(R.id.sunTimeView);
        tv_am = (TextView) findViewById(R.id.tv_am);
        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_pm = (TextView) findViewById(R.id.tv_pm);
        tv_am.setText(formatTime(amTime.getHour(), amTime.getMin(), amTime.getDay()));
        tv_day.setText(formatTime(dayTime.getHour(), dayTime.getMin(), dayTime.getDay()));
        tv_pm.setText(formatTime(pmTime.getHour(), pmTime.getMin(), pmTime.getDay()));
        updateFollowTheSun(false);
        tv_am.setOnClickListener(this);
        tv_day.setOnClickListener(this);
        tv_pm.setOnClickListener(this);
        initTimePicker();
    }

    private String formatTime(int hour, int min, String periodType) {
        return hour + ":" + (min == 0 ? "00" : min) + " " + periodType;
    }

    private void initTimePicker() {
        View view = LayoutInflater.from(this).inflate(R.layout.time_picker, null);
        wheel_hour = (WheelView) view.findViewById(R.id.hour);
        wheel_mins = (WheelView) view.findViewById(R.id.mins);
        wheel_day = (WheelView) view.findViewById(R.id.day);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final TextView tv_save = (TextView) view.findViewById(R.id.tv_save);

        tv_cancel.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        wheel_hour.setCyclic(true);
        wheel_hour.setAdapter(new NumericWheelAdapter(1, 12));

        final String minString[] = new String[]{"00", "15", "30", "45"};
        wheel_mins.setAdapter(new ArrayWheelAdapter<>(minString));

        final String dayString[] = new String[]{FollowTheSunTime.TYPE_AM, FollowTheSunTime.TYPE_PM};
        wheel_day.setAdapter(new ArrayWheelAdapter<>(dayString));

        wheel_hour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                tv_save.setEnabled(false);
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                selectedTime.setHour(wheel.getCurrentItem() + 1);
                Log.e("", "hour:" + selectedTime.getHour());
                tv_save.setEnabled(true);
            }
        });

        wheel_mins.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                tv_save.setEnabled(false);
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                int index = wheel.getCurrentItem();
                selectedTime.setMin(Integer.parseInt(minString[index]));
                Log.e("", "mins:" + selectedTime.getMin());
                tv_save.setEnabled(true);
            }
        });


        wheel_day.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                tv_save.setEnabled(false);
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                int index = wheel.getCurrentItem();
                selectedTime.setDay(dayString[index]);
                Log.e("", "day:" + selectedTime.getDay());
                tv_save.setEnabled(true);
            }
        });

        timePicker = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        timePicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_bg));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_am:
                type = TYPE_AM;
                selectedTime.setHour(amTime.getHour());
                selectedTime.setMin(amTime.getMin());
                selectedTime.setDay(amTime.getDay());
                timePicker.showAtLocation(v, Gravity.CENTER, 0, 0);
                if (amTime.getHour() == 0) {
                    amTime.setHour(1);
                }
                wheel_hour.setCurrentItem(amTime.getHour() - 1);
                wheel_mins.setCurrentItem(getMinsIndex(amTime.getMin()));
                wheel_day.setCurrentItem(getDayIndex(amTime.getDay()));
                break;
            case R.id.tv_day:
                type = TYPE_DAY;
                selectedTime.setHour(dayTime.getHour());
                selectedTime.setMin(dayTime.getMin());
                selectedTime.setDay(dayTime.getDay());
                timePicker.showAtLocation(v, Gravity.CENTER, 0, 0);
                if (amTime.getHour() == 0) {
                    amTime.setHour(1);
                }
                wheel_hour.setCurrentItem(dayTime.getHour() - 1);
                wheel_mins.setCurrentItem(getMinsIndex(dayTime.getMin()));
                wheel_day.setCurrentItem(getDayIndex(dayTime.getDay()));
                break;
            case R.id.tv_pm:
                type = TYPE_PM;
                selectedTime.setHour(pmTime.getHour());
                selectedTime.setMin(pmTime.getMin());
                selectedTime.setDay(pmTime.getDay());
                timePicker.showAtLocation(v, Gravity.CENTER, 0, 0);
                if (amTime.getHour() == 0) {
                    amTime.setHour(1);
                }
                wheel_hour.setCurrentItem(pmTime.getHour() - 1);
                wheel_mins.setCurrentItem(getMinsIndex(pmTime.getMin()));
                wheel_day.setCurrentItem(getDayIndex(pmTime.getDay()));
                break;
            case R.id.tv_cancel:
                timePicker.dismiss();
                break;
            case R.id.tv_save:
                timePicker.dismiss();
                switch (type) {
                    case TYPE_AM:
                        tv_am.setText(formatTime(selectedTime.getHour(), selectedTime.getMin(), selectedTime.getDay()));
                        amTime.setHour(selectedTime.getHour());
                        amTime.setMin(selectedTime.getMin());
                        amTime.setDay(selectedTime.getDay());
                        if (amTime.compareTo(dayTime) != -1) {
                            dayTime = amTime.getNextTime();
                            tv_day.setText(formatTime(dayTime.getHour(), dayTime.getMin(), dayTime.getDay()));
                        }
                        if (dayTime.compareTo(pmTime) != -1 || dayTime.compareTo(FollowTheSunTime.POINT_START) == 0) {
                            pmTime = dayTime.getNextTime();
                            tv_pm.setText(formatTime(pmTime.getHour(), pmTime.getMin(), pmTime.getDay()));
                        }
                        break;
                    case TYPE_DAY:
                        tv_day.setText(formatTime(selectedTime.getHour(), selectedTime.getMin(), selectedTime.getDay()));
                        dayTime.setHour(selectedTime.getHour());
                        dayTime.setMin(selectedTime.getMin());
                        dayTime.setDay(selectedTime.getDay());
                        if (dayTime.compareTo(amTime) != 1){
                            amTime = dayTime.getLastTime();
                            tv_am.setText(formatTime(amTime.getHour(), amTime.getMin(), amTime.getDay()));
                            if (amTime.compareTo(pmTime) == 0){
                                pmTime = amTime.getLastTime();
                                tv_pm.setText(formatTime(pmTime.getHour(), pmTime.getMin(), pmTime.getDay()));
                            }
                        }else if (dayTime.compareTo(pmTime) != -1){
                            pmTime = dayTime.getNextTime();
                            tv_pm.setText(formatTime(pmTime.getHour(), pmTime.getMin(), pmTime.getDay()));
                            if (pmTime.compareTo(amTime) == 0){
                                amTime = pmTime.getNextTime();
                                tv_am.setText(formatTime(amTime.getHour(), amTime.getMin(), amTime.getDay()));
                            }
                        }
                        break;
                    case TYPE_PM:
                        tv_pm.setText(formatTime(selectedTime.getHour(), selectedTime.getMin(), selectedTime.getDay()));
                        pmTime.setHour(selectedTime.getHour());
                        pmTime.setMin(selectedTime.getMin());
                        pmTime.setDay(selectedTime.getDay());
                        if (pmTime.compareTo(dayTime) != 1) {
                            dayTime = pmTime.getLastTime();
                            tv_day.setText(formatTime(dayTime.getHour(), dayTime.getMin(), dayTime.getDay()));
                        }
                        if (dayTime.compareTo(amTime) != 1 || dayTime.compareTo(FollowTheSunTime.POINT_START) == 0) {
                            amTime = dayTime.getLastTime();
                            tv_am.setText(formatTime(amTime.getHour(), amTime.getMin(), amTime.getDay()));
                        }
                        break;
                }
                updateFollowTheSun(true);
                break;
        }
    }



    private void updateFollowTheSun(boolean isSmooth) {
        int amStart = change2Unit(amTime.getHour(), amTime.getMin(), amTime.getDay());
        int dayStart = change2Unit(dayTime.getHour(), dayTime.getMin(), dayTime.getDay());
        if (dayStart < amStart)
            dayStart += 24 * 4;
        int pmStart = change2Unit(pmTime.getHour(), pmTime.getMin(), pmTime.getDay());
        if (pmStart < dayStart)
            pmStart += 24 * 4;
        if (isSmooth)
            sunView.smoothMoveTo(amStart, dayStart - amStart, pmStart - dayStart);
        else sunView.setDate(amStart, dayStart - amStart, pmStart - dayStart);
    }

    private int change2Unit(int hour, int min, String periodType) {
        int cellCount = min / 15;
        if (hour == 12) {
            hour -= 12;
        }
        if (FollowTheSunTime.TYPE_AM.equals(periodType)) {
            return hour * 4 + cellCount + 6 * 4;
        } else {
            return (hour - 6) * 4 + cellCount + 24 * 4;
        }
    }

    private int getMinsIndex(int time) {
        if (time == 00) {
            return 0;
        } else if (time == 15) {
            return 1;
        } else if (time == 30) {
            return 2;
        } else if (time == 45) {
            return 3;
        } else {
            return 0;
        }
    }

    private int getDayIndex(String day) {
        if (FollowTheSunTime.TYPE_AM.equals(day)) {
            return 0;
        } else if (FollowTheSunTime.TYPE_PM.equals(day)) {
            return 1;
        } else {
            return 0;
        }
    }

}
