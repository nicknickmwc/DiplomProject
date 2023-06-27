package diplom_pack.client.client_v1;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.Date;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import diplom_pack.client.client_v1.Entities.Video;

public class VideoRecordActivity extends AppCompatActivity {

    private static int CAMERA_PERMISSION_CODE = 100;
    private static int VIDEO_RECORD_CODE = 101;
    private boolean UriExist = false;

    private Video video;

    private String videoFileName;

    FirebaseStorage storage;
    StorageReference storageRef;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        init();

        getCameraPermission();
        recordingVideo();

    }

    public boolean isHavingCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void recordingVideo() {
        if (isHavingCamera()) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, VIDEO_RECORD_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                video.setUri(data.getData());

                String uuid = UUID.randomUUID().toString();
                videoFileName = "video_" + uuid + ".mp4";

                StorageReference videoRef = storageRef.child("videos/"+videoFileName);

                //Отправление видео в хранилище и сохранение ссылки в БД
                //////////////////////////////////////////////////////

                videoRef.putFile(video.getUri());

                pushMetaVideoToDB(videoFileName);

                Intent intent = new Intent(VideoRecordActivity.this, HomeActivity.class);
                intent.putExtra("fileName", videoFileName);
                startActivity(intent);
                finish();


                //Конец отправления видео в хранилище и сохранения ссылки в БД
                //////////////////////////////////////////////////////

            }
        }

    }


    //Метод отправления меттаданных видео в БД
    //////////////////////////////////////////

    public void pushMetaVideoToDB(String file_name) {


        //Обновляем количество видео
        ////////////////////////////

        DocumentReference count_of_videos_Ref = db.collection("count_of_videos").document("doc1");

        count_of_videos_Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {

                        int countOfVideos = Integer.parseInt(document.getString("count"));

                        countOfVideos += 1;

                        video.setVideoNumber(countOfVideos);

                        Map<String, Object> newCount = new HashMap<>();
                        newCount.put("count", String.valueOf(countOfVideos));

                        count_of_videos_Ref.update(newCount);

                        DocumentReference videoRef = db.collection("videos").document();

                        Map<String, Object> data = new HashMap<>();

                        //Текущая дата
                        LocalDate currentDate = LocalDate.now();
                        String formattedDate = currentDate.toString();


                        data.put("file_name", file_name);
                        data.put("video_number", String.valueOf(countOfVideos));
                        data.put("likes_count", "0");
                        data.put("date_of_create", formattedDate);

                        videoRef.set(data);

                    } else {
                        System.out.println("Document not found");
                    }
                } else {
                    System.out.println("Error getting document: " + task.getException());
                }
            }
        });
        //Конец обновления количества видео
        ///////////////////////////////////

    }
    //Конец метода отправления меттаданных видео в БД
    //////////////////////////////////////////

    public void init() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        video = new Video();
    }

    public boolean getUriExist() {
        return this.UriExist;
    }


}