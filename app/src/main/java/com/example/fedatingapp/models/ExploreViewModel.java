package com.example.fedatingapp.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExploreViewModel extends ViewModel {

    private MutableLiveData<List<ExploreCategory>> categories;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> error;

    public ExploreViewModel() {
        categories = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        error = new MutableLiveData<>();

        // Load dữ liệu khi khởi tạo
        loadCategories();
    }

    public LiveData<List<ExploreCategory>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCategories() {
        isLoading.setValue(true);

        try {
            // Trong thực tế, bạn sẽ gọi API hoặc lấy dữ liệu từ Repository tại đây
            // Sử dụng AsyncTask, Executor hoặc các cơ chế bất đồng bộ khác

            // Giả lập việc tải dữ liệu
            new Thread(() -> {
                try {
                    // Giả lập thời gian tải
                    Thread.sleep(500);

                    // Lấy dữ liệu mẫu
                    List<ExploreCategory> data = generateSampleData();

                    // Cập nhật dữ liệu trên main thread
                    categories.postValue(data);
                    error.postValue(null);
                } catch (Exception e) {
                    error.postValue(e.getMessage());
                } finally {
                    isLoading.postValue(false);
                }
            }).start();

        } catch (Exception e) {
            error.setValue(e.getMessage());
            isLoading.setValue(false);
        }
    }

    // Hàm tạo dữ liệu mẫu
    private List<ExploreCategory> generateSampleData() {
        List<ExploreCategory> data = new ArrayList<>();

        data.add(new ExploreCategory(
                1,
                "Du lịch",
                "Những người thích du lịch khám phá",
                "https://vr360.com.vn/uploads/images/5-cach-quang-ba-du-lich.jpg",
                true
        ));

        data.add(new ExploreCategory(
                2,
                "Âm nhạc",
                "Kết nối qua âm nhạc yêu thích",
                "https://cdn.nhathuoclongchau.com.vn/unsafe/800x0/https://cms-prod.s3-sgn09.fptcloud.com/am_nhac_la_gi_nhung_loi_ich_cua_am_nhac_trong_cuoc_song_1_049dffc6ba.png",
                false
        ));

        data.add(new ExploreCategory(
                3,
                "Thể thao",
                "Tìm bạn đồng hành luyện tập",
                "https://katatech.net/uploaded/tin-tuc/mon-the-thao-dot-calo-nhieu-nhat-1.webp",
                true
        ));

        data.add(new ExploreCategory(
                4,
                "Nấu ăn",
                "Cùng khám phá ẩm thực",
                "https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2021/10/17/964726/Nau-An.jpg",
                false
        ));

        data.add(new ExploreCategory(
                5,
                "Book",
                "Tìm người cùng sở thích đọc sách",
                "https://kenh14cdn.com/203336854389633024/2021/1/3/photo-1-16096337476961612322578.jpg",
                false
        ));

        data.add(new ExploreCategory(
                6,
                "Thú cưng",
                "Dành cho người yêu thú cưng",
                "https://bestcargo.vn/wp-content/uploads/2022/07/Hinh-anh-van-chuyen-thu-cung-bang-may-bay.jpg",
                true
        ));

        data.add(new ExploreCategory(
                7,
                "Công nghệ",
                "Kết nối với người đam mê công nghệ",
                "https://blog.topcv.vn/wp-content/uploads/2020/09/cong-nghe-thong-tin-la-gi-tn.jpg",
                false
        ));

        data.add(new ExploreCategory(
                8,
                "Yoga & Thiền",
                "Tìm bạn cùng tập luyện",
                "https://www.saigondance.vn/wp-content/uploads/2018/08/hinh-anh-tap-yoga.jpg",
                false
        ));

        return data;
    }
}