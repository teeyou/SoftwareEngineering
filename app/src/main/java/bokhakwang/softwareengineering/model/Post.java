package bokhakwang.softwareengineering.model;

import com.google.firebase.firestore.GeoPoint;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Post implements Serializable {
    private String id;
    private String author;
    private transient GeoPoint LatLng;
    private List<String> images;
    private String time;
    private String district;
    private String detail_location;
    private String contents;
    private String password;
}
