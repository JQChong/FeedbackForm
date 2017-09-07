package com.example.jingquan.survey;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResponseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Map<String, ArrayList<Question>> map = new LinkedHashMap<>();
    private Set<String> questionList = new LinkedHashSet<>();

    private OnFragmentInteractionListener mListener;

    public ResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResponseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResponseFragment newInstance(String param1, String param2) {
        ResponseFragment fragment = new ResponseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ResponseFragment newInstance() {
        return new ResponseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_response, container, false);
        try {
            Manager manager = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
            final Database db = manager.getExistingDatabase("survey_responses6");
            Query q = db.createAllDocumentsQuery();
            QueryEnumerator qe = q.run();
            for (Iterator<QueryRow> iqr = qe; iqr.hasNext(); ) {
                QueryRow qr = iqr.next();
                Document doc = qr.getDocument();
                ArrayList<Question> aq = new ArrayList<>();
                Map<String, Object> mso = doc.getProperties();
                TreeMap<String, Object> tmso = new TreeMap<>(mso);
                List<String> ls = new ArrayList<>(tmso.keySet());
                ObjectMapper om = new ObjectMapper();
                for (String k : ls) {
                    if (k.contains("Q")) {
                        JSONObject object = new JSONObject((LinkedHashMap) mso.get(k));
                        Question qn = om.readValue(object.toString(), Question.class);
                        aq.add(qn);
                        questionList.add(qn.getStatement());
                    }
                }

                Collections.sort(aq, new Comparator<Question>() {
                    @Override
                    public int compare(Question o1, Question o2) {
                        return o1.getqNumber() - o2.getqNumber();
                    }
                });

                map.put(doc.getId(), aq);
            }

            final TableLayout tl = (TableLayout) v.findViewById(R.id.response);
            TableRow tr = new TableRow(getActivity());
            Object[] questions = questionList.toArray();

            for (Object str : questions) {
                TextView header = new TextView(getActivity());
                header.setPadding(0, 10, 50, 10);
                header.setText(str.toString());
                header.setTextSize(16);
                header.setTypeface(null, Typeface.BOLD);
                tr.addView(header);
            }

            View headerBorder = new View(getActivity());
            headerBorder.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            headerBorder.setBackgroundColor(Color.rgb(51, 51, 51));

            tl.addView(tr);
            tl.addView(headerBorder);

            Object[] idList = map.keySet().toArray();
            for (int i = 0; i < map.size(); i++) {
                // this is the best workaround I can think of, otherwise the program will complain and throw Illegal State Exception :(
                View rowBorder = new View(getActivity());
                rowBorder.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                rowBorder.setBackgroundColor(Color.rgb(188, 189, 196));

                TableRow responseRow = new TableRow(getActivity());
                String key = idList[i].toString();
                ArrayList<Question> temp = map.get(key);
                System.out.println(temp.size());
                for (int j = 0; j < temp.size(); j++) {
                    TextView res = new TextView(getActivity());
                    res.setText(temp.get(j).getResponse());
                    res.setPadding(0, 10, 50, 10);
                    res.setTextSize(16);
                    responseRow.addView(res);
                }
                tl.addView(responseRow);
                tl.addView(rowBorder);
            }

            Button clear = (Button) v.findViewById(R.id.clear);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tl.removeAllViews();
                    try {
                        QueryEnumerator qe = db.createAllDocumentsQuery().run();
                        for (Iterator<QueryRow> iqr = qe; iqr.hasNext(); ) {
                            QueryRow qr = iqr.next();
                            Document doc = qr.getDocument();
                            doc.delete();
                        }
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
