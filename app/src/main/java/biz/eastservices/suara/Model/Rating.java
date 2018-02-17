package biz.eastservices.suara.Model;

/**
 * Created by reale on 2/9/2018.
 */

public class Rating {
    public float ratingValue;
    public String ratingComment;

    public Rating() {
    }

    public Rating(float ratingValue, String ratingComment) {
        this.ratingValue = ratingValue;
        this.ratingComment = ratingComment;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }
}
