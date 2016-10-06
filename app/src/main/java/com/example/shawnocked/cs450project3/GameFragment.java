package com.example.shawnocked.cs450project3;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    ViewSwitcher viewSwitcher;
    private Handler mHandler = new Handler();
    private int numberOfClicks;   //track the number of clicks
    private Boolean newGame = false; // newGame to see if user wants to play the game again
    private View defaultpic = null;
    protected TextView count = null;   // the click counter textview

    private Hashtable<View, Integer> defaultPicIdMap = new Hashtable<>();
    private Hashtable<View, Integer> realImageIdMap = new Hashtable<>();
    private Hashtable<Integer, View> pictureGetter = new Hashtable<>();

    int matchCheck = 0;
    View currentClick;
    View lastClick;

    // 2 View Arraylists containing different set of pictures for different games
    static private ArrayList<View> pictureList1 = new ArrayList<View>();
    static private ArrayList<View> pictureList2 = new ArrayList<View>();

    // a defaultPictures array to hold views
    View[] defaultPictures = new View[16];


    // PictureList Generator function
    public void pictureListGenerator(View rootView){
        Resources resources = getResources();
        //List1
        for(int i = 0; i < 8; i++){
            int picid1 = resources.getIdentifier("picture" + i, "id", getActivity().getPackageName());
            View pic1 = rootView.findViewById(picid1);
            pictureList1.add(pic1);
            pictureList1.add(pic1);
            shufflePics(pictureList1);
        }
        //List2
        for (int j = 5; j < 13; j++){
            int picid2 = resources.getIdentifier("picture" + j, "id", getActivity().getPackageName());
            View pic2 = rootView.findViewById(picid2);
            pictureList2.add(pic2);
            pictureList2.add(pic2);
            shufflePics(pictureList2);
        }
    }

    // Picture Randomize Function shuffle all pictures in the arraylist
    public void shufflePics(ArrayList<View> picturelist){
        Collections.shuffle(picturelist);
    }

    // restart function
    public void restart(){
        numberOfClicks = 0;
        ((GameActivity)getActivity()).update(numberOfClicks);
        for(int i = 0; i<16; i++){
            switchView(defaultPictures[i],defaultpic);
        }

        defaultPicIdMap.clear();
        realImageIdMap.clear();
        pictureGetter.clear();
        pictureList1.clear();
        pictureList2.clear();
    }
    // second game function
    public void secondGame(){
        newGame = true;
        restart();
        startGame(getView());
    }

    // function to switch views
    public void switchView(View defaultView, View view){
        ViewGroup parent = (ViewGroup) defaultView.getParent();
        int index = parent.indexOfChild(defaultView);
        parent.removeView(defaultView);
        parent.addView(view, index);
    }

    // function to check if two pictures are matched
    public void checkMatch(View view1, View view2){
        int defaultId = defaultPicIdMap.get(view1);
        int picId = realImageIdMap.get(view2);

        // if IDs don't match, hide both pictures
        if (defaultId != picId){
            switchView(pictureGetter.get(defaultId),defaultPictures[1]);
            switchView(pictureGetter.get(picId),defaultPictures[1]);
        }
        else {
            lastClick = null;
            matchCheck++;
            if (matchCheck == 8){
                Toast.makeText(getContext(), "You are the Champion! ", Toast.LENGTH_LONG).show();
            }
        }
        ((GameActivity) getActivity()).stopChecking();
    }


    public void startGame(View rootview){
        Resources resources = getResources();
        numberOfClicks = 0;

        pictureListGenerator(rootview);

        for (int i = 0; i < 16; i++) {
            // get the default defaultPictures Einstin's ID and set it up
            int id = resources.getIdentifier("image" + i, "id", getActivity().getPackageName());
            defaultpic = ((ImageButton)rootview.findViewById(id));
            ImageButton ib = (ImageButton)rootview.findViewById(id);
            defaultPictures[i] = ib;
            defaultPicIdMap.put(defaultPictures[i], i);

            if(!newGame) {
                realImageIdMap.put(pictureList1.get(i),i);
                pictureGetter.put(i, pictureList1.get(i));
            }
            else {
                realImageIdMap.put(pictureList2.get(i),i);
                pictureGetter.put(i,pictureList2.get(i));
            }

            ib.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    numberOfClicks++;
                    ((GameActivity) getActivity()).update(numberOfClicks);

                    // If the view we click is the default view, we execute the following code.
                    // Avoiding switch pics back to default view
                    if (defaultPicIdMap.get(view) >= 0){
                        // show the corresponding picture hidden behind the default picture
                        int defaultId = defaultPicIdMap.get(view);
                        currentClick = pictureGetter.get(defaultId); // get the corresponding picture
                        switchView(view, currentClick);

                        if (lastClick == null) {
                            lastClick = currentClick;
                        } else {
                            // This means that user clicks the second picture
                            ((GameActivity) getActivity()).checkMatchBar();

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkMatch(currentClick, lastClick);
                                }
                            }, 1000);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        this.count = (TextView) rootView.findViewById(R.id.number_of_clicks);
        startGame(rootView);
        return rootView;
    }


    public String getState(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 16; i++){
            builder.append(i); //id
            builder.append(",");
            builder.append(defaultPicIdMap.get(defaultPictures[i])); //defaultpic's id
            builder.append(",");
            if(!newGame){builder.append(realImageIdMap.get(pictureList1.get(i)));} // image's id
            else {builder.append(realImageIdMap.get(pictureList2.get(i)));}
            builder.append(",");
        }
        builder.append(numberOfClicks);
        builder.append(matchCheck);

        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        restart();
        String[] fields = gameData.split(",");
        int index = 0;
        matchCheck = Integer.parseInt(fields[fields.length-1]);
        numberOfClicks = Integer.parseInt(fields[fields.length-2]);
        View pic = null;
        for(int i = 0; i< fields.length-2; i++){
            int id = i - index;
            switch (id){
                case 0:
                    pic = defaultPictures[Integer.parseInt(fields[i])];  // restore the default picture back
                    break;
                case 1:
                    defaultPicIdMap.put(pic,Integer.parseInt(fields[i]));
                    break;
                case 2:
                    realImageIdMap.put(pictureGetter.get(fields[i]),Integer.parseInt(fields[i]));
                    index = index + 3;
                    break;
            }

        }
    }

    // borrowed online from http://android-er.blogspot.com/2013/11/example-of-viewswitcher.html
    public void setViewSwitcher(View view){
        Animation slide_in_left, slide_out_right;

        slide_in_left = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_out_right);

        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);
    }

}
