package diplom_pack.client.client_v1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import diplom_pack.client.client_v1.Entities.Video;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    //Элемент представления, формируемое из layout-файла
    private LayoutInflater inflater;
    //Список изображений
    private ArrayList<Uri> imagesUri;

    public ImagesAdapter(Context context, ArrayList<Uri> imagesUri) {

        this.inflater = LayoutInflater.from(context);
        this.imagesUri = imagesUri;
    }

    @Override

    //Возвращает объект ViewHolder, который будет хранить данные по одному объекту PostItem
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Создаем представление из layout-содержимого
        View view = inflater.inflate(R.layout.activity_image, parent, false);

        return new ViewHolder(view);
    }

    //Выполняет привязку объекта ViewHolder к объекту PostItem по определенной позиции
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        //Достаем Image из листа по индексу



        Uri image = imagesUri.get(position);

        //holder.imageView.setImageURI(image);

        holder.setImage(image);


    }

    //Возвращает количество объектов в списке
    @Override
    public int getItemCount() {
        return imagesUri.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        View view;

        public ViewHolder(View view) {

            super(view);
            imageView = view.findViewById(R.id.imageViewLike);
            this.view = view;



        }

        public void setImage(Uri uri) {

            Glide.with(view).load(uri).into(imageView);

        }

    }


}
