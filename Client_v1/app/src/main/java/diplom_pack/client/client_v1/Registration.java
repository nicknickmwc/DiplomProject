package diplom_pack.client.client_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import diplom_pack.client.client_v1.Entities.User;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db;
    private User user;

    private EditText editTextEmailReg;
    private EditText editTextPasswordReg;
    private EditText editTextUserName;
    private EditText editTextDate;

    private Button buttonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();


        //При нажатии на editTextDate открывается календарь для выбора даты
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получение текущей даты
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Установка выбранной даты в EditText
                                editTextDate.setText(dayOfMonth + "." + (month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);

                // Показ DatePickerDialog
                datePickerDialog.show();

            }
        });

        //При нажатии кнопки "Сохранить" создается User и вызывается letsReg
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(editTextEmailReg.getText().toString(), editTextPasswordReg.getText().toString(),
                        editTextUserName.getText().toString(), editTextDate.getText().toString());
                letsReg(user.getEmail(), user.getPassword());
                db.collection("users").add(userData(user));
            }
        });


    }

    //Начало метода регистрации letsReg
    //Осуществляется регистрация по эл. почте и паролю, вызывается метод setDataForUser
    public void letsReg(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUserName(editTextUserName.getText().toString());
                    user.setDateOfBorn(editTextDate.getText().toString());
                    setDataForUser(userData(user));
                }
                else {
                    Toast.makeText(Registration.this, "Неверный формат данных", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Конец метода регистрации letsReg
    ///////////////////////////////////////////////////////////////////////////////////

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmailReg = findViewById(R.id.editTextEmailReg);
        editTextPasswordReg = findViewById(R.id.editTextPasswordReg);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextDate = findViewById(R.id.editTextDate);

        buttonReg = findViewById(R.id.buttonReg);
    }

    //Метод userData создания наборов Map из User
    public Map<String, Object> userData (User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("user_name", user.getUserName());
        data.put("date_of_born", user.getDateOfBorn());
        data.put("likes", null);
        data.put("selfVideos", null);
        return data;
    }

    //Начало метода setDataForUser для отправки дполнительных данных в Firestore и перехода к следующей активности
    //////////////////////////////////////////////////////////////////////////////////////////////
    public void setDataForUser(Map<String, Object> userMap) {
        db.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(Registration.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(Registration.this, "Неверный формат данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //Конец метода setDataForUser
    /////////////////////////////

}