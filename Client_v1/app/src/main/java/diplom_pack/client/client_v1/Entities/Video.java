package diplom_pack.client.client_v1.Entities;

import android.net.Uri;

import java.util.Map;

public class Video {

    private Uri uri;

    private String Uid;
    private String name;
    private String Url;
    private String countOfLikes;
    private String countOfComments;
    private String dateOfCreate;
    private String authorName;
    private int videoNumber;

    public Video() {

    }
    public Video(Uri uri) {
        this.uri = uri;
    }

    public String getUid() {
        return Uid;
    }

    public String getDateOfCreate() {
        return dateOfCreate;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return Url;
    }

    public String getCountOfLikes() {
        return countOfLikes;
    }

    public String getCountOfComments() {
        return countOfComments;
    }

    public int getVideoNumber() {
        return videoNumber;
    }

    public Uri getUri() {
        return uri;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setCountOfLikes(String countOfLikes) {
        this.countOfLikes = countOfLikes;
    }

    public void setCountOfComments(String countOfComments) {
        this.countOfComments = countOfComments;
    }

    public void setVideoNumber(int videoNumber) {
        this.videoNumber = videoNumber;
    }

    public void setDateOfCreate(String dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
