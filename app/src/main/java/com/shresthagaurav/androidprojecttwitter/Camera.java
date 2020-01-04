package com.shresthagaurav.androidprojecttwitter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.shresthagaurav.androidprojecttwitter.api.ApiClass;
import com.shresthagaurav.androidprojecttwitter.model.ImageModel;
import com.shresthagaurav.androidprojecttwitter.model.SignUpResponse;
import com.shresthagaurav.androidprojecttwitter.model.User;
import com.shresthagaurav.androidprojecttwitter.strictMode.StrictModeClass;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class Camera extends AppCompatActivity {
    ImageView iv_profile;
    Button btn_login;
    String password, email, username, imageName;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_camera );
        iv_profile = findViewById( R.id.profile );
        btn_login = findViewById( R.id.btn_c_login );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString( "email" );
            username = bundle.getString( "username" );
            password = bundle.getString( "password" );

        }
        iv_profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
            }
        } );
        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageOnly();
                signUp();
            }
        } );
    }

    private void BrowseImage() {
        Intent intent = new Intent( Intent.ACTION_PICK );
        intent.setType( "image/*" );
        startActivityForResult( intent, 0 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText( this, "Please select an image ", Toast.LENGTH_SHORT ).show();
            }
        }
        Uri uri = data.getData();
        iv_profile.setImageURI( uri );
        imagePath = getRealPathFromUri( uri );
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader( getApplicationContext(),
                uri, projection, null, null, null );
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
        cursor.moveToFirst();
        String result = cursor.getString( colIndex );
        cursor.close();
        return result;
    }

    private void saveImageOnly() {
        File file = new File( imagePath );
        RequestBody requestBody = RequestBody.create( MediaType.parse( "multipart/form-data" ), file );
        MultipartBody.Part body = MultipartBody.Part.createFormData( "imageFile",
                file.getName(), requestBody );

        ApiClass usersAPI = new ApiClass();

        Call<ImageModel> responseBodyCall = usersAPI.calls().uploadImage( body );

        StrictModeClass.StrictMode();
        //Synchronous methid
        try {
            Response<ImageModel> imageResponseResponse = responseBodyCall.execute();
            imageName = imageResponseResponse.body().getFilename();
            Toast.makeText( this, "Image inserted" + imageName, Toast.LENGTH_SHORT ).show();

        } catch (IOException e) {
            Toast.makeText( this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }

    private void signUp() {
        User users = new User( email, password, username, imageName );

        ApiClass usersAPI = new ApiClass();
        Call<SignUpResponse> signUpCall = usersAPI.calls().register( users );

        signUpCall.enqueue( new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText( Camera.this, "Code " + response.code(), Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( Camera.this, "Registered", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText( Camera.this, "Error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );

    }
}
