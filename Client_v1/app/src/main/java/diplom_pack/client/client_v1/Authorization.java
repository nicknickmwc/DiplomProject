package diplom_pack.client.client_v1;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import diplom_pack.client.client_v1.Entities.User;

public class Authorization extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonAuth;
    private Button buttonToReg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        init();


        //При нажатии кнопки "Регистрация" осуществляется переход в Registration
        buttonToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authorization.this, Registration.class);
                startActivity(intent);

            }
        });


        //При нажатии кнопки "Вход" происходит попытка авторизации
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
            }
        };

        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                letsAuth(user.getEmail(), user.getPassword());
            }
        });

    }


    //Метод авторизации
    //////////////////////////////////////////////////////
    public void letsAuth(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Authorization.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Authorization.this, "Неправильный email или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Конец метода авторизации
    //////////////////////////////////////////////////////

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        buttonAuth = findViewById(R.id.buttonAuth);
        buttonToReg = findViewById(R.id.buttonToReg);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

    }

}