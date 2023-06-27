package diplom_pack.client.client_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        //Получение авторизованного пользователя
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        Intent intent;
        if (firebaseUser != null) {
            intent = new Intent(SplashScreen.this, HomeActivity.class);
        }
        else {
            intent = new Intent(SplashScreen.this, Authorization.class);
        }
        startActivity(intent);
        finish();

    }

    }

