package com.example.jingquan.survey;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LSQViewFragment extends Fragment {

    public static ArrayList<String> lsqList = new ArrayList<>();
    public Manager manager;
    public Database db;
    public Document doc;
    private OnFragmentInteractionListener mListener;

    public LSQViewFragment() {
    }

    public static LSQViewFragment newInstance() {
        return new LSQViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lsq, container, false);
        lsqList.clear();
        try {
            manager = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
            db = manager.getExistingDatabase("questions_lists8");
            doc = db.getExistingDocument("1234567890");
            final Map<String, Object> questionMap = doc.getProperties();
            ArrayList<Question> aq = new ArrayList<>();
            for (String key : questionMap.keySet()) {
                if (key.contains("LSQ")) {
                    ObjectMapper om = new ObjectMapper();
                    JSONObject json = new JSONObject((LinkedHashMap) questionMap.get(key));
                    Question qn = om.readValue(json.toString(), Question.class);
                    aq.add(qn);
                }
            }
            Collections.sort(aq, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    return o1.getqNumber() - o2.getqNumber();
                }
            });
            for (Question q : aq) {
                lsqList.add(q.getStatement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListView lv = (ListView) v.findViewById(R.id.list_lsq);
        final Adapter a = new Adapter();
        lv.setAdapter(a);

        Button add = (Button) v.findViewById(R.id.add1);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                LayoutInflater li = getActivity().getLayoutInflater();
                View dialogView = li.inflate(R.layout.edit_dialog, null);
                adb.setView(dialogView);

                final EditText et = (EditText) dialogView.findViewById(R.id.edit1);
                final EditText number = (EditText) dialogView.findViewById(R.id.qNo);
                adb.setTitle("Add Question");
                adb.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog ad = adb.create();
                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (number.getText().toString().length() == 0) {
                                        number.setError("This field is required.");
                                        number.requestFocus();
                                        return;
                                    }
                                    final int i = Integer.parseInt(number.getText().toString());
                                    if (i > lsqList.size() + 1) {
                                        number.setError("Question number is invalid. Try again.");
                                        number.requestFocus();
                                    } else {
                                        et.setError(null);
                                        number.setError(null);
                                        doc.update(new Document.DocumentUpdater() {
                                            @Override
                                            public boolean update(UnsavedRevision newRevision) {
                                                Map<String, Object> temp = newRevision.getProperties();
                                                TreeMap<String, Object> lhmso = new TreeMap<>(temp);
                                                try {
                                                    ObjectMapper om = new ObjectMapper();
                                                    List<Object> lo = new ArrayList<>(lhmso.values());
                                                    List<String> ls = new ArrayList<>(lhmso.keySet());
                                                    for (int j = 0; j < temp.size(); j++) {
                                                        if (ls.get(j).contains("LSQ") || ls.get(j).contains("FRQ")) {
                                                            JSONObject json = new JSONObject((LinkedHashMap) lo.get(j));
                                                            Question qn = om.readValue(json.toString(), Question.class);
                                                            if (qn.getqNumber() > i) {
                                                                temp.put(ls.get(j), new Question(qn.getqNumber() + 1, qn.getqType(), qn.getStatement()));
                                                            }
                                                        }
                                                    }
                                                    temp.put("LSQ-" + System.currentTimeMillis(), new Question(i, Question.QUESTION_TYPE.LSQ, et.getText().toString()));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                newRevision.setUserProperties(temp);
                                                return true;
                                            }
                                        });
                                        lsqList.add(i - 1, et.getText().toString());
                                        a.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                ad.show();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private interface OnFragmentInteractionListener {
    }

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (lsqList != null && lsqList.size() != 0) {
                return lsqList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return lsqList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = getActivity().getLayoutInflater();
                v = li.inflate(R.layout.edit_listview, parent, false);
            }

            TextView tv = (TextView) v.findViewById(R.id.tv);
            ImageButton ib1 = (ImageButton) v.findViewById(R.id.ib1);
            ImageButton ib2 = (ImageButton) v.findViewById(R.id.ib2);

            tv.setText(lsqList.get(position));
            ib1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    LayoutInflater li = getActivity().getLayoutInflater();
                    View dialogView = li.inflate(R.layout.edit_dialog, null);
                    adb.setView(dialogView);

                    final EditText et = (EditText) dialogView.findViewById(R.id.edit1);
                    final EditText number = (EditText) dialogView.findViewById(R.id.qNo);

                    et.setText(lsqList.get(position));

                    number.setText(String.valueOf(position + 1));
                    number.setEnabled(false);

                    adb.setTitle("Edit Question");
                    adb.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                doc.update(new Document.DocumentUpdater() {
                                    @Override
                                    public boolean update(UnsavedRevision newRevision) {
                                        Map<String, Object> temp = newRevision.getProperties();
                                        TreeMap<String, Object> sorted = new TreeMap<>(temp);
                                        ObjectMapper om = new ObjectMapper();
                                        List<Object> lo = new ArrayList<>(sorted.values());
                                        List<String> ls = new ArrayList<>(sorted.keySet());
                                        String key = "";
                                        for (int i = 0; i < lo.size(); i++) {
                                            try {
                                                if (ls.get(i).contains("LSQ") || ls.get(i).contains("FRQ")) {
                                                    JSONObject object = new JSONObject((LinkedHashMap)lo.get(i));
                                                    Question qn = om.readValue(object.toString(), Question.class);
                                                    if (qn.getStatement().equals(lsqList.get(position))) {
                                                        key = ls.get(i);
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        temp.put(key, new Question(position + 1, Question.QUESTION_TYPE.LSQ, et.getText().toString()));
                                        newRevision.setUserProperties(temp);
                                        return true;
                                    }
                                });
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }
                            lsqList.set(position, et.getText().toString());
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    adb.show();
                }
            });
            ib2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        doc.update(new Document.DocumentUpdater() {
                            @Override
                            public boolean update(UnsavedRevision newRevision) {
                                Map<String, Object> temp = newRevision.getProperties();
                                TreeMap<String, Object> sorted = new TreeMap<>(temp);
                                ObjectMapper om = new ObjectMapper();
                                List<Object> lo = new ArrayList<>(sorted.values());
                                List<String> ls = new ArrayList<>(sorted.keySet());
                                String key = "";
                                ArrayList<Question> aq = new ArrayList<>();
                                for (int i = 0; i < lo.size(); i++) {
                                    try {
                                        if (ls.get(i).contains("LSQ") || ls.get(i).contains("FRQ")) {
                                            JSONObject object = new JSONObject((LinkedHashMap)lo.get(i));
                                            Question qn = om.readValue(object.toString(), Question.class);
                                            aq.add(qn);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                for (int i = 0; i < aq.size(); i++) {
                                    Question qn = aq.get(i);
                                    if (qn.getStatement().equals(lsqList.get(position))) {
                                        key = ls.get(i);
                                        break;
                                    }
                                }
                                temp.remove(key);
                                try {
                                    System.out.println(aq.size());
                                    for (int j = 0; j < aq.size(); j++) {
                                        Question qn = aq.get(j);
                                        System.out.println(qn.getqNumber());
                                        if (qn.getqNumber() > position + 1) {
                                            temp.put(ls.get(j), new Question(qn.getqNumber() - 1, qn.getqType(), qn.getStatement()));
                                        }
                                    }
                                    newRevision.setUserProperties(temp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        });
                        lsqList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return v;
        }
    }
}
