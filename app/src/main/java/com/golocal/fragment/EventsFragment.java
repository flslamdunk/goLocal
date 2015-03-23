package com.golocal.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.golocal.activity.*;
import com.golocal.model.Item;
import com.golocal.utils.Constants;
import com.golocal.utils.Helpers;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

public class EventsFragment extends Fragment {

    private View mLoadingView;
    private View mNodataView;
    private Spinner mLocationSpinner;
    List<Item> cardValues = new ArrayList<Item>();
    SharedPreferences preferences;
    public static final String TAG = "events"; //The fragment's tag
    public static int mTime;

    public static EventsFragment newInstance(int time) {
        EventsFragment fragment = new EventsFragment();
        mTime = time;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        mLoadingView = view.findViewById(R.id.empty);
        mLoadingView.setVisibility(View.VISIBLE);
        mNodataView = view.findViewById(R.id.nodata);

        mLocationSpinner = (Spinner) view.findViewById(R.id.location_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(adapter);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    }

    @Override
    public void onResume(){
        super.onResume();
        makeEventsCall(mTime);
    }

    /**
     * make a network call to event endpoint to fetch events
     */
    private void makeEventsCall(int time) {
        String url = String.format(Constants.EVENTS_URL, time);
        Ion.with(this)
                .load(url)
                .as(new TypeToken<List<Item>>(){})
                .setCallback(new FutureCallback<List<Item>>() {
                    @Override
                    public void onCompleted(Exception e, List<Item> values) {
                        mLoadingView.setVisibility(View.GONE);
                        if(values.size() > 0) {
                            cardValues = values;
                            initCards();
                        }else{
                            mNodataView.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void makeActionCall(boolean like, int id) {
        JsonObject json = new JsonObject();
        json.addProperty("like", String.valueOf(like));
        json.addProperty("id", id);
        Ion.with(this)
                .load(Constants.ACTION_URL)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                    }
                });
    }

    private void initCards(){
        //Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < cardValues.size(); i++) {
            EventCard card = new EventCard(this.getActivity());
            Item item = cardValues.get(i);
            if(item != null){
                CardHeader header = new CardHeader(this.getActivity());
                header.setTitle(item.title);
                card.addCardHeader(header);

//                CardThumbnail thumbnail = new CardThumbnail(this.getActivity());
//                thumbnail.setDrawableResource(R.drawable.number_1);
//                card.addCardThumbnail(thumbnail);

                card.likes = item.likes;
                card.unlikes = item.dislikes;
                card.eventId = item.id;
                card.url = item.url;
                card.category = item.category;
            }
            card.init();
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.card_list_view);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);

        }
    }

    public class EventCard extends Card {

        TextView mLikes;
        TextView mUnlikes;
        ImageView mLikesImage;
        ImageView mUnlikeImage;
        ImageView mCategoryImage;
        RelativeLayout mLikesContainer;
        RelativeLayout mDislikesContainer;

        String url;
        String category;
        int likes;
        int unlikes;
        int eventId;


        public EventCard(Context context) {
            this(context, R.layout.event);
        }

        public EventCard(Context context, int innerLayout) {
            super(context, innerLayout);
            init();
        }

        private void init() {
            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    EventCard ec = (EventCard) card;
                    String url = ec.url;
                    //a url without http will throw exception
                    if (!url.toLowerCase().startsWith("http://")) {
                        url = "http://" + url;
                    }
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(intent);
                }
            });
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            mLikes = (TextView) parent.findViewById(R.id.likes_number);
            mUnlikes = (TextView) parent.findViewById(R.id.dislike_number);
            mLikesImage = (ImageView) parent.findViewById(R.id.likes_image);
            mUnlikeImage = (ImageView) parent.findViewById(R.id.dislike_image);
            mCategoryImage = (ImageView) parent.findViewById(R.id.category_image);
            mLikesContainer = (RelativeLayout)parent.findViewById(R.id.like_container);
            mDislikesContainer = (RelativeLayout)parent.findViewById(R.id.dislike_container);

            if(preferences.contains(Helpers.getLikedKey(eventId))) {
                mLikesImage.setImageResource(R.drawable.happy2);
            }
            if(preferences.contains(Helpers.getDislikedKey(eventId))) {
                mUnlikeImage.setImageResource(R.drawable.angry2);
            }
            mLikesContainer.setTag(eventId);
            mDislikesContainer.setTag(eventId);

            mLikes.setText(likes + "");
            mUnlikes.setText(unlikes + "");

            mCategoryImage.setImageResource(Helpers.categoryToResource(category));

            //handle like event
            mLikesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int eventId = (Integer)v.getTag();
                    //allow action only when user did not do any action this event
                    if(!preferences.contains(Helpers.getLikedKey(eventId)) && !preferences.contains(Helpers.getDislikedKey(eventId))) {
                        TextView mLikesNumber = (TextView) v.findViewById(R.id.likes_number);
                        int currentLikes = Integer.parseInt(mLikesNumber.getText().toString());
                        mLikesNumber.setText(String.valueOf(currentLikes + 1));

                        ImageView likeIV = (ImageView) v.findViewById(R.id.likes_image);
                        likeIV.setImageResource(R.drawable.happy2);

                        //save local state to sharedPreference
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Helpers.getLikedKey(eventId), true);
                        editor.apply();

                        //make a network call to server to increase like by 1
                        makeActionCall(true, eventId);
                    }
                }
            });

            //handle unlike event
            mDislikesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int eventId = (Integer)v.getTag();
                    //allow action only when user did not do any action this event
                    if(!preferences.contains(Helpers.getLikedKey(eventId)) && !preferences.contains(Helpers.getDislikedKey(eventId))) {
                        TextView dislikeTV = (TextView) v.findViewById(R.id.dislike_number);
                        int currentDislikes = Integer.parseInt(dislikeTV.getText().toString());
                        dislikeTV.setText(String.valueOf(currentDislikes + 1));

                        ImageView dislikeIV = (ImageView) v.findViewById(R.id.dislike_image);
                        dislikeIV.setImageResource(R.drawable.angry2);

                        //save local state to sharedPreference
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Helpers.getDislikedKey(eventId), true);
                        editor.apply();

                        //make a network call to server to increase dislike by 1
                        makeActionCall(false, eventId);
                    }
                }
            });
        }
    }
}