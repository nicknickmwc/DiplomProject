package diplom_pack.client.client_v1;


import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diplom_pack.client.client_v1.Entities.User;
import diplom_pack.client.client_v1.Entities.Video;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    //Элемент представления, формируемое из layout-файла
    private LayoutInflater inflater;
    //Список видео
    private ArrayList<Video> videos;

    private String filename;


    public PostAdapter(Context context, ArrayList<Video> videos) {

        this.inflater = LayoutInflater.from(context);
        this.videos = videos;
    }


    //Переопределяем основные методы RecyclerView-адаптера
    @Override

    //Возвращает объект ViewHolder, который будет хранить данные по одному объекту PostItem
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Создаем представление из layout-содержимого
        View view = inflater.inflate(R.layout.activity_page, parent, false);

        return new ViewHolder(view);
    }

    //Выполняет привязку объекта ViewHolder к объекту PostItem по определенной позиции
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Показываем основной Linear
        //holder.setOnGeneralLinear();


        //Достаем Video из листа по индексу
        Video video = videos.get(position);

        holder.currentVideo = video;

        //Устанавливаем Uri для VideoView
        holder.videoData(video.getUri(), filename);

        holder.seekBarData();

        holder.rightElementsListener();


        //holder.hideGeneralLinear();

        //holder.generalLinear.setVisibility(View.INVISIBLE);

    }

    //Возвращает количество объектов в списке
    @Override
    public int getItemCount() {
        return videos.size();
    }

    //Наследуем Thread и переопределяем run() для нашего потока
    public static class ProgressBarThread extends Thread{

        public SeekBar seekBar;
        public VideoView videoView;

        public ProgressBarThread() {

        }

        public ProgressBarThread(SeekBar seekBar, VideoView videoView) {
            this.seekBar = seekBar;
            this.videoView = videoView;
        }

        @Override
        public void run() {

            int progress=1;

                    while (videoView.getCurrentPosition()<videoView.getDuration()) {

                        //progress = Math.round(videoView.getCurrentPosition()+1 / (videoView.getDuration() / 100));
                        if ((videoView.getCurrentPosition() >= ((int)(videoView.getDuration() / seekBar.getMax())*progress)) &
                                (progress<=99) ) {
                            seekBar.setProgress(progress, true);
                            progress += 1;
                        }
                    }

                    this.interrupt();

        }

        public void setSeekBar(SeekBar seekBar) {
            this.seekBar = seekBar;
        }

        public void setVideoView(VideoView videoView) {
            this.videoView = videoView;
        }
    }

    //Класс, содержащий в себе данные и идентификацию эелментов layout-файла
    ////////////////////////////////////////////////////////////////////////
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public boolean likeExist = false;

        public VideoView videoView;
        public SeekBar seekBar;

        public LinearLayout generalLinear;
        public ImageView likePostImg;

        public TextView usernameTextView;
        public TextView likesCountTextView;

        public Map<String, String> likesMap;
        public ArrayList<String> likesList;
        public ArrayList<String> likesDateList;

        public Video currentVideo;

        FirebaseFirestore db;
        CollectionReference usersCollection;
        CollectionReference videosCollection;
        FirebaseAuth mAuth;

        User user;

        public int videoDuration;

        //Конструктор ViewHolder
        ////////////////////////
        public ViewHolder(View view) {
            super(view);
            videoView = view.findViewById(R.id.videoView);
            seekBar = view.findViewById(R.id.seekBar);
            generalLinear = view.findViewById(R.id.generalLinear);
            likePostImg = view.findViewById(R.id.likePostImg);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            likesCountTextView = view.findViewById(R.id.likesCountTextView);

            likesMap = new HashMap<>();
            likesList = new ArrayList<>();
            likesDateList = new ArrayList<>();

            currentVideo = new Video();

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            usersCollection = db.collection("users");
            videosCollection = db.collection("videos");
            user = new User(mAuth.getCurrentUser().getEmail());


        }
        //Конец конструктора ViewHolder
        ///////////////////////////////

        //Слушатели кнопок главного экрана
        //////////////////////////////////
        public void rightElementsListener() {


            likePostImg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    Animation buttonAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.button_anim);

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        view.startAnimation(buttonAnimation);
                    }

                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (!likeExist) {
                            likePostImg.setImageResource(R.drawable.liked_post_linear_img);
                            addLike();
                            addLikesCountForVideo();
                        }
                    }

                    return true;
                }
            });

        }
        //Конец метода bottomAndRightElementsListener
        //////////////////////////////////

        //Взаимодействия c seekBar
        //////////////////////////
        public void seekBarData() {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int time;
                    time = (int)(videoView.getDuration()/seekBar.getMax()*seekBar.getProgress());
                    videoView.seekTo(time);
                }
            });
        }

        //конец seekBarData метода
        ///////////////////////////

        //Скрываем элементы главного экрана при удержании пальца
        ////////////////////////////////////////////////////////
        public void hideGeneralLinear() {
            generalLinear.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        generalLinear.setVisibility(View.INVISIBLE);
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        generalLinear.setVisibility(View.VISIBLE);
                    }

                    return false;

                }

            });
        }
        //конец hideGeneralLinear метода
        /////////////////////////////////

        public void setOnGeneralLinear() {
            generalLinear.setVisibility(View.VISIBLE);
        }

        //Взаимодействия с videoView
        ////////////////////////////
        public void videoData(Uri videoUri, String s) {

            videoView.setVideoURI(videoUri);

            likesCountTextView.setText(currentVideo.getCountOfLikes());

            usernameTextView.setText(currentVideo.getAuthorName());

            getLikes();

            //Воспроизведение при готовности видео
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    videoView.start();
                    //videoView.requestFocus();
                    videoView.setScaleY(1.3f);
                    videoView.setScaleX(1.1f);

                    //ProgressBarThread progressBarThread = new ProgressBarThread(seekBar, videoView);
                    //progressBarThread.start();
                }
            });

            //Воспроизведение при окончании видео
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seekBar.setProgress(0);
                    videoView.start();
                    videoView.setScaleY(1.3f);
                    videoView.setScaleX(1.1f);

                    //ProgressBarThread progressBarThread = new ProgressBarThread(seekBar, videoView);
                    //progressBarThread.start();
                }
            });


            //Остановка видео и продолжение при нажатии

            generalLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    }
                    else {
                        videoView.start();
                    }
                }
            });


        }
        //конец VideoData метода
        //////////////////////


        //Метод getLikes для получения HashMap лайков и их отметки и распределения по соотвествующим листам
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public void getLikes() {

            Query query = usersCollection.whereEqualTo("email", user.getEmail()).limit(1);

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
                                    setLikeStatus(entry.getKey());
                                    likesList.add(entry.getKey());
                                    likesDateList.add(entry.getValue());
                                    Log.d(TAG, entry.getKey() + " " + entry.getValue());
                                }

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
        //Конец метода getLikes
        ///////////////////////

        //Метод setLikeStatus для установления соответствующей картинки лайка при его наличии в списке
        //////////////////////////////////////////////////////////////////////////////////////////////
        public void setLikeStatus(String name) {
            if (currentVideo.getName()!=null && name!=null) {
                if (name == currentVideo.getName()) {
                    likePostImg.setImageResource(R.drawable.liked_post_linear_img);
                    likeExist = true;
                }
            }
        }
        //Конец Метода setLikeStatus
        ////////////////////////////

        public void addLike() {

            LocalDate currentDate = LocalDate.now();
            String formattedDate = currentDate.toString();

            likesMap.put(currentVideo.getName(), formattedDate);

            Query query = usersCollection.whereEqualTo("email", user.getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            document.getReference().update("likes", likesMap);

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        }

        public void addLikesCountForVideo() {

            Query query = videosCollection.whereEqualTo("file_name", currentVideo.getName());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            int likes_count = Integer.parseInt(currentVideo.getCountOfLikes()) + 1;

                            document.getReference().update("likes_count", String.valueOf(likes_count));

                            likesCountTextView.setText( String.valueOf(likes_count));

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        }



    }
    // конец ViewHolder класса
    ////////////////////////////////////////////////////////////////


}
