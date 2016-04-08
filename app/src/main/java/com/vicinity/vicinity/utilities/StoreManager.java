package com.vicinity.vicinity.utilities;

/**
 * Created by Jovch on 08-Apr-16.
 */
public class StoreManager {

    private static StoreManager instance;

    public static StoreManager getInstance(){
        if (instance == null){
            instance = new StoreManager();
        }
        return instance;
    }

    private StoreManager(){

    }



    public void removeNotification(CustomNotificationElement cne){

    }





}
