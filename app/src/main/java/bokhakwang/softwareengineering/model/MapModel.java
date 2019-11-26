package bokhakwang.softwareengineering.model;

import java.util.List;

public class MapModel {

    private DistrictDataSource mDistrictDataSource;
    private DistrictDetailDataSource mDistrictDetailDataSource;

    public MapModel(DistrictDataSource districtDataSource, DistrictDetailDataSource districtDetailDataSource) {
        mDistrictDataSource = districtDataSource;
        mDistrictDetailDataSource = districtDetailDataSource;

    }

    public List<District> getDistrictList() {
        return mDistrictDataSource.getDistrictList();
    }

    public List<List<District>> getDistrictDetailList() {
        return mDistrictDetailDataSource.getDistrictDetailList();
    }
}
