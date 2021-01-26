package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlaceModel {
    private StringProperty city,added,def;
    public PlaceModel(String city, String added, String def){
        this.city = new SimpleStringProperty(city);
        this.added = new SimpleStringProperty(added);
        this.def = new SimpleStringProperty(def);
    }
    public StringProperty AddedProperty(){
        return added;
    }
    public StringProperty CityProperty(){
        return city;
    }
    public StringProperty DefProperty(){
        return def;
    }
}
