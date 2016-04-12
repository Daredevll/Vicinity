package com.vicinity.vicinity.dataBase;


import com.vicinity.vicinity.model.Place;

import java.util.ArrayList;

public interface IPlacesDAO {

    void addPlace(Place place);

    void deletePlace(Place place);

    void updatePlace(Place oldPlace, Place newPlace);

    ArrayList<Place> getAllPlaces();
}
