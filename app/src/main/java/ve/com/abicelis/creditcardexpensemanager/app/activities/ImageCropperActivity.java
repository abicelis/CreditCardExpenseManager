package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;

/**
 * Created by Alex on 23/8/2016.
 */
public class ImageCropperActivity extends AppCompatActivity implements View.OnClickListener {

    //Constants
    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;
    public static final String IMAGE_PATH = "image_path";


    //UI
    CropImageView cropImageView;


    //DATA
    String imagePath;
    Bitmap originalImage = null;
    Bitmap croppedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        try {
            imagePath = getIntent().getStringExtra(IMAGE_PATH);
            originalImage = BitmapFactory.decodeFile(imagePath);
        } catch (Exception e) {
            originalImage = null;
        }

        if (originalImage == null) {
            Toast.makeText(this, "No image to crop!", Toast.LENGTH_SHORT).show();
            finish();
        }


        cropImageView = (CropImageView) findViewById(R.id.image_cropper_crop_image_view);
        cropImageView.setImageBitmap(originalImage);
        cropImageView.setGuidelines(2);

        ImageButton cropButton = (ImageButton) findViewById(R.id.image_cropper_crop_button);
        ImageButton rotateButton = (ImageButton) findViewById(R.id.image_cropper_rotate_button);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.image_cropper_cancel_button);

        cropButton.setOnClickListener(this);
        rotateButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        switch (i) {
            case R.id.image_cropper_crop_button:
                handleImageCrop();
                break;
            case R.id.image_cropper_rotate_button:
                cropImageView.rotateImage(90);
                break;
            case R.id.image_cropper_cancel_button:
                finish();
                break;
        }
    }

    private void handleImageCrop() {

        //Get image
        croppedImage = cropImageView.getCroppedImage();

        //If image is too large, scale it to decent size
        croppedImage = ImageUtils.scaleBitmap(croppedImage, 1920);

        //Compress it
        byte[] out = ImageUtils.toCompressedByteArray(croppedImage, IMAGE_COMPRESSION_PERCENTAGE);

        //Save it
        try {
            File image = new File(imagePath);
            FileOutputStream fos = new FileOutputStream(image);
            fos.write(out);
            fos.close();
        }catch (IOException e) {
            Toast.makeText(this, "There was a problem saving the cropped image!", Toast.LENGTH_SHORT).show();
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }



}
