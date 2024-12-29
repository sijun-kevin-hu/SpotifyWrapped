package gt.fel2.wrapped;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gt.fel2.wrapped.databinding.FragmentMainwrappedBinding;
import gt.fel2.wrapped.databinding.MainWrappedViewBinding;

public class MainSummary extends Fragment {

    private FragmentMainwrappedBinding binding;

    private ViewGroup holder;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainwrappedBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public MainWrappedViewBinding createMainWrappedView(String category) {
        @NonNull MainWrappedViewBinding lb = MainWrappedViewBinding
                .inflate(getLayoutInflater(), holder, false);
        lb.titleText.setText("Your Top " + category + " Was...");
        lb.firstText.setText("Loading...");
        lb.firstDesc.setText("Quite an achievement!");
        lb.secondText.setText("2. ");
        lb.secondDesc.setText("Didn't quite clinch the first spot!");
        lb.thirdText.setText("3. ");
        lb.thirdDesc.setText("");
        lb.fourthText.setText("4. ");
        lb.fourthDesc.setText("");
        lb.fifthText.setText("5. ");
        lb.fifthDesc.setText("");
        holder.addView(lb.getRoot());
        return lb;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();

        holder = (ViewGroup) binding.holder;

        MainWrappedViewBinding albumBinding = createMainWrappedView("Album");
        MainWrappedViewBinding artistBinding = createMainWrappedView("Artist");
        MainWrappedViewBinding songBinding = createMainWrappedView("Song");
        MainWrappedViewBinding genreBinding = createMainWrappedView("Genre");


//        binding.toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
//            @Override
//            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
//                Snackbar.make(binding.getRoot(), "Refreshing data...", Snackbar.LENGTH_SHORT).show();
//            }
//        });

        DatabaseReference dataRef = database.getReference("users");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (snapshot.hasChild(uid)) {
                    getActivity().runOnUiThread(() -> {
                        UserData d = snapshot.child(uid).getValue(UserData.class);
                        // set the text in the ui

                        if (d.topArtists.size() < 1) {
                            return;
                        }

                        artistBinding.firstText.setText(d.topArtists.get(0).name);
                        artistBinding.secondText.setText("2. " + d.topArtists.get(1).name);
                        artistBinding.thirdText.setText("3. " + d.topArtists.get(2).name);
                        artistBinding.fourthText.setText("4. " + d.topArtists.get(3).name);
                        artistBinding.fifthText.setText("5. " + d.topArtists.get(4).name);

                        Glide.with(MainSummary.this).load(d.topArtists.get(0).imageUrl)
                                .into((ImageView) artistBinding.firstImage);
                        Glide.with(MainSummary.this).load(d.topArtists.get(1).imageUrl)
                                .into((ImageView) artistBinding.secondImage);
                        Glide.with(MainSummary.this).load(d.topArtists.get(2).imageUrl)
                                .into((ImageView) artistBinding.thirdImage);
                        Glide.with(MainSummary.this).load(d.topArtists.get(3).imageUrl)
                                .into((ImageView) artistBinding.fourthImage);
                        Glide.with(MainSummary.this).load(d.topArtists.get(4).imageUrl)
                                .into((ImageView) artistBinding.fifthImage);

                        songBinding.firstText.setText(d.topSongs.get(0).name);
                        songBinding.secondText.setText("2. " + d.topSongs.get(1).name);
                        songBinding.thirdText.setText("3. " + d.topSongs.get(2).name);
                        songBinding.fourthText.setText("4. " + d.topSongs.get(3).name);
                        songBinding.fifthText.setText("5. " + d.topSongs.get(4).name);

//                        Glide.with(MainSummary.this).load(d.topSongs.get(0).imageURL)
//                                .into((ImageView) songBinding.firstImage);
//                        Glide.with(MainSummary.this).load(d.topSongs.get(1).imageURL)
//                                .into((ImageView) songBinding.secondImage);
//                        Glide.with(MainSummary.this).load(d.topSongs.get(2).imageURL)
//                                .into((ImageView) songBinding.thirdImage);
//                        Glide.with(MainSummary.this).load(d.topSongs.get(3).imageURL)
//                                .into((ImageView) songBinding.fourthImage);
//                        Glide.with(MainSummary.this).load(d.topSongs.get(4).imageURL)
//                                .into((ImageView) songBinding.fifthImage);

                        genreBinding.firstText.setText(d.topGenres.get(0));
                        genreBinding.secondText.setText("2. " + d.topGenres.get(1));
                        genreBinding.thirdText.setText("3. " + d.topGenres.get(2));
                        genreBinding.fourthText.setText("4. " + d.topGenres.get(3));
                        genreBinding.fifthText.setText("5. " + d.topGenres.get(4));

//                        Glide.with(MainSummary.this).load(d.topGenres.get(0).imageURL)
//                                .into((ImageView) genreBinding.firstImage);
//                        Glide.with(MainSummary.this).load(d.topGenres.get(1).imageURL)
//                                .into((ImageView) genreBinding.secondImage);
//                        Glide.with(MainSummary.this).load(d.topGenres.get(2).imageURL)
//                                .into((ImageView) genreBinding.thirdImage);
//                        Glide.with(MainSummary.this).load(d.topGenres.get(3).imageURL)
//                                .into((ImageView) genreBinding.fourthImage);
//                        Glide.with(MainSummary.this).load(d.topGenres.get(4).imageURL)
//                                .into((ImageView) genreBinding.fifthImage);

                        albumBinding.firstText.setText(d.topAlbums.get(0).name);
                        albumBinding.secondText.setText("2. " + d.topAlbums.get(1).name);
                        albumBinding.thirdText.setText("3. " + d.topAlbums.get(2).name);
                        albumBinding.fourthText.setText("4. " + d.topAlbums.get(3).name);
                        albumBinding.fifthText.setText("5. " + d.topAlbums.get(4).name);

                        Glide.with(MainSummary.this).load(d.topAlbums.get(0).imageUrl)
                                .into((ImageView) albumBinding.firstImage);
                        Glide.with(MainSummary.this).load(d.topAlbums.get(1).imageUrl)
                                .into((ImageView) albumBinding.secondImage);
                        Glide.with(MainSummary.this).load(d.topAlbums.get(2).imageUrl)
                                .into((ImageView) albumBinding.thirdImage);
                        Glide.with(MainSummary.this).load(d.topAlbums.get(3).imageUrl)
                                .into((ImageView) albumBinding.fourthImage);
                        Glide.with(MainSummary.this).load(d.topAlbums.get(4).imageUrl)
                                .into((ImageView) albumBinding.fifthImage);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}