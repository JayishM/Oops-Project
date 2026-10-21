package com.nightmarket.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.model.CreateListingRequest;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;

import java.math.BigDecimal;

public class AddItemFragment extends Fragment {

    private ImageView btnAddBack;
    private EditText etTitle;
    private Spinner spinnerCategory;
    private EditText etPrice;
    private Spinner spinnerCondition;
    private EditText etDescription;
    private EditText etSellerName;
    private EditText etSellerPhone;
    private EditText etSellerEmail;
    private EditText etMeetupArea;
    private CheckBox cbNegotiable;
    private ProgressBar progressBarAdd;
    private Button btnPublish;

    private RemoteListingRepository repository;

    private final String[] categoryNames = {
            "Textbooks & Books", "Electronics & Gadgets", "Fashion & Apparel",
            "Hostel & Room Essentials", "Sports & Fitness", "Free Stuff", "Other"
    };
    private final String[] categoryValues = {
            "TEXTBOOKS", "ELECTRONICS", "CLOTHING", "HOSTEL_ESSENTIALS",
            "SPORTS_FITNESS", "FREE_STUFF", "OTHER"
    };

    private final String[] conditionNames = {"Like new", "Brand new", "Good", "Fair"};
    private final String[] conditionValues = {"LIKE_NEW", "BRAND_NEW", "GOOD", "FAIR"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new RemoteListingRepository(requireContext());
        bindViews(view);
        setupSpinners();
        setupListeners();
    }

    private void bindViews(View view) {
        btnAddBack = view.findViewById(R.id.btnAddBack);
        etTitle = view.findViewById(R.id.etTitle);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etPrice = view.findViewById(R.id.etPrice);
        spinnerCondition = view.findViewById(R.id.spinnerCondition);
        etDescription = view.findViewById(R.id.etDescription);
        etSellerName = view.findViewById(R.id.etSellerName);
        etSellerPhone = view.findViewById(R.id.etSellerPhone);
        etSellerEmail = view.findViewById(R.id.etSellerEmail);
        etMeetupArea = view.findViewById(R.id.etMeetupArea);
        cbNegotiable = view.findViewById(R.id.cbNegotiable);
        progressBarAdd = view.findViewById(R.id.progressBarAdd);
        btnPublish = view.findViewById(R.id.btnPublish);
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, conditionNames);
        spinnerCondition.setAdapter(condAdapter);
    }

    private void setupListeners() {
        btnAddBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnPublish.setOnClickListener(v -> submitListing());
    }

    private void submitListing() {
        String title = etTitle.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String sellerName = etSellerName.getText().toString().trim();
        String phone = etSellerPhone.getText().toString().trim();
        String email = etSellerEmail.getText().toString().trim();
        String meetup = etMeetupArea.getText().toString().trim();

        // Validations
        if (title.length() < 3) {
            etTitle.setError("Title must be at least 3 characters");
            etTitle.requestFocus();
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.isEmpty() ? "0" : priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                etPrice.setError("Price cannot be negative");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Enter a valid price");
            etPrice.requestFocus();
            return;
        }

        if (description.length() < 10) {
            etDescription.setError("Please describe the item (at least 10 characters)");
            etDescription.requestFocus();
            return;
        }

        if (sellerName.length() < 2) {
            etSellerName.setError("Enter your name (at least 2 characters)");
            etSellerName.requestFocus();
            return;
        }

        if (phone.isEmpty() && email.isEmpty()) {
            Toast.makeText(getContext(), "Provide at least one contact method (phone or email)", Toast.LENGTH_LONG).show();
            etSellerPhone.setError("Phone or email required");
            etSellerPhone.requestFocus();
            return;
        }

        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etSellerEmail.setError("Enter a valid email address");
            etSellerEmail.requestFocus();
            return;
        }

        CreateListingRequest request = new CreateListingRequest();
        request.setTitle(title);
        request.setPrice(price);
        request.setDescription(description);
        request.setCategory(categoryValues[spinnerCategory.getSelectedItemPosition()]);
        request.setCondition(conditionValues[spinnerCondition.getSelectedItemPosition()]);
        request.setSellerName(sellerName);
        request.setSellerPhone(phone);
        request.setSellerEmail(email);
        request.setMeetupArea(meetup.isEmpty() ? "Campus meetup spot" : meetup);
        request.setNegotiable(cbNegotiable.isChecked());

        progressBarAdd.setVisibility(View.VISIBLE);
        btnPublish.setEnabled(false);

        repository.createListing(request, new RemoteListingRepository.ApiCallback<Listing>() {
            @Override
            public void onSuccess(Listing result) {
                if (!isAdded()) return;
                progressBarAdd.setVisibility(View.GONE);
                btnPublish.setEnabled(true);
                Toast.makeText(getContext(), "Listing published successfully! 🎉", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToProductDetail(result.getId());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                progressBarAdd.setVisibility(View.GONE);
                btnPublish.setEnabled(true);
                Toast.makeText(getContext(), "Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
