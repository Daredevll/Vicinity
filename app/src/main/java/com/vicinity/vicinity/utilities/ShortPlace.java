package com.vicinity.vicinity.utilities;

import java.io.Serializable;

/**
 * Created by Jovch on 08-Apr-16.
 *
 */
public class ShortPlace implements Serializable{

    private String id;
    private String name;
    private String address;

    public ShortPlace(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }


}
