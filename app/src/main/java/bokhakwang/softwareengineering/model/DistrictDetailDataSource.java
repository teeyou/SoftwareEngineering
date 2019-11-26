package bokhakwang.softwareengineering.model;

import java.util.ArrayList;
import java.util.List;

public class DistrictDetailDataSource {
    private List<List<District>> mDistrictDetailList;

    private List<District> mSeoulDistrictList;
    private List<District> mIncheonDistrictList;
    private List<District> mDaejeonDistrictList;
    private List<District> mJeonjulDistrictList;
    private List<District> mDaeguDistrictList;
    private List<District> mGwangjuDistrictList;
    private List<District> mBusanDistrictList;
    private List<District> mJejuDistrictList;

    public DistrictDetailDataSource() {
        mSeoulDistrictList = new ArrayList<>();
        mIncheonDistrictList = new ArrayList<>();
        mDaejeonDistrictList = new ArrayList<>();
        mJeonjulDistrictList = new ArrayList<>();
        mDaeguDistrictList = new ArrayList<>();
        mGwangjuDistrictList = new ArrayList<>();
        mBusanDistrictList = new ArrayList<>();
        mJejuDistrictList = new ArrayList<>();

        mDistrictDetailList = new ArrayList<>();

        initDistricts_seoul();
        initDistricts_incheon();
        initDistricts_daejeon();
        initDistricts_jeonju();
        initDistricts_daegu();
        initDistricts_gwangju();
        initDistricts_busan();
        initDistricts_jeju();

        mDistrictDetailList.add(mSeoulDistrictList);
        mDistrictDetailList.add(mIncheonDistrictList);
        mDistrictDetailList.add(mDaejeonDistrictList);
        mDistrictDetailList.add(mJeonjulDistrictList);
        mDistrictDetailList.add(mDaeguDistrictList);
        mDistrictDetailList.add(mGwangjuDistrictList);
        mDistrictDetailList.add(mBusanDistrictList);
        mDistrictDetailList.add(mJejuDistrictList);
    }

    public void initDistricts_seoul() {
        mSeoulDistrictList.add(new District("강남구",0,0));
        mSeoulDistrictList.add(new District("강동구",0,0));
        mSeoulDistrictList.add(new District("강북구",0,0));
        mSeoulDistrictList.add(new District("강서구",0,0));
        mSeoulDistrictList.add(new District("관악구",0,0));
        mSeoulDistrictList.add(new District("광진구",0,0));
        mSeoulDistrictList.add(new District("구로구",0,0));
        mSeoulDistrictList.add(new District("금천구",0,0));
        mSeoulDistrictList.add(new District("노원구",0,0));
        mSeoulDistrictList.add(new District("도봉구",0,0));
        mSeoulDistrictList.add(new District("동대문구",0,0));
        mSeoulDistrictList.add(new District("동작구",0,0));
        mSeoulDistrictList.add(new District("마포구",0,0));
        mSeoulDistrictList.add(new District("서대문구",0,0));
        mSeoulDistrictList.add(new District("서초구",0,0));
        mSeoulDistrictList.add(new District("성동구",0,0));
        mSeoulDistrictList.add(new District("성북구",0,0));
        mSeoulDistrictList.add(new District("송파구",0,0));
        mSeoulDistrictList.add(new District("양천구",0,0));
        mSeoulDistrictList.add(new District("영등포구",0,0));
        mSeoulDistrictList.add(new District("용산구",0,0));
        mSeoulDistrictList.add(new District("은평구",0,0));
        mSeoulDistrictList.add(new District("종로구",0,0));
        mSeoulDistrictList.add(new District("중구",0,0));
        mSeoulDistrictList.add(new District("중랑구",0,0));
    }

    public void initDistricts_incheon() {
        mIncheonDistrictList.add(new District("강화군",0,0));
        mIncheonDistrictList.add(new District("계양구",0,0));
        mIncheonDistrictList.add(new District("남동구",0,0));
        mIncheonDistrictList.add(new District("동구",0,0));
        mIncheonDistrictList.add(new District("미추홀구",0,0));
        mIncheonDistrictList.add(new District("부평구",0,0));
        mIncheonDistrictList.add(new District("서구",0,0));
        mIncheonDistrictList.add(new District("연수구",0,0));
        mIncheonDistrictList.add(new District("옹진군",0,0));
        mIncheonDistrictList.add(new District("중구",0,0));
    }

    public void initDistricts_daejeon() {
        mDaejeonDistrictList.add(new District("대덕구",0,0));
        mDaejeonDistrictList.add(new District("동구",0,0));
        mDaejeonDistrictList.add(new District("서구",0,0));
        mDaejeonDistrictList.add(new District("유성구",0,0));
        mDaejeonDistrictList.add(new District("중구",0,0));
    }

    public void initDistricts_jeonju() {
        mJeonjulDistrictList.add(new District("덕진구",0,0));
        mJeonjulDistrictList.add(new District("완산구",0,0));
    }

    public void initDistricts_daegu() {
        mDaeguDistrictList.add(new District("남구",0,0));
        mDaeguDistrictList.add(new District("달서구",0,0));
        mDaeguDistrictList.add(new District("달성군",0,0));
        mDaeguDistrictList.add(new District("동구",0,0));
        mDaeguDistrictList.add(new District("북구",0,0));
        mDaeguDistrictList.add(new District("서구",0,0));
        mDaeguDistrictList.add(new District("수성구",0,0));
        mDaeguDistrictList.add(new District("중구",0,0));
    }

    public void initDistricts_gwangju() {
        mGwangjuDistrictList.add(new District("광산구",0,0));
        mGwangjuDistrictList.add(new District("남구",0,0));
        mGwangjuDistrictList.add(new District("동구",0,0));
        mGwangjuDistrictList.add(new District("북구",0,0));
        mGwangjuDistrictList.add(new District("서구",0,0));
    }

    public void initDistricts_busan() {
        mBusanDistrictList.add(new District("강서구",0,0));
        mBusanDistrictList.add(new District("금정구",0,0));
        mBusanDistrictList.add(new District("기장군",0,0));
        mBusanDistrictList.add(new District("남구",0,0));
        mBusanDistrictList.add(new District("동구",0,0));
        mBusanDistrictList.add(new District("동래구",0,0));
        mBusanDistrictList.add(new District("부산진구",0,0));
        mBusanDistrictList.add(new District("북구",0,0));
        mBusanDistrictList.add(new District("사상구",0,0));
        mBusanDistrictList.add(new District("사하구",0,0));
        mBusanDistrictList.add(new District("서구",0,0));
        mBusanDistrictList.add(new District("수영구",0,0));
        mBusanDistrictList.add(new District("연제구",0,0));
        mBusanDistrictList.add(new District("영도구",0,0));
        mBusanDistrictList.add(new District("중구",0,0));
        mBusanDistrictList.add(new District("해운대구",0,0));
    }

    public void initDistricts_jeju() {
        mJejuDistrictList.add(new District("서귀포시",0,0));
        mJejuDistrictList.add(new District("제주시",0,0));
    }

    public List<List<District>> getDistrictDetailList() {
        return mDistrictDetailList;
    }
}
