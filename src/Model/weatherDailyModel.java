package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class weatherDailyModel {
    private StringProperty  date,temp,wind,hum;
    public weatherDailyModel(String date, String temp, String wind, String hum){
        this.date = new SimpleStringProperty(date);
        this.temp = new SimpleStringProperty(temp);
        this.wind = new SimpleStringProperty(wind);
        this.hum = new SimpleStringProperty(hum);
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
