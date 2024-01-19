package com.example.plantonic.ui.productDetailsScreen;

import static com.example.plantonic.utils.constants.IntentConstants.PRODUCT_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.plantonic.R;
import com.example.plantonic.firebaseClasses.FavouriteItem;
import com.example.plantonic.firebaseClasses.ProductItem;
import com.example.plantonic.ui.activity.home.HomeActivity;
import com.example.plantonic.ui.bottomSheet.proceedToCheckout.ProceedToBottomSheet;
import com.example.plantonic.utils.CartUtil;
import com.example.plantonic.utils.FavUtil;
import com.example.plantonic.utils.OrdersUtil;
import com.example.plantonic.utils.ProductUtil;
import com.example.plantonic.utils.crypto.EncryptUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProductViewFragment extends Fragment {
    com.denzcoskun.imageslider.ImageSlider imageSlider;
    TextView addToCartBtn, goToCartBtn;
    TextView name, productPrice, productActualPrice, productDescription, productDetails, productDiscount, product_breath, product_height, product_length, product_weight;
    ImageView backBtn;
    CircleImageView shareBtn, favouriteBtn;
    NestedScrollView productDetailsScrollView;
    ProgressBar progressBar;
    int productNo = 1;
    TextView integer_number;
    TextView decrease, increase;
    View view;


    String productId;
    private ProductViewModel productViewModel;
    private boolean isFavourite = false;
    private boolean isCart = false;

    final int[] callBack = {0};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_view, container, false);
        imageSlider = view.findViewById(R.id.productImages);
        backBtn = view.findViewById(R.id.backBtn);
        addToCartBtn = view.findViewById(R.id.addToCartBtn);
        goToCartBtn = view.findViewById(R.id.goToCartBtn);
        name = view.findViewById(R.id.productName);
        shareBtn = view.findViewById(R.id.shareBtn);
        favouriteBtn = view.findViewById(R.id.favouriteBtn);
        productPrice = view.findViewById(R.id.productPrice);
        productActualPrice = view.findViewById(R.id.realPrice);
        productDetails = view.findViewById(R.id.productDetails);
        productDescription = view.findViewById(R.id.productDescription);
        productDiscount = view.findViewById(R.id.discount);
        integer_number = view.findViewById(R.id.integer_number);
        decrease = view.findViewById(R.id.decrease);
        increase = view.findViewById(R.id.increase);
        productDetailsScrollView = view.findViewById(R.id.productDetailsScrollView);
        progressBar = view.findViewById(R.id.productDetailsProgressBar);
        product_breath = view.findViewById(R.id.product_breath);
        product_height = view.findViewById(R.id.product_height);
        product_length = view.findViewById(R.id.product_length);
        product_weight = view.findViewById(R.id.product_weight);


        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        try {
            productId = getArguments().getString(PRODUCT_ID);
            progressBar.setVisibility(View.VISIBLE);
            productDetailsScrollView.setVisibility(View.GONE);
            addToCartBtn.setVisibility(View.GONE);
        } catch (Exception e) {
            productId = null;
            getParentFragmentManager().popBackStackImmediate();
        }


        if (productId != null) {

            /*
              Getting all data about product
             */
            productViewModel.getProductDetailsFromId(productId).observe(getViewLifecycleOwner(), new Observer<ProductItem>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onChanged(ProductItem productItem) {
                    if (productItem != null && Objects.equals(productItem.productId, productId)) {
                        productDetailsScrollView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        addToCartBtn.setVisibility(View.VISIBLE);

                        if (productItem.productName.length() > 20){
                            productDetails.setText(productItem.productName.substring(0, 20) + "...");
                        }else{
                            productDetails.setText(productItem.productName);
                        }

                        name.setText(productItem.productName);
                        productActualPrice.setText("₹" + productItem.listedPrice);
                        productPrice.setText("₹" + productItem.actualPrice);
                        int realPrice = Integer.parseInt(productItem.listedPrice);
                        int price = Integer.parseInt(productItem.actualPrice);
                        int discount = (realPrice - price) * 100 / realPrice;
                        productDiscount.setText(discount + "% off");
                        productDescription.setText(productItem.productDescription);
                        product_breath.setText(productItem.getBreadth() + " cm");
                        product_height.setText(productItem.getHeight() + " cm");
                        product_length.setText(productItem.getLength() + " cm");
                        product_weight.setText(productItem.getWeight() + " kg");


                        //share button of Product
                        shareBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(Intent.ACTION_SEND);
                                myIntent.setType("text/plain");
                                String body = null;
                                try {
                                    String encryptedString = EncryptUtil.Companion.encrypt(productItem.productId.toString());
                                    Log.d("-----------en: ", encryptedString);
//                                    String decryptedString = EncryptUtil.Companion.decrypt(encryptedString);
//                                    Log.d("-----------de: ", decryptedString);

                                    body = "Hey, checkout this awesome plant I found on Plantonic - " + productItem.productName + "\n" + "Click here " + "https://plantonic.co.in/p/" + encryptedString;
                                    String sub = "Checkout this plant on Plantonic";
                                    myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                                    myIntent.putExtra(Intent.EXTRA_TEXT, body);
                                    startActivity(Intent.createChooser(myIntent, "Share Using"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        // Adding listener to
                        // favourite Button of Product

                        favouriteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isFavourite) {
                                    productViewModel.addToFav(new FavouriteItem(FirebaseAuth.getInstance().getUid()
                                            , productItem.productId, productItem.merchantId, System.currentTimeMillis()));
                                } else {
                                    productViewModel.removeFromFav(FirebaseAuth.getInstance().getUid(), productItem.productId);
                                }
                            }
                        });


                        ArrayList<SlideModel> slideModels = new ArrayList<>();

                        if (!Objects.equals(productItem.imageUrl1, "")) {
                            slideModels.add(new SlideModel(productItem.imageUrl1, ScaleTypes.CENTER_CROP));
                        }
                        if (!Objects.equals(productItem.imageUrl2, "")) {
                            slideModels.add(new SlideModel(productItem.imageUrl2, ScaleTypes.CENTER_CROP));
                        }
                        if (!Objects.equals(productItem.imageUrl3, "")) {
                            slideModels.add(new SlideModel(productItem.imageUrl3, ScaleTypes.CENTER_CROP));
                        }
                        if (!Objects.equals(productItem.getImageUrl4(), "")) {
                            slideModels.add(new SlideModel(productItem.getImageUrl4(), ScaleTypes.CENTER_CROP));
                        }
                        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                    } else {
                        addToCartBtn.setVisibility(View.GONE);
                        productDetailsScrollView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });

            /*
            Checking if product is favourite
            */
            productViewModel.checkIfFav(FirebaseAuth.getInstance().getUid(), productId).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isFav) {
                    isFavourite = isFav;

                    if (isFav) {
                        favouriteBtn.setForegroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark));

                    } else {
                        favouriteBtn.setForegroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.light_gray));

                    }

                }
            });

            /*
            Checking if product is added to cart
            */
            productViewModel.checkIfAddedToCart(FirebaseAuth.getInstance().getUid(), productId).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isAdded) {
                    if (isAdded) {
                        goToCartBtn.setVisibility(View.VISIBLE);

                    } else {
                        goToCartBtn.setVisibility(View.GONE);
                    }
                    isCart = isAdded;
                }
            });



            /*
             * If added to the cart then
             * Get the cart quantity
             * Everytime added
             * */
