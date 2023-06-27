package diplom_pack.client.client_v1;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import diplom_pack.client.client_v1.Entities.Video;

public class LikesBlockActivity extends AppCompatActivity {

    AtomicInteger isLock = new AtomicInteger(0);

    private RecyclerView recyclerView;
    private LinearLayout homeLinearLikes;
    private AutoCompleteTextView autoCompleteTextView;

    private ArrayList<String> images;
    private ArrayList<Uri> imagesUri;

    private Map<String, String> likesMap;

    private ArrayList<String> datesOfLikes;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CollectionReference usersCollection;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_block);

        init();

        getLikes();

        buttonClicks();

    }

    public void init() {

        images = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewLikes);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        homeLinearLikes = findViewById(R.id.homeLinearLikes);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        usersCollection = db.collection("users");
        mAuth = FirebaseAuth.getInstance();

        likesMap = new HashMap<>();
        datesOfLikes = new ArrayList<>();
        imagesUri = new ArrayList<>();

    }

    public void buttonClicks() {

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                images = new ArrayList<>();

                String date = autoCompleteTextView.getText().toString();

                for (Map.Entry<String, String> entry : likesMap.entrySet()) {
                    if (date == entry.getKey()) {
                        images.add(entry.getValue());
                    }
                }

                getUrisOfImages();

            }
        });

        homeLinearLikes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Animation buttonAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.button_anim);

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.startAnimation(buttonAnimation);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(LikesBlockActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });



    }

    public void getUrisOfImages() {

        Log.d(TAG, "");

        for (String image: images) {

            StorageReference videoRef = storageRef.child("images/" + image);

            videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, "Загружен uri");
                    imagesUri.add(uri);
                    isLock.incrementAndGet();
                    setAdapterForRV();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Обработка ошибки
                    Log.d(TAG, "Не загружен uri");
                }
            });

        }


    }

    public void setAdapterForRV() {

        if (isLock.get()==2) {

            ImagesAdapter imagesAdapter = new ImagesAdapter(this, imagesUri);

            Log.d(TAG, "Создан img_адаптер");

            //Отвечает за измерение и позиционирование представлений элементов в RecyclerView
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            //Передаем адаптер в RecyclerView
            recyclerView.setAdapter(imagesAdapter);


        }

    }

    public void setAdapterForACTV() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_dropdown, datesOfLikes);
        autoCompleteTextView.setAdapter(arrayAdapter);
    }



    public void getLikes() {

        Query query = usersCollection.whereEqualTo("email", mAuth.getCurrentUser().getEmail()).limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>(){}.getType();

                        likesMap = gson.fromJson(gson.toJson(documentSnapshot.get("likes")), type);

                        if (likesMap != null) {


                            //Распределяем имена понравившихся видео и даты их отметки по соотвествующим листам
                            for (Map.Entry<String, String> entry : likesMap.entrySet()) {

                                String s = entry.getKey().substring(0, entry.getKey().length() - 3)+"jpg";

                                images.add(s);
                                datesOfLikes.add(entry.getValue());
                                Log.d(TAG, s + " " + entry.getValue());
                            }

                            getUrisOfImages();
                            setAdapterForACTV();


                        }
                        // обработка полученной map
                    } else {
                        // документ не найден
                    }
                } else {
                    // ошибка выполнения запроса
                }
            }
        });

    }

}