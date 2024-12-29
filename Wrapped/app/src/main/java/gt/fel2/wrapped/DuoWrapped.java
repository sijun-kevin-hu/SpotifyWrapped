package gt.fel2.wrapped;

import static android.content.ContentValues.TAG;

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

import gt.fel2.wrapped.databinding.DuoWrappedViewBinding;
import gt.fel2.wrapped.databinding.FragmentDuoWrappedBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DuoWrapped#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DuoWrapped extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentDuoWrappedBinding binding;

    private ViewGroup wrappedViewHolder;

    private FirebaseDatabase database;

    public DuoWrapped() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DuoWrapped.
     */
    // TODO: Rename and change types and number of parameters
    public static DuoWrapped newInstance(String param1, String param2) {
        DuoWrapped fragment = new DuoWrapped();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DuoWrappedViewBinding createDuoWrappedView(String categoryTitle, String duoName) {
        @NonNull DuoWrappedViewBinding lb = DuoWrappedViewBinding
                .inflate(getLayoutInflater(), wrappedViewHolder, false);
        lb.titleText.setText(categoryTitle);
        lb.theirName.setText(duoName);

        //lb.yourFirstImage.setImageDrawable();
        lb.yourFirstText.setText("Kendrick Lamar");
        lb.yourSecondText.setText("Nujabes");
        lb.yourThirdText.setText("Lorde");

        //lb.theirFirstImage.setImageDrawable();
        lb.theirFirstText.setText("Travis Scott");
        lb.theirSecondText.setText("Lorde");
        lb.theirThirdText.setText("Jay-Z");

        wrappedViewHolder.addView(lb.getRoot());

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
        binding = FragmentDuoWrappedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        wrappedViewHolder = (ViewGroup) binding.holder;



        String duoName = DuoWrappedArgs.fromBundle(getArguments()).getDuoName();
        String duoID = DuoWrappedArgs.fromBundle(getArguments()).getDuoID();

        binding.duoWrappedSubtitle.setText("See how your music taste stacks up to " + duoName + "'s.");

        DuoWrappedViewBinding artistView = createDuoWrappedView("Top Artists", duoName);
        DuoWrappedViewBinding genreView = createDuoWrappedView("Top Genres", duoName);
        DuoWrappedViewBinding songView = createDuoWrappedView("Top Songs", duoName);
        DuoWrappedViewBinding albumView = createDuoWrappedView("Top Albums", duoName);

        DatabaseReference dbRef = database.getReference("users");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userID)) {
                    getActivity().runOnUiThread(() -> {
                        UserData d = snapshot.child(userID).getValue(UserData.class);

                        artistView.yourFirstText.setText(d.topArtists.get(0).name);
                        artistView.yourSecondText.setText(d.topArtists.get(1).name);
                        artistView.yourThirdText.setText(d.topArtists.get(2).name);
                        Glide.with(DuoWrapped.this).load(d.topArtists.get(0).imageUrl)
                                .into((ImageView) artistView.yourFirstImage);

                        genreView.yourFirstText.setText(d.topGenres.get(0));
                        genreView.yourSecondText.setText(d.topGenres.get(1));
                        genreView.yourThirdText.setText(d.topGenres.get(2));
//                        Glide.with(DuoWrapped.this).load(d.topGenres.get(0))
//                                .into((ImageView) genreView.yourFirstImage);

                        songView.yourFirstText.setText(d.topSongs.get(0).name);
                        songView.yourSecondText.setText(d.topSongs.get(1).name);
                        songView.yourThirdText.setText(d.topSongs.get(2).name);
                        //Glide.with(DuoWrapped.this).load(d.topSongs.get(0).imageURL)
                        //        .into((ImageView) songView.yourFirstImage);

                        albumView.yourFirstText.setText(d.topAlbums.get(0).name);
                        albumView.yourSecondText.setText(d.topAlbums.get(1).name);
                        albumView.yourThirdText.setText(d.topAlbums.get(2).name);
                        Glide.with(DuoWrapped.this).load(d.topAlbums.get(0).imageUrl)
                                .into((ImageView) albumView.yourFirstImage);
                    });
                }
                if (snapshot.hasChild(duoID)) {
                    getActivity().runOnUiThread(() -> {
                        UserData d = snapshot.child(duoID).getValue(UserData.class);

                        artistView.theirFirstText.setText(d.topArtists.get(0).name);
                        artistView.theirSecondText.setText(d.topArtists.get(1).name);
                        artistView.theirThirdText.setText(d.topArtists.get(2).name);
                        Glide.with(DuoWrapped.this).load(d.topArtists.get(0).imageUrl)
                                .into((ImageView) artistView.theirFirstImage);

                        genreView.theirFirstText.setText(d.topGenres.get(0));
                        genreView.theirSecondText.setText(d.topGenres.get(1));
                        genreView.theirThirdText.setText(d.topGenres.get(2));
//                        Glide.with(DuoWrapped.this).load(d.topGenres.get(0))
//                                .into((ImageView) genreView.theirFirstImage);

                        songView.theirFirstText.setText(d.topSongs.get(0).name);
                        songView.theirSecondText.setText(d.topSongs.get(1).name);
                        songView.theirThirdText.setText(d.topSongs.get(2).name);
                        //Glide.with(DuoWrapped.this).load(d.topSongs.get(0).imageURL)
                        //        .into((ImageView) songView.theirFirstImage);

                        albumView.theirFirstText.setText(d.topAlbums.get(0).name);
                        albumView.theirSecondText.setText(d.topAlbums.get(1).name);
                        albumView.theirThirdText.setText(d.topAlbums.get(2).name);
                        Glide.with(DuoWrapped.this).load(d.topAlbums.get(0).imageUrl)
                                .into((ImageView) albumView.theirFirstImage);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
        
    }
}