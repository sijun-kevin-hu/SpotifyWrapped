package gt.fel2.wrapped;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gt.fel2.wrapped.databinding.FragmentPastWrappedBinding;
import gt.fel2.wrapped.databinding.PastWrappedViewBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastWrapped#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastWrapped extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPastWrappedBinding binding;

    private ViewGroup holder;
    private FirebaseDatabase database;

    public PastWrapped() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PastWrapped.
     */
    // TODO: Rename and change types and number of parameters
    public static PastWrapped newInstance(String param1, String param2) {
        PastWrapped fragment = new PastWrapped();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PastWrappedViewBinding createPastWrappedView(String yearName) {
        @NonNull PastWrappedViewBinding lb = PastWrappedViewBinding
                .inflate(getLayoutInflater(), holder, false);
        lb.titleText.setText(yearName);
        lb.topArtistText.setText("Kendrick Lamar");
        lb.firstSongText.setText("1. " + "Song name");
        lb.secondSongText.setText("2. " + "Song name");
        lb.thirdSongText.setText("3. " + "Song name");
        lb.firstGenreText.setText("1. " + "Genre name");
        lb.secondGenreText.setText("2. " + "Genre name");
        lb.thirdGenreText.setText("3. " + "Genre names");
        //lb.topArtistImage.setImageDrawable();
        holder.addView(lb.getRoot());
        return lb;
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
        binding = gt.fel2.wrapped.databinding.FragmentPastWrappedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();

        holder = (ViewGroup) binding.holder;

        PastWrappedViewBinding thisYear = createPastWrappedView("This year");
        DatabaseReference dbRef = database.getReference("users");
        //createPastWrappedView("2022");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (snapshot.hasChild(uid)) {
                    getActivity().runOnUiThread(() -> {
                        UserData d = snapshot.child(uid).getValue(UserData.class);
                        // set the text in the ui

                        thisYear.topArtistText.setText(d.getTopArtists().get(0).name);
                        Glide.with(PastWrapped.this).load(d.topArtists.get(0).imageUrl)
                                .into((ImageView) thisYear.topArtistImage);
                        thisYear.firstSongText.setText("1. " + d.getTopSongs().get(0).name);
                        thisYear.secondSongText.setText("2. " + d.getTopSongs().get(1).name);
                        thisYear.thirdSongText.setText("3. " + d.getTopSongs().get(2).name);
                        thisYear.firstGenreText.setText("1. " + d.getTopGenres().get(0));
                        thisYear.secondGenreText.setText("2. " + d.getTopGenres().get(1));
                        thisYear.thirdGenreText.setText("3. " + d.getTopGenres().get(2));
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}