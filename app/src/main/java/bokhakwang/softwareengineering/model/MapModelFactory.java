package bokhakwang.softwareengineering.model;

public class MapModelFactory {

    public static MapModel createMapeModel() {
        return new MapModel(new DistrictDataSource());
    }
}
