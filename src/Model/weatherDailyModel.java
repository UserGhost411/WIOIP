package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class weatherDailyModel {
    private StringProperty img,date,temp,wind,hum;
    public weatherDailyModel(String img, String date, String temp, String wind, String hum){
        this.img = new SimpleStringProperty(img);
        this.date = new SimpleStringProperty(date);
        this.temp = new SimpleStringProperty(temp);
        this.wind = new SimpleStringProperty(wind);
        this.hum = new SimpleStringProperty(hum);
    }
    public StringProperty ImgProperty(){
        return img;
    }
    public StringProperty DateProperty(){
        return date;
    }
    public StringProperty TempProperty(){
        return temp;
    }
    public StringProperty WindProperty(){
        return wind;
    }
    public StringProperty HumProperty(){
        return hum;
    }
}
