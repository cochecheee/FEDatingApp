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
                "https://example.com/travel.jpg",
                true
        ));

        data.add(new ExploreCategory(
                2,
                "Âm nhạc",
                "Kết nối qua âm nhạc yêu thích",
                "https://example.com/music.jpg",
                false
        ));

        data.add(new ExploreCategory(
                3,
                "Thể thao",
                "Tìm bạn đồng hành luyện tập",
                "https://example.com/sports.jpg",
                true
        ));

        data.add(new ExploreCategory(
                4,
                "Nấu ăn",
                "Cùng khám phá ẩm thực",
                "https://example.com/cooking.jpg",
                false
        ));

        data.add(new ExploreCategory(
                5,
                "Sách",
                "Tìm người cùng sở thích đọc sách",
                "https://example.com/books.jpg",
                false
        ));

        data.add(new ExploreCategory(
                6,
                "Thú cưng",
                "Dành cho người yêu thú cưng",
                "https://example.com/pets.jpg",
                true
        ));

        data.add(new ExploreCategory(
                7,
                "Công nghệ",
                "Kết nối với người đam mê công nghệ",
                "https://example.com/tech.jpg",
                false
        ));

        data.add(new ExploreCategory(
                8,
                "Yoga & Thiền",
                "Tìm bạn cùng tập luyện",
                "https://example.com/yoga.jpg",
                false
        ));

        return data;
    }
}