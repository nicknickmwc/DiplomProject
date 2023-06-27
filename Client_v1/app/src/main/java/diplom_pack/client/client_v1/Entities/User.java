package diplom_pack.client.client_v1.Entities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {

    private ArrayList<String> selfVideoLinks;
    private ArrayList<String> favoriteVideoLinks;

    private String userName;
    private String name;
    private String email;
    private String password;

    private String dateOfBorn;

    //Конструкторы
    //////////////////////////////////////////////////////////////////////
    public User(String email) {
        this.email = email;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String userName, String dateOfBorn) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.dateOfBorn = dateOfBorn;
    }


    //Конец конструкторов
    //////////////////////////////////////////////////////////////////////

    //Сеттеры
    //////////////////////////////////////////////////////////////////////
    public void setSelfVideo(ArrayList<String> selfVideo) {
        this.selfVideoLinks = selfVideo;
    }

    public void setFavoriteVideo(ArrayList<String> favoriteVideo) {
        this.favoriteVideoLinks = favoriteVideo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBorn(String dateOfBorn) {
        this.dateOfBorn = dateOfBorn;
    }
    //Конец сеттеров
    //////////////////////////////////////////////////////////////////////

    //Геттеры
    //////////////////////////////////////////////////////////////////////
    public ArrayList<String> getSelfVideo() {
        return selfVideoLinks;
    }

    public ArrayList<String> getFavoriteVideo() {
        return favoriteVideoLinks;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDateOfBorn() {
        return dateOfBorn;
    }
    //Конец геттеров
    //////////////////////////////////////////////////////////////////////

}
