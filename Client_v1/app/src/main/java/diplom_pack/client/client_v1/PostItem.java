package diplom_pack.client.client_v1;

import android.net.Uri;
import android.widget.SeekBar;
import android.widget.VideoView;

public class PostItem {

    private Uri videoUri;

    public PostItem(Uri videoUri) {
        this.videoUri = videoUri;

    }

    public Uri getVideoUri() {
        return videoUri;
    }


    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public static void seekBarAttach(VideoView videoView, SeekBar seekBar) {

    }

}
