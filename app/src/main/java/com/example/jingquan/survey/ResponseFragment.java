package com.example.jingquan.survey;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

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
            final Database db = manager.getExistingDatabase("survey_responses5");
            Query q = db.createAllDocumentsQuery();
            QueryEnumerator qe = q.run();
            final ArrayList<Question> aq = new ArrayList<>();
            for (Iterator<QueryRow> iqr = qe; iqr.hasNext(); ) {
                QueryRow qr = iqr.next();
                Document doc = qr.getDocument();
                Map<String, Object> mso = doc.getProperties();
                TreeMap<String, Object> tmso = new TreeMap<>(mso);
                List<String> ls = new ArrayList<>(tmso.keySet());
                ObjectMapper om = new ObjectMapper();
                for (String k : ls) {
                    if (k.contains("Q")) {
                        JSONObject object = new JSONObject((LinkedHashMap) mso.get(k));
                        Question qn = om.readValue(object.toString(), Question.class);
                        aq.add(qn);
                    }
                }

                Collections.sort(aq, new Comparator<Question>() {
                    @Override
                    public int compare(Question o1, Question o2) {
                        return o1.getqNumber() - o2.getqNumber();
                    }
                });
            }
            final ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list_res);
            String code = "code";
            Set<String> temp = new LinkedHashSet<>();
            for (Question question : aq) {
                temp.add(question.getStatement());
            }
            ArrayList<String> questions = new ArrayList<>(temp);
            TreeMap<Integer, List<Question>> groupedList = new TreeMap<>();
            for (Question question : aq) {
                int qNo = question.getqNumber();
                if (groupedList.containsKey(qNo)) {
                    List<Question> lq = groupedList.get(qNo);
                    lq.add(question);
                } else {
                    List<Question> lq = new ArrayList<>();
                    lq.add(question);
                    groupedList.put(qNo, lq);
                }
            }

            List<List<Question>> llq = new ArrayList<>(groupedList.values());

            List<Map<String, String>> lmss = new ArrayList<>();
            List<List<Map<String, String>>> llmss = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                Map<String, String> tempMap = new TreeMap<>();
                lmss.add(tempMap);
                tempMap.put(code, questions.get(i));

                List<Map<String, String>> tempList = new ArrayList<>();
                for (int j = 0; j < llq.get(i).size(); j++) {
                    Map<String, String> tempMap1 = new TreeMap<>();
                    tempList.add(tempMap1);
                    tempMap1.put(code, llq.get(i).get(j).getResponse());
                }
                llmss.add(tempList);
            }
            String[] groupFrom = {code};
            int[] groupTo = {R.id.heading};
            String[] childFrom = {code};
            int[] childTo = {R.id.child_items};

            SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(getActivity(), lmss, R.layout.response_heading, groupFrom, groupTo, llmss, R.layout.response_content, childFrom, childTo);
            elv.setAdapter(sela);

            Button clear = (Button) v.findViewById(R.id.clear);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elv.setAdapter((BaseExpandableListAdapter) null);
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

            Button email = (Button) v.findViewById(R.id.button4);
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(Intent.ACTION_SEND);
                    in.setType("message/rfc822");
                    in.putExtra(Intent.EXTRA_EMAIL, new String[]{"appventure@nushigh.edu.sg"}); //TEMPORARY
                    in.putExtra(Intent.EXTRA_SUBJECT, "Appventure Feedback Responses");
                    String s = "";
                    for (int i = 0; i < aq.size(); i++) {
                        Question q = aq.get(i);
                        s += q.getqNumber() + " " + q.getStatement() + ": " + q.getResponse() + "\n";
                        if (i % 4 == 3) {
                            s += "\n";
                        }
                    }
                    in.putExtra(Intent.EXTRA_TEXT, s);
                    try {
                        startActivity(Intent.createChooser(in, "Send responses"));
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "No email clients available", Toast.LENGTH_LONG).show();
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
