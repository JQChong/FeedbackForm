package com.example.jingquan.survey;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.R.attr.key;
import static com.example.jingquan.survey.LSQFragment.likertList;
import static com.example.jingquan.survey.LSQFragment.likertResponse;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FRQFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FRQFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FRQFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static ArrayList<String> frqList = new ArrayList<>();
    public static ArrayList<String> frqResponse = new ArrayList<>();
    private ArrayList<Question> all_frq = new ArrayList<>();
    private ArrayList<Question> all_questions = new ArrayList<>();
    private int currentIndex = 0;

    public FRQFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FRQFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FRQFragment newInstance(String param1, String param2) {
        FRQFragment fragment = new FRQFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment_frq, container, false);
        try {
            frqList.clear();
            Manager manager = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
            Database db = manager.getExistingDatabase("questions_lists6");
            Document doc = db.getExistingDocument("1234567890");
            final Map<String, Object> questionMap = doc.getProperties();
            ObjectMapper om = new ObjectMapper();
            for (String key : questionMap.keySet()) {
                if (key.contains("FRQ")) {
                    JSONObject json = new JSONObject((LinkedHashMap) questionMap.get(key));
                    Question qn = om.readValue(json.toString(), Question.class);
                    all_frq.add(qn);
                }
            }

            List<String> ls = new ArrayList<>(questionMap.keySet());
            List<Object> lo = new ArrayList<>(questionMap.values());

            for(int i=0; i<lo.size(); i++){
                if (ls.get(i).contains("LSQ") || ls.get(i).contains("FRQ")) {
                    JSONObject json = new JSONObject((LinkedHashMap) lo.get(i));
                    Question qn = om.readValue(json.toString(), Question.class);
                    all_questions.add(qn);
                }
            }

            Collections.sort(all_frq, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    return o1.getqNumber() - o2.getqNumber();
                }
            });

            Collections.sort(all_questions, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    return o1.getqNumber() - o2.getqNumber();
                }
            });

            for (Question q : all_frq) {
                frqList.add(q.getStatement());
            }

            final Button b = (Button) v.findViewById(R.id.button4);
            final TextSwitcher ts1 = (TextSwitcher) v.findViewById(R.id.ts3);
            final TextSwitcher ts2 = (TextSwitcher) v.findViewById(R.id.ts4);

            ts1.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    TextView tv = new TextView(getActivity());
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
                    return tv;
                }
            });
            ts2.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    TextView tv = new TextView(getActivity());
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    return tv;
                }
            });
            ts1.setCurrentText("Question " + (likertList.size() + 1) + "/" + (questionMap.size() - 2));
            ts2.setCurrentText(frqList.get(0));

            Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);

            ts1.setInAnimation(in);
            ts2.setInAnimation(in);
            ts1.setOutAnimation(out);
            ts2.setOutAnimation(out);

            final EditText et = (EditText) v.findViewById(R.id.editText2);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String res = et.getText().toString();
                    if (res.equalsIgnoreCase("")) {
                        et.setError("This is a required question");
                    } else {
                        currentIndex++;
                        frqResponse.add(et.getText().toString());
                        if (currentIndex == frqList.size() - 1) {
                            b.setText("Finish");
                        }
                        if (currentIndex == frqList.size()) {
                            Map<String, Object> allRes = new HashMap<>();
                            ArrayList<String> all_Res = new ArrayList<>();
                            all_Res.addAll(likertResponse);
                            all_Res.addAll(frqResponse);
                            for (int i = 0; i < all_Res.size(); i++) {
                                Question qn = all_questions.get(i);
                                allRes.put("Q-" + (i + 1), new Question(qn.getqNumber(), qn.getqType(), qn.getStatement(), all_Res.get(i)));
                            }
                            try {
                                Manager manager1 = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
                                Database db = manager1.getExistingDatabase("survey_responses4");
                                Document doc = db.getDocument("res-" + System.currentTimeMillis());
                                doc.putProperties(allRes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            EndFragment ef = new EndFragment();
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.setCustomAnimations(R.animator.enter_right, R.animator.exit_left);
                            ft.replace(R.id.main, ef);
                            ft.commit();
                        } else {
                            ts1.setText("Question " + (currentIndex + likertList.size() + 1) + "/" + (questionMap.size() - 2));
                            ts2.setText(frqList.get(currentIndex));
                        }
                        et.setText("");
                    }
                }
            });
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
