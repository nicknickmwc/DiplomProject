package diplom_pack.client.client_v1;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import diplom_pack.client.client_v1.Entities.Video;

public class HomeActivity extends AppCompatActivity {


    //Переменная для разграничения потоков
    AtomicInteger isLock = new AtomicInteger(0);

    private RecyclerView recyclerView;
    private TextView textView;

    private ArrayList<Video> videos;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    private LinearLayout shootLinear;
    private LinearLayout homeLinear;
    private LinearLayout profileLinear;

    public String videoFileName;
    public List<String> exampleVideosName;
    public Map<String,String> videoMeta;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {

            videoFileName = arguments.getString("fileName");

        }


        textView = findViewById(R.id.homeTextView);

        getVideos(3);

        bottomBarButtonsClick();



    }

    //Метод установки слушателей для кнопок нижней панели bottomBarButtonsClick
    /////////////////////////////////////////////////////
    public void bottomBarButtonsClick() {
        shootLinear.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    Animation buttonAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.button_anim);

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        view.startAnimation(buttonAnimation);
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                        Intent intent = new Intent(HomeActivity.this, VideoRecordActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                }
            });

        homeLinear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Animation buttonAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.button_anim);

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.startAnimation(buttonAnimation);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        profileLinear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Animation buttonAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.button_anim);

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.startAnimation(buttonAnimation);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(HomeActivity.this, LikesBlockActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });



    }
    //Конец метода bottomBarButtonsClick
    ////////////////////////////////////

    public void init() {

        recyclerView = findViewById(R.id.recyclerView);
        shootLinear = findViewById(R.id.shootLinear);
        homeLinear = findViewById(R.id.homeLinear);
        profileLinear = findViewById(R.id.profileLinear);

        videos = new ArrayList<>();
        exampleVideosName = new ArrayList<>();
        videoMeta = new HashMap<>();

        //videos.add(new Video(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.husky)));
        //videos.add(new Video(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.panda)));

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


    }


    //Метод selectedNumbersOfVideos для генерации уникальных чисел
    //с количеством и верхней границей extreme////////////////////
    public ArrayList<String> selectedNumbersOfVideos(int extreme) {

        HashSet<String> numbers = new HashSet<String>();
        Random random = new Random();
        while (numbers.size() < extreme) {
            int randomNumber = random.nextInt(extreme);
            String randomString = Integer.toString(randomNumber);
            numbers.add(randomString);
        }

        ArrayList<String> numbersList = new ArrayList<>(numbers);

        return numbersList;

    }
    //Конец метода selectedNumbersOfVideos
    //////////////////////////////////////

    //Метод getVideos для получения видеоданных по их номеру
    ////////////////////////////////////////////////////////
    public void getVideos(int countOfVideos) {

        CollectionReference collectionRef = db.collection("videos");

        Query query = collectionRef.whereIn("video_number",selectedNumbersOfVideos(countOfVideos));

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        Video video;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            video = new Video();
                            video.setName(document.getString("file_name"));
                            video.setCountOfLikes(document.getString("likes_count"));
                            video.setDateOfCreate(document.getString("date_of_create"));
                            video.setAuthorName(document.getString("author_name"));
                            videos.add(video);
                            Log.d(TAG, "Загружены метаданные");
                            Log.d(TAG, video.getName());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    getUrisOfVideos();

                }

            });

    }
    //Конец метода getVideos
    ////////////////////////


    //Метод getUrisOfVideos для получения Uri набора видео
    //////////////////////////////////////////////////////
    public void getUrisOfVideos() {

        Log.d(TAG, "getUrisOfVideos начал работу");

        for (Video video: videos) {

            StorageReference videoRef = storageRef.child("videos/" + video.getName());



            videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    video.setUri(uri);
                    Log.d(TAG, "Загружен uri");
                    isLock.incrementAndGet();
                    setAdapter();
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
    //Конец метода getUrisOfVideos
    //////////////////////////////

    //Метод setAdapter для настройки RecyclerView, начало которой не происходит раньше загрузки данных
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void setAdapter() {

        if (isLock.get()==2) {
            PostAdapter postAdapter = new PostAdapter(this, videos);

            Log.d(TAG, "Создан постадаптер");

            //Отвечает за измерение и позиционирование представлений элементов в RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //Передаем адаптер в RecyclerView
            recyclerView.setAdapter(postAdapter);

            //Создает режим одиночного свайпа
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);


        }

    }
    //Конец метода setAdapter
    /////////////////////////



}