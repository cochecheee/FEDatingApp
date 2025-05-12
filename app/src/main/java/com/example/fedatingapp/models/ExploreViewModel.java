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

    /**
     * Tạo dữ liệu mẫu cho các danh mục khám phá dựa trên mảng common_interests
     * @return Danh sách các ExploreCategory
     */
    private List<ExploreCategory> generateSampleData() {
        List<ExploreCategory> data = new ArrayList<>();

        // Travel
        data.add(new ExploreCategory(
                1,
                "Travel",
                "Những người thích du lịch khám phá",
                "https://vr360.com.vn/uploads/images/5-cach-quang-ba-du-lich.jpg",
                true
        ));

        // Cooking
        data.add(new ExploreCategory(
                2,
                "Cooking",
                "Cùng khám phá ẩm thực và công thức nấu ăn",
                "https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2021/10/17/964726/Nau-An.jpg",
                false
        ));

        // Fitness
        data.add(new ExploreCategory(
                3,
                "Fitness",
                "Tìm bạn đồng hành luyện tập gym và fitness",
                "https://www.rrc.ca/well-being/wp-content/uploads/sites/96/2025/01/sport-fitness-box1.jpg",
                true
        ));

        // Reading
        data.add(new ExploreCategory(
                4,
                "Reading",
                "Tìm người cùng sở thích đọc sách và trao đổi tri thức",
                "https://kenh14cdn.com/203336854389633024/2021/1/3/photo-1-16096337476961612322578.jpg",
                false
        ));

        // Movies
        data.add(new ExploreCategory(
                5,
                "Movies",
                "Kết nối với những người đam mê điện ảnh",
                "https://lumiere-a.akamaihd.net/v1/images/p_studio_elio_payoff_poster_v1_b71992a8.jpeg",
                true
        ));

        // Music
        data.add(new ExploreCategory(
                6,
                "Music",
                "Kết nối qua âm nhạc yêu thích",
                "https://daily.jstor.org/wp-content/uploads/2023/01/good_times_with_bad_music_1050x700.jpg",
                false
        ));

        // Art
        data.add(new ExploreCategory(
                7,
                "Art",
                "Những người yêu thích hội họa và nghệ thuật",
                "https://mastermedia.vn/wp-content/uploads/2023/05/art-va-design-1.jpg",
                true
        ));

        // Photography
        data.add(new ExploreCategory(
                8,
                "Photography",
                "Tìm bạn cùng đam mê chụp ảnh và khám phá",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT6qjYGkQv7lsrht3oSTaYAJhGXLYJKtkscHA&s",
                false
        ));

        // Hiking
        data.add(new ExploreCategory(
                9,
                "Hiking",
                "Kết nối với những người đam mê trekking và leo núi",
                "https://vj-prod-website-cms.s3.ap-southeast-1.amazonaws.com/1518801341-1703731145719.jpg",
                true
        ));

        // Gaming
        data.add(new ExploreCategory(
                10,
                "Gaming",
                "Tìm đồng đội và bạn chơi game",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCvub9PV_Ee6Wxl4PRzfsJnUQ0NPqNWVyfsw&s",
                false
        ));

        // Dance
        data.add(new ExploreCategory(
                11,
                "Dance",
                "Tìm bạn cùng đam mê vũ đạo",
                "https://upload.wikimedia.org/wikipedia/commons/3/38/Two_dancers.jpg",
                true
        ));

        // Food
        data.add(new ExploreCategory(
                12,
                "Food",
                "Kết nối với những người yêu thích ẩm thực",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrm45IT7TPDizgfnHZXQPDqLqTyIxJBt0D4Q&s",
                false
        ));

        // Outdoors
        data.add(new ExploreCategory(
                13,
                "Outdoors",
                "Tìm bạn cùng đam mê các hoạt động ngoài trời",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAhmr3g1QBvM9ZGm8whbxInmKParuZqEY6xw&s",
                true
        ));

        // Sports
        data.add(new ExploreCategory(
                14,
                "Sports",
                "Tìm bạn đồng hành luyện tập thể thao",
                "https://katatech.net/uploaded/tin-tuc/mon-the-thao-dot-calo-nhieu-nhat-1.webp",
                false
        ));

        // Tech
        data.add(new ExploreCategory(
                15,
                "Tech",
                "Kết nối với người đam mê công nghệ",
                "https://blog.topcv.vn/wp-content/uploads/2020/09/cong-nghe-thong-tin-la-gi-tn.jpg",
                true
        ));

        // Fashion
        data.add(new ExploreCategory(
                16,
                "Fashion",
                "Tìm bạn cùng đam mê thời trang và phong cách",
                "https://file.hstatic.net/200000503583/article/high-fashion-la-gi-21_15eb1f9733ae4344977098b5bdcaf03f_2048x2048.jpg",
                false
        ));

        // Pets
        data.add(new ExploreCategory(
                17,
                "Pets",
                "Dành cho người yêu thú cưng",
                "https://files.nationalasthma.org.au/images/iStock-1445196818.jpg",
                true
        ));

        // Coffee
        data.add(new ExploreCategory(
                18,
                "Coffee",
                "Dành cho những người yêu thích cà phê",
                "https://www.nescafe.com/nz/sites/default/files/2023-09/NESCAF%C3%89%20Cappuccino.jpg",
                false
        ));

        // Wine
        data.add(new ExploreCategory(
                19,
                "Wine",
                "Kết nối với những người sành rượu vang",
                "https://funix.edu.vn/wp-content/uploads/2022/05/C%C3%A1ch-c%C3%A0i-%C4%91%E1%BA%B7t-Wine-tr%C3%AAn-Ubuntu-%C4%91%E1%BB%83-ch%E1%BA%A1y-ph%E1%BA%A7n-m%E1%BB%81m-Windows.jpg",
                true
        ));

        // Yoga
        data.add(new ExploreCategory(
                20,
                "Yoga",
                "Tìm bạn cùng tập luyện yoga và thiền",
                "https://www.saigondance.vn/wp-content/uploads/2018/08/hinh-anh-tap-yoga.jpg",
                false
        ));

        return data;
    }
}