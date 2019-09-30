package bokhakwang.softwareengineering.model;

import java.util.ArrayList;
import java.util.List;

public class DistrictDataSource {
    private List<District> mDistrictList;

    public DistrictDataSource() {
        mDistrictList = new ArrayList<>();
        initDistricts();
    }

    public void initDistricts() {
        mDistrictList.add(new District("서울",37.549226f,126.989360f));
        mDistrictList.add(new District("인천",37.455650f,126.698978f));
        mDistrictList.add(new District("대전",36.338256f,127.3922187f));
        mDistrictList.add(new District("전주",35.8278969f,127.1092707f));
        mDistrictList.add(new District("대구",35.830005f,128.563571f));
        mDistrictList.add(new District("광주",35.153514f,126.832454f));
        mDistrictList.add(new District("부산",35.160117f,129.047544f));
        mDistrictList.add(new District("제주",33.499145f,126.529713f));

    }


    public List<District> getDistrictList() {
        return mDistrictList;
    }
}
