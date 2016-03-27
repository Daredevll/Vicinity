package com.vicinity.vicinity.controller.controllersupport;

import com.vicinity.vicinity.R;

/**
 * Created by Jovch on 26-Mar-16.
 */
public class AppearanceManager {

    public static final int BACKGROUND_DAY = R.drawable.bg_day_blurred;
    public static final int BACKGROUND_NIGHT = R.drawable.bg_night_blurred;

    public static int getBackgroundID(){
        //TODO: Implement logic for choosing background  based on dayTime

        return BACKGROUND_NIGHT;
    }
}
