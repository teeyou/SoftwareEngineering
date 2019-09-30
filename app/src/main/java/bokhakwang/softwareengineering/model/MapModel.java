package bokhakwang.softwareengineering.model;

import java.util.List;

public class MapModel {

    private DistrictDataSource mDistrictDataSource;

    public MapModel(DistrictDataSource districtDataSource) {
        mDistrictDataSource = districtDataSource;
    }

    public List<District> getDistrictList() {
        return mDistrictDataSource.getDistrictList();
    }
}
