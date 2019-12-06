package com.example.cwpar.mymp3playerproject2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Fragment1 extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private ArrayList<String> list = new ArrayList<>();
    private ImageView ivPlay, ivStop, ivFav, ivRewind, ivFoward;
    private TextView tvPlaying, tvTime;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private String selectedFile;
    private View view, favoriteView;
    private Thread thread;
    public static ArrayAdapter<String> adapter;
    private static final String MP3_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    private boolean isPlaying = false;
    private int pos;    // 재생 멈춘 시점 저장
    private EditText edtSinger, edtTitle;
    private Spinner spinner;
    private RatingBar ratingBar;
    public static MyDBHelper dbHelper;
    public static SQLiteDatabase db;
    private String janre;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment1, container, false);
        listView = view.findViewById(R.id.listView);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivStop = view.findViewById(R.id.ivStop);
        ivRewind = view.findViewById(R.id.ivRewind);
        ivFoward = view.findViewById(R.id.ivFoward);
        ivFav = view.findViewById(R.id.ivFav);
        tvPlaying = view.findViewById(R.id.tvPlaying);
        tvTime = view.findViewById(R.id.tvTime);
        seekBar = view.findViewById(R.id.seekBar);

        File[] mp3List = new File(MP3_PATH).listFiles();
        for (File file : mp3List) {
            String fileName = file.getName();
            if (fileName.length() >= 5) {
                String extendName = fileName.substring(fileName.length() - 3);
                if (extendName.equals("mp3") && !list.contains(fileName)) {
                    list.add(fileName);
                }
            }
        }   // end of for

        dbHelper = new MyDBHelper(getActivity().getApplicationContext());
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_single_choice, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.DKGRAY);
                return view;
            }
        };

        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        ivPlay.setOnClickListener(this);
        ivStop.setOnClickListener(this);
        ivFav.setOnClickListener(this);
        ivRewind.setOnClickListener(this);
        ivFoward.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedFile = list.get(position);
        this.position = position;
        playMusic(MP3_PATH + selectedFile);
        ivPlay.setImageResource(R.drawable.sharp_play_arrow_black_36dp);
    }

    public void playMusic(String path){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            toastDisplay("재생하실 파일을 선택해 주세요.");
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPlay:
                try{
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        ivPlay.setImageResource(R.drawable.sharp_pause_black_36dp);
                        tvPlaying.setText("Now Playing ...   " + selectedFile);
                        startUiThread();
                    } else if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        tvPlaying.setText("PAUSED");
                        ivPlay.setImageResource(R.drawable.sharp_play_arrow_black_36dp);

                    }
                } catch(Exception e) {
                    toastDisplay("재생하실 파일을 선택해 주세요.");
                    e.printStackTrace();
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextSong();
                    }
                });
                break;

            case R.id.ivStop:
                try {
                    mediaPlayer.stop();
                    playMusic(MP3_PATH + selectedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ivPlay.setEnabled(true);
                tvPlaying.setText("STOP");
                seekBar.setProgress(0);
                tvTime.setText("00:00");
                ivPlay.setImageResource(R.drawable.sharp_play_arrow_black_36dp);
                break;
            case R.id.ivFav:
                favoriteView = View.inflate(view.getContext(), R.layout.dialog, null);
                edtSinger = favoriteView.findViewById(R.id.edtSinger);
                edtTitle = favoriteView.findViewById(R.id.edtTitle);
                spinner = favoriteView.findViewById(R.id.spinner);
                ratingBar = favoriteView.findViewById(R.id.ratingBar);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            janre = "";
                        } else {
                            janre = parent.getItemAtPosition(position).toString();
//                            toastDisplay(janre);
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("좋아요");
                dialog.setView(favoriteView);
                dialog.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String singer = edtSinger.getText().toString();
                        String title = edtTitle.getText().toString();
                        int rate = (int) ratingBar.getRating();
                        db = dbHelper.getWritableDatabase();
                        if (edtSinger.getText().toString().equals("") || edtTitle.getText().toString().equals("")) {
                            toastDisplay("가수명과 곡명을 입력해 주세요");
                        } else {
                            db.execSQL("INSERT INTO MP3TBL VALUES ('" + singer + "','" + title + "','" + janre + "'," + rate + ");");
                        }
                        db.close();
                        toastDisplay("즐겨찾는 곡으로 저장되었습니다.");

                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toastDisplay("취소되었습니다.");
                    }
                });
                dialog.show();
                break;

            case R.id.ivRewind :
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    position-=1;
                    if(position<0){
                        position=list.size()-1;
                    }
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(MP3_PATH + list.get(position));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    selectedFile = list.get(position);
                    tvPlaying.setText("Now Playing ...   " + selectedFile);
                    startUiThread();
                }
                break;
            case R.id.ivFoward :
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    position+=1;
                    if(position>=list.size()){
                        position = 0;
                    }
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(MP3_PATH + list.get(position));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selectedFile = list.get(position);
                    tvPlaying.setText("Now Playing ...   " + selectedFile);
                    mediaPlayer.start();
                    startUiThread();
                }
                break;

        }
    }

    private void nextSong() {
        if (++position >= list.size()) {
            // 마지막 곡이 끝나면, 재생할 곡을 초기화.
            position = 0;
        } else {
            // 다음 곡을 재생.
            try {
                mediaPlayer.setDataSource(MP3_PATH + list.get(position));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startUiThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (mediaPlayer == null) {
                    return;
                }
                // 작업스레드 내에서 UI객체를 변경하기 위해
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setMax(mediaPlayer.getDuration());
                        tvTime.setText(simpleDateFormat.format(mediaPlayer.getDuration()));

                    }
                });

                while (mediaPlayer.isPlaying()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            tvTime.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                        }
                    }); // end of runOnUiThread
                    SystemClock.sleep(100);

                }// end of while
            }
        };
        thread.start();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mediaPlayer.stop();
//        mediaPlayer.release();
//    }

    private void toastDisplay(String s) {
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
