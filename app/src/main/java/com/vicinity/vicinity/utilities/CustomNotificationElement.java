package com.vicinity.vicinity.utilities;

/**
 * Created by Jovch on 06-Apr-16.
 * CustomNotificationElement class for easier creation and manipulation of inter-user Communication
 */
public class CustomNotificationElement {


    private String customerId;
    private String restaurantId;
    private int peopleCount;
    private String comment;
    private String time;
    private String date;
    private boolean isConfirmed;
    private boolean isAnswer;
    String placeName;

    public CustomNotificationElement(String customerId, String restaurantId, int peopleCount, String comment, String time, String date, boolean isConfirmed, boolean isAnswer, String placeName) {

        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.peopleCount = peopleCount;
        this.comment = comment;
        this.time = time;
        this.date = date;
        this.isConfirmed = isConfirmed;
        this.isAnswer = isAnswer;
        this.placeName = placeName;
    }


    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public String getPlaceName() {
        return placeName;
    }
}
