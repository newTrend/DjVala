package com.example.parminder.djvala;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Music extends ActionBarActivity {

    mainHappening adapterObj;
    ListView l;
    String TAG = "check";
    Toolbar toolbar;
    ArrayList listName = new ArrayList();
    ArrayList listId = new ArrayList();
    ArrayList listArtist = new ArrayList();
    ArrayList listNote = new ArrayList();
    ProgressDialog pDialog;
    JsonObjectRequest jsonObjReq;
    String tag_json_obj = "json_obj_req";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        if (savedInstanceState == null) {

            String url = "http://10.10.20.169:82/newTrend/djVala/getMusicList.php";
             jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            l = (ListView) findViewById(R.id.listView);
                            try {
                                JSONArray dataSet = (JSONArray) response.get("data");
                                if (dataSet != null) {
                                    int len = dataSet.length();
                                    for (int i = 0; i < len; i++) {
                                        JSONObject json = dataSet.getJSONObject(i);
                                        listName.add(json.get("name"));
                                        listId.add(json.get("id"));
                                        listArtist.add(json.get("artist"));
                                        listNote.add(json.get("note"));

                                    }
                                }
                                adapterObj = new mainHappening(getApplicationContext(), listName,listId,listArtist,listNote);
                                l.setAdapter(adapterObj);
                                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        final String b = (String) listId.get(position);
                                        final String url2 = "http://10.10.20.169:82/newTrend/djVala/deleteSong.php?id=" + b;
                                        new AlertDialog.Builder(Music.this)
                                                .setTitle("Title")
                                                .setMessage("Do you really want to whatever?")
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {

                                                        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                                                                url2, null,
                                                                new Response.Listener<JSONObject>() {

                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        Intent intent = getIntent();
                                                                        finish();
                                                                        startActivity(intent);
                                                                    }
                                                                }, new Response.ErrorListener() {

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // hide the progress dialog
                                                            }
                                                        });

// Adding request to request queue
                                                        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null).show();
                                    }
                                });

//                          sl
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error);
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        } else {
            l = (ListView) findViewById(R.id.listView);
            adapterObj = (mainHappening) savedInstanceState.getParcelable("myData");
            l.setAdapter(adapterObj);
        }


    }


}


class MyViewHolder {
    TextView idText;
    TextView nameText;
    TextView artistText;
    TextView noteText;

    MyViewHolder(View row) {
        nameText = (TextView) row.findViewById(R.id.nameText);
        idText = (TextView) row.findViewById(R.id.hidden);
        artistText = (TextView) row.findViewById(R.id.artistText);
        noteText = (TextView) row.findViewById(R.id.noteText);
    }
}

class mainHappening extends ArrayAdapter implements Parcelable {
    protected static final String TAG = "ERROR";
    Context context;
    String[] nameList;
    String[] idList;
    String[] artistList;
    String[] noteList;
    TextView loading;

    public mainHappening(Context c, ArrayList names, ArrayList id, ArrayList artist, ArrayList note) {
        super(c, R.layout.single_row, R.id.textView, names);
        this.context = c;
        this.nameList = (String[]) names.toArray(new String[names.size()]);
        this.idList = (String[]) id.toArray(new String[id.size()]);
        this.artistList = (String[]) artist.toArray(new String[artist.size()]);
        this.noteList = (String[]) note.toArray(new String[note.size()]);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final MyViewHolder holder;
        if (row == null) {
            //only run if row is created for first time
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (MyViewHolder) row.getTag();
        }
//        TextView nameText = (TextView) row.findViewById(R.id.nameText);


            holder.nameText.setText(nameList[position]);
            holder.idText.setText(idList[position]);
            holder.artistText.setText(artistList[position]);
            holder.noteText.setText(noteList[position]);



        //image setting finished
        return row;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

}
