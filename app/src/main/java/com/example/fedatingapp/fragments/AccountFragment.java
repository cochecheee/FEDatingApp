package com.example.fedatingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.activities.SettingsActivity;
import com.example.fedatingapp.adapters.SliderAdapter;
import com.example.fedatingapp.databinding.FragmentAccountBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment"; // Tag for logging
    // Use ViewBinding
    private FragmentAccountBinding binding;
    View rootLayout;
    private SliderView sliderView;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        Log.d(TAG, "onCreateView: View inflated");
        return binding.getRoot(); // Return the root view from binding
        // Inflate the layout for this fragment
//        rootLayout = inflater.inflate(R.layout.fragment_account, container, false);
//
//        sliderView = rootLayout.findViewById(R.id.slider_view);
//
//        final SliderAdapter adapter = new SliderAdapter(getActivity());
//
//        sliderView.setSliderAdapter(adapter);
//
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
//        sliderView.startAutoCycle();
//
//        return rootLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Setting up views and listeners");
        // Setup SliderView (using binding)
        setupSlider();
        // Setup Click Listeners
        setupClickListeners();
        // Load initial profile data (example)
        loadProfileData();
    }
    private void setupSlider() {
        if (binding == null) return; // Safety check

        // Use getActivity() or requireContext() for context
        final SliderAdapter adapter = new SliderAdapter(requireContext());
        // this.sliderAdapter = adapter; // Store if needed

        binding.sliderView.setSliderAdapter(adapter);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.sliderView.startAutoCycle();
        Log.d(TAG, "setupSlider: Slider configured");
    }

    private void setupClickListeners() {
        if (binding == null) return; // Safety check

        // Settings Button
        binding.buttonSettingsContainer.setOnClickListener(v -> {
            Log.d(TAG, "Settings button clicked");
            Toast.makeText(getContext(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            // Navigate to SettingsActivity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        // Add Photo Button
        binding.buttonAddPhotoContainer.setOnClickListener(v -> {
            Log.d(TAG, "Add Photo button clicked");
            Toast.makeText(getContext(), "Add Media Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement logic to open image/video picker
            // openMediaPicker();
        });

        // Edit Profile Button
        binding.buttonEditProfileContainer.setOnClickListener(v -> {
            Log.d(TAG, "Edit Profile button clicked");
            Toast.makeText(getContext(), "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to EditProfileActivity
            // Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            // startActivity(intent);
        });

        // Tinder Plus Card
        binding.cardTinderPlus.setOnClickListener(v -> {
            Log.d(TAG, "Tinder Plus card clicked");
            Toast.makeText(getContext(), "Get Tinder Plus Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement logic for Tinder Plus purchase/info screen
        });

        // Optional: Click listener for profile image
        binding.profileImage.setOnClickListener(v -> {
            Log.d(TAG, "Profile image clicked");
            Toast.makeText(getContext(), "Profile Image Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Maybe open a larger view of the image or allow changing it
            // openMediaPicker(); // Could reuse the add media logic
        });
    }
    private void loadProfileData() {
        if (binding == null || getContext() == null) return; // Safety checks

        // --- Placeholder Data ---
        // TODO: Replace this with actual data fetching (API call, database, etc.)
        String name = "Anya Forger";
        int age = 6; // Or read minds? ;)
        String job = "Student / Esper";
        String school = "Eden Academy";
        String imageUrl = "https://cdn.vox-cdn.com/thumbor/2Pa12DUFshuhBYDsO14aQZvn4aQ=/1400x1400/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/23453019/anya_spy_x_family_anime.jpg"; // Example URL

        // --- Update UI Elements ---
        binding.tvProfileNameAge.setText(String.format("%s, %d", name, age));
        binding.tvProfileJob.setText(job);
        binding.tvProfileSchool.setText(school);

        // Load image using Glide (or Picasso)
        Glide.with(this) // Use 'this' (Fragment context)
                .load(imageUrl)
                .placeholder(R.drawable.user_man) // Placeholder while loading
                .error(R.drawable.user_man) // Image to show on error
                .circleCrop() // Make it circular if the source isn't already
                .into(binding.profileImage);
        Log.d(TAG, "loadProfileData: Profile data loaded (placeholder)");
    }

    // --- Optional: Media Picker Logic ---
    /*
    private void openMediaPicker() {
        // Use Intent.ACTION_PICK or ActivityResultLauncher to select image/video
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // You might want to use ActivityResultLauncher for better handling
        // resultLauncher.launch(intent);
        Log.d(TAG, "openMediaPicker: Intent launched");
    }
    */
    /*
    // Example ActivityResultLauncher setup (register in Fragment's onCreate or onAttach)
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedMediaUri = data.getData();
                    Log.d(TAG, "Media selected: " + selectedMediaUri.toString());
                    // TODO: Upload the selected media or update the profile image
                    // Glide.with(this).load(selectedMediaUri).circleCrop().into(binding.profileImage);
                    // uploadMedia(selectedMediaUri);
                }
            }
        });
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up binding to avoid memory leaks
        binding = null;
        Log.d(TAG, "onDestroyView: Binding cleared");
    }
}