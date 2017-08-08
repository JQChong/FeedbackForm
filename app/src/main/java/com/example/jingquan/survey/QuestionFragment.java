package com.example.jingquan.survey;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

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
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ArrayList<String> questionsList = new ArrayList<>();
    public static ArrayList<String> response = new ArrayList<>();
    public static ArrayList<Question> aq;

    public ArrayList<RatingBar> rb = new ArrayList<>();
    public ArrayList<EditText> et = new ArrayList<>();

    int ratecount = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
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
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        setHasOptionsMenu(true);
        try {
            questionsList.clear();
            Manager manager = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
            Database db = manager.getExistingDatabase("questions_lists7");
            Document doc = db.getExistingDocument("1234567890");
            final Map<String, Object> questionMap = doc.getProperties();
            aq = new ArrayList<>();
            for (String key : questionMap.keySet()) {
                if (key.contains("Q")) {
                    ObjectMapper om = new ObjectMapper();
                    JSONObject json = new JSONObject((LinkedHashMap) questionMap.get(key));
                    Question qn = om.readValue(json.toString(), Question.class);
                    aq.add(qn);
                }
                if (key.contains("LSQ")) {
                    ratecount++;
                }
            }
            Collections.sort(aq, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    return o1.getqNumber() - o2.getqNumber();
                }
            });
            for (Question q : aq) {
                questionsList.add(q.getStatement());
            }
            RecyclerView rv = (RecyclerView) v.findViewById(R.id.my_recycler_view);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
            rv.setAdapter(new QuestionAdapter());
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater mi) {
        mi.inflate(R.menu.menubar, menu);
        super.onCreateOptionsMenu(menu, mi);
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.submit:
                ArrayList<String> res = new ArrayList<>();
                Map<String, Object> mapRes = new HashMap<>();
                for (int i = 0; i < rb.size(); i++) {
                    res.add(rb.get(i).getRating() + "");
                }
                for (int j = 0; j < et.size(); j++) {
                    res.add(et.get(j).getText().toString());
                }
                for (int k = 0; k < res.size(); k++) {
                    Question qn = aq.get(k);
                    mapRes.put("Q-" + (k + 1), new Question(qn.getqNumber(), qn.getqType(), qn.getStatement(), res.get(k)));
                }
                System.out.print(mapRes.size());
                try {
                    Manager m = new Manager(new AndroidContext(getActivity()), Manager.DEFAULT_OPTIONS);
                    Database db = m.getExistingDatabase("survey_responses5");
                    Document doc = db.getDocument("res-" + System.currentTimeMillis());
                    doc.putProperties(mapRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EndFragment ef = new EndFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.enter_right, R.animator.exit_left);
                ft.replace(R.id.main, ef);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
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

    private class QuestionAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            RecyclerView.ViewHolder vh = null;
            switch (viewType) {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ratingcard, parent, false);
                    vh = new RatingHolder(view);
                    rb.add((RatingBar) view.findViewById(R.id.ratingBar));
                    break;
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frqcard, parent, false);
                    vh = new FRQHolder(view);
                    et.add((EditText) view.findViewById(R.id.edit2));
                    break;
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = holder.getItemViewType();
            switch (type) {
                case 0:
                    RatingHolder rh = (RatingHolder) holder;
                    rh.getTv().setText(aq.get(position).getStatement());
                    break;
                case 1:
                    FRQHolder fh = (FRQHolder) holder;
                    fh.getTv().setText(aq.get(position).getStatement());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return aq.size();
        }

        public int getItemViewType(int position) {
            return position < ratecount ? 0 : 1;
        }
    }
}
