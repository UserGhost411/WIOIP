package Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class LocationModel {
    private StringProperty city,loc;
    public LocationModel(String city, String loc){
        this.city = new SimpleStringProperty(city);
        this.loc = new SimpleStringProperty(loc);
    }
    public StringProperty CityProperty(){
        return city;
    }
    public StringProperty LocProperty(){
        return loc;
    }
    public String getCity() { return city.get(); }
}
