package bokhakwang.softwareengineering.model;

public class MapModelFactory {

    public static MapModel createMapModel() {
        return new MapModel(new DistrictDataSource(),new DistrictDetailDataSource());
    }
}
