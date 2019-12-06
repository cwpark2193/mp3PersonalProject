package com.example.cwpar.mymp3playerproject2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.cwpar.mymp3playerproject2.Fragment1.db;
import static com.example.cwpar.mymp3playerproject2.Fragment1.dbHelper;

public class Fragment2 extends Fragment implements View.OnClickListener {
    private View view, favoriteView;
    private ArrayList<MyDataDAO> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button btnUpdate, btnDelete, btnOrder, btnSelect;
    private EditText edtSinger, edtTitle;
    private RatingBar ratingBar;
    private Spinner spinner;
    private LinearLayoutManager layoutManager;
    private MyDataAdapter adapter;
    private String janre;
    private boolean btnSwitch = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment2, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnOrder = view.findViewById(R.id.btnOrder);
        btnSelect = view.findViewById(R.id.btnSelect);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyDataAdapter(R.layout.view_holder, list);
        recyclerView.setAdapter(adapter);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnSelect.setOnClickListener(this);

        btnSelect.callOnClick();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelect:
                db = dbHelper.getReadableDatabase();
                Cursor cursor;
                cursor = db.rawQuery("SELECT * FROM MP3TBL;", null);
                list.clear();
                while (cursor.moveToNext()) {
                    list.add(new MyDataDAO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3)));
                }
                cursor.close();
                db.close();
                adapter.notifyDataSetChanged();
                break;

            case R.id.btnUpdate:
                favoriteView = View.inflate(view.getContext(), R.layout.dialog, null);
                edtSinger = favoriteView.findViewById(R.id.edtSinger);
                edtTitle = favoriteView.findViewById(R.id.edtTitle);
                spinner = favoriteView.findViewById(R.id.spinner);
                ratingBar = favoriteView.findViewById(R.id.ratingBar);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
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
                edtSinger.setText(list.get(adapter.getLastCheckedPosition()).getSinger());
                edtTitle.setText(list.get(adapter.getLastCheckedPosition()).getTitle());
//                spinner.setPrompt(list.get(adapter.getLastCheckedPosition()).getJanre());
                ratingBar.setRating(list.get(adapter.getLastCheckedPosition()).getRate());
                dialog.setView(favoriteView);
                dialog.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String singer = edtSinger.getText().toString();
                        String title = edtTitle.getText().toString();
                        int rate = (int) ratingBar.getRating();
                        db = dbHelper.getWritableDatabase();
                        if (edtSinger.getText().toString().equals("") || edtTitle.getText().toString().equals("")) {
                            toastDisplay("가수명과 곡명을 입력해 주세요");
                        } else {
                            Log.e("Fragment2", "insert: "+String.valueOf(adapter.getLastCheckedPosition()));
                            db.execSQL("UPDATE MP3TBL SET singer = '" + singer + "', title = '" + title + "', janre = '" + janre + "', rate = " + rate + " WHERE singer = '"+list.get(adapter.getLastCheckedPosition()).getSinger()+"' AND title = '"+list.get(adapter.getLastCheckedPosition()).getTitle()+"';");
                        }
                        db.close();
                        toastDisplay("정보가 수정되었습니다.");
                        btnSelect.callOnClick();

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

            case R.id.btnDelete:
                db = dbHelper.getReadableDatabase();
                Log.e("Fragment2", "delete: "+String.valueOf(adapter.getLastCheckedPosition()));
                db.execSQL("DELETE FROM MP3TBL WHERE singer = '"+list.get(adapter.getLastCheckedPosition()).getSinger()+"' AND title = '"+list.get(adapter.getLastCheckedPosition()).getTitle()+"';");
                db.close();
                list.remove(adapter.getLastCheckedPosition());
                adapter.notifyItemRemoved(adapter.getLastCheckedPosition());
                toastDisplay("선택된 목록이 삭제되었습니다.");
                break;

            case R.id.btnOrder:
                db = dbHelper.getReadableDatabase();
                Cursor cursor1;
                list.clear();
                if (!btnSwitch) {
                    cursor1 = db.rawQuery("SELECT * FROM MP3TBL ORDER BY singer DESC;", null);
                    btnSwitch = true;
                } else {
                    cursor1 = db.rawQuery("SELECT * FROM MP3TBL ORDER BY singer ASC;", null);
                    btnSwitch = false;
                }

                while (cursor1.moveToNext()) {

                    list.add(new MyDataDAO(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getInt(3)));
                }

                cursor1.close();
                db.close();
                adapter.notifyDataSetChanged();
                break;


        } // end of switch
    } // end of onClick

    private void toastDisplay(String s) {
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
