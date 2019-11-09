package com.usf.pickup.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.util.ArrayUtils;
import com.squareup.picasso.Picasso;
import com.usf.pickup.BottomNav;
import com.usf.pickup.Pickup;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.MyGames;
import com.usf.pickup.api.models.User;
import com.usf.pickup.helpers.RoundedCornersTransform;
import com.usf.pickup.ui.search.SearchAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ProfileViewModel profileViewModel;
    private boolean editMode;
    private SearchAdapter gamesAdapter;
    private EditText profileName;
    private EditText profileDesc;
    private ImageButton editBtn;
    private ListView myGamesList;
    String currentPhotoPath;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageView profilePicture = root.findViewById(R.id.profile_pic);
        profileName = root.findViewById(R.id.display_name);
        profileDesc = root.findViewById(R.id.profile_description);
        editBtn = root.findViewById(R.id.edit_btn);
        myGamesList = root.findViewById(R.id.current_games_list_view);

        profileViewModel.searchMyGames();

        gamesAdapter = new SearchAdapter(root.getContext());
        profileViewModel.getMyGames().observe(this, new Observer<ApiResult<MyGames>>() {

            @Override
            public void onChanged(ApiResult<MyGames> apiResult) {
                gamesAdapter.updateResults(ArrayUtils.concat(apiResult.getData().getGames(), apiResult.getData().getOrganizedGames()));
            }
        });

        editMode = false;

        profileName.setEnabled(false);
        profileDesc.setEnabled(false);

        profileViewModel.getProfileFormState().observe(this, new Observer<ProfileFormState>() {
            @Override
            public void onChanged(@Nullable ProfileFormState profileFormState) {
                if (profileFormState == null) {
                    return;
                }

                showValidation(profileFormState);
            }
        });

        profileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null){
                    profileName.setText(user.getDisplayName());
                    profileDesc.setText(user.getProfileDescription());

                    if(profileViewModel.getMyGames().getValue() != null){
                        for (Game g: profileViewModel.getMyGames().getValue().getData().getOrganizedGames()) {
                            g.getOrganizer().setProfilePicture(user.getProfilePicture());
                        }

                        profileViewModel.getMyGames().setValue(profileViewModel.getMyGames().getValue());
                    }

                    if(user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()){
                        Picasso.get().load(getString(R.string.api_url) +
                                getString(R.string.static_files) + "/" + user.getProfilePicture())
                                .centerCrop()
                                .transform(new RoundedCornersTransform(50))
                                .fit()
                                .into(profilePicture);
                    }
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                profileViewModel.profileDataChanged(profileName.getText().toString(), profileDesc.getText().toString());
            }
        };
        profileName.addTextChangedListener(afterTextChangedListener);
        profileDesc.addTextChangedListener(afterTextChangedListener);

        myGamesList.setDivider(null);
        myGamesList.setDividerHeight(0);
        myGamesList.setAdapter(gamesAdapter);

        editBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(editMode) {
                    profileViewModel.updateDisplayName(profileName.getText().toString());
                    profileViewModel.updateProfileDescription(profileDesc.getText().toString());
                    Toast.makeText(getContext(), "Profile changes saved!", Toast.LENGTH_SHORT).show();
                    editBtn.setImageResource(R.drawable.ic_edit_black_24dp);
                    profileName.setEnabled(false);
                    profileDesc.setEnabled(false);

                }
                else {
                    showValidation(profileViewModel.getProfileFormState().getValue());
                    editBtn.setImageResource(R.drawable.ic_done_black_24dp);
                    profileName.setEnabled(true);
                    profileDesc.setEnabled(true);
                }

                editMode = !editMode;
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        return root;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.usf.pickup.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            profileViewModel.updateProfilePicture(compressImage());
        }
    }

    public byte[] compressImage() {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(currentPhotoPath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(currentPhotoPath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(currentPhotoPath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return out.toByteArray();
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void showValidation(ProfileFormState profileFormState){
        // Only worry about showing errors in edit mode
        if(editMode){
            editBtn.setEnabled(profileFormState.isDataValid());

            // Clear existing errors
            profileName.setError(null);
            profileDesc.setError(null);

            if (profileFormState.getNameError() != null) {
                profileName.setError(getString(profileFormState.getNameError()));
            }
            if (profileFormState.getDescriptionError() != null) {
                profileDesc.setError(getString(profileFormState.getDescriptionError()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((BottomNav) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}