//            productViewModel.getCurrentCartQuantity(FirebaseAuth.getInstance().getUid(), productId);

            // Current Cart Quantity
//            productViewModel.currentCartQuantity.observe(getViewLifecycleOwner(), new Observer<CartItem>() {
//                @Override
//                public void onChanged(CartItem cartItem) {
//                    if (cartItem != null && Objects.equals(cartItem.getProductId(), productId) && cartItem.getQuantity() > 0L) {
//                        if (callBack[0] > 0) {
////                            Toast.makeText(requireContext(), "Added product: " + cartItem.getQuantity() + " items", Toast.LENGTH_SHORT).show();
//                            ProceedToBottomSheet cartBottomSheet = new ProceedToBottomSheet();
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PRODUCT_ID, cartItem.getProductId());
//                            bundle.putString(IntentConstants.PRODUCT_QUANTITY, cartItem.getQuantity().toString());
//                            cartBottomSheet.setArguments(bundle);
//                            cartBottomSheet.show(requireActivity().getSupportFragmentManager(),"TAG");
//                        }
//                        callBack[0] += 1;
//                    }
//                }
//
//            });

        }


        //Add to cart product item
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productViewModel.addToCart(FirebaseAuth.getInstance().getUid(), productId, Long.parseLong(integer_number.getText().toString()));
                    ProceedToBottomSheet cartBottomSheet = new ProceedToBottomSheet();
                    cartBottomSheet.show(requireActivity().getSupportFragmentManager(), "TAG");
//                }
            }
        });

        // Go to cart btn click
        goToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(FavUtil.lastFragment, "product") || Objects.equals(FavUtil.lastFragment, "cart")) {
                    CartUtil.lastFragment = "product";
                    FavUtil.lastFragment = "cart";
                } else if (Objects.equals(ProductUtil.lastFragment, "cart")) {
                    ProductUtil.lastFragment = "";
                } else {
                    CartUtil.lastFragment = "home";
                }
//                    Navigation.findNavController(ProductViewFragment.this.view).popBackStack(R.id.homeFragment, true);
                Navigation.findNavController(ProductViewFragment.this.view).navigate(R.id.cartFragment, null, new NavOptions.Builder().setPopUpTo(R.id.cartFragment, true).build());

            }
        });

        //back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        //product Items No decrease
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseInteger(view);
            }
        });

        //product Items No. increase
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseInteger(view);
            }
        });
        return view;
    }

    public void increaseInteger(View view) {
        productNo = productNo + 1;
        display(productNo);

    }

    public void decreaseInteger(View view) {
        productNo = productNo - 1;
        if (productNo > 1) {
            display(productNo);
        } else {
            productNo = 1;
            display((1));
        }
    }

    private void display(int number) {
        integer_number.setText("" + number);
    }

    @Override
    public void onStart() {
        super.onStart();
        display(productNo);

        // Change status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        // Hide the nav bar
        ((HomeActivity) requireActivity()).hideBottomNavBar();

//        callBack[0] = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Change status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        // Show the nav bar
        if (!Objects.equals(CartUtil.lastFragment, "home")) {
            ((HomeActivity) requireActivity()).showBottomNavBar();
        }

//        callBack[0] = 0;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        callBack[0] = 0;
//    }

    //backspaced backstack
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (Objects.equals(ProductUtil.lastFragment, "cart")) {
                    ProductUtil.lastFragment = "";
                }

//                if (Objects.equals(CartUtil.lastFragment, "")){
//                    FavUtil.lastFragment = "";
//                }

                if (Objects.equals(ProductUtil.lastFragment, "orders")){
                    ProductUtil.lastFragment = "";
                }

                if (Objects.equals(OrdersUtil.lastFragment, "product")){
                    ((HomeActivity)requireActivity()).hideBottomNavBar();
                }

                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.popBackStackImmediate();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}