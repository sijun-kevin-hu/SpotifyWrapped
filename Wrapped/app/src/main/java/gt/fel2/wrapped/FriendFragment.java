package gt.fel2.wrapped;

import static android.app.ProgressDialog.show;
import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import gt.fel2.wrapped.databinding.FragmentFriendBinding;
import gt.fel2.wrapped.databinding.FriendCardBinding;
import gt.fel2.wrapped.databinding.FriendRequestCardBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {
  private BottomNavigationView bottomNavigationView;
  private Button button;

  private FragmentFriendBinding binding;

  private ViewGroup fh;
  private ViewGroup frq;
  private View bS;
  private BottomSheetBehavior bSB;
  private FirebaseDatabase database;

  private FriendCardBinding selectedCard;
  private String selectedString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void createFriendRequestCard(String uid, String id, String name) {
        @NonNull FriendRequestCardBinding lb = FriendRequestCardBinding
                .inflate(getLayoutInflater(), frq, false);
        lb.titleText.setText("@" + id);
        lb.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Friend request accepted
                frq.removeView(lb.getRoot());
                DatabaseReference dbRef = database.getReference("users");
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(userID)) {
                            dbRef.child(userID).child("friendRequests").child(uid).setValue(false);
                            dbRef.child(userID).child("friendIDs").child(uid).setValue(true);
                        }
                        if (snapshot.hasChild(uid)) {
                            dbRef.child(uid).child("friendIDs").child(userID).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //createFriendCard(uid, id, name, false);
            }
        });
        lb.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Friend request denied
                frq.removeView(lb.getRoot());
            }
        });
        frq.addView(lb.getRoot());
    }

    public void createFriendCard(String uid, String id, String name, boolean sharing) {
        @NonNull FriendCardBinding lb = FriendCardBinding
                .inflate(getLayoutInflater(), fh, false);
        lb.getRoot().setClickable(sharing);
        lb.getRoot().setFocusable(sharing);
        lb.getRoot().setCardElevation(sharing ? 6 : 0);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondaryContainer, typedValue, true);
        @ColorInt int activeColor = typedValue.data;
        theme.resolveAttribute(com.google.android.material.R.attr.colorSurfaceDim, typedValue, true);
        @ColorInt int disabledColor = typedValue.data;
        lb.getRoot().setCardBackgroundColor(sharing ? activeColor : disabledColor);
        lb.name.setText(name);
        lb.desc.setText("@" + id + " · " + (sharing ? "S" : "Not s") + "haring with you");
        String shortName = name.split(" ")[0];
        lb.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCard = lb;
                selectedString = uid;

                bSB.setState(BottomSheetBehavior.STATE_EXPANDED);
                ((TextView) bS.findViewById(R.id.friendSharingDescription))
                        .setText(shortName + " is " + (sharing ? "" : "not ") +"sharing their Wrapped with you.");
                ((TextView) bS.findViewById(R.id.bsTitle))
                        .setText("Sharing settings for " + shortName);
            }
        });

        if (sharing) {
            lb.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendFragmentDirections.ActionFriendFragToDuoWrapped action =
                            FriendFragmentDirections.actionFriendFragToDuoWrapped(uid, shortName);
                    NavHostFragment.findNavController(FriendFragment.this)
                            .navigate(action);
                }
            });
        }

        fh.addView(lb.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();

        bSB = BottomSheetBehavior.from(binding.friendSettingsBottomsheet);
        bSB.setState(BottomSheetBehavior.STATE_HIDDEN);

        binding.deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder m = new MaterialAlertDialogBuilder(requireActivity());
                m.setTitle(getResources().getString(R.string.deleteConfirmationTitle));
                m.setMessage(getResources().getString(R.string.deleteConfirmationMessage));
                m.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                m.setNegativeButton(getResources().getString(R.string.confirmDelete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedCard == null) return;
                        binding.friendHolder.removeView(selectedCard.getRoot());
                        DatabaseReference dbRef = database.getReference("users");
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userID)) {
                                    dbRef.child(userID).child("friendIDs").child(selectedString).setValue(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                m.show();
            }
        });

        binding.addFriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FriendFragment.this)
                        .navigate(R.id.action_friendFrag_to_addFriend);
            }
        });

        binding.friendSharingToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // get friend ID
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (isChecked) {
                    //Update firebase to let this friend see your wrapped
                    database.getReference("users")
                            .child(selectedString)
                            .child("friendIDs")
                            .child(uid)
                            .setValue(true);
                    database.getReference("users")
                            .child(selectedString)
                            .child("friendIDsNotSharing")
                            .child(uid)
                            .setValue(false);
                } else {
                    //Update firebase to disable friend's access to your wrapped
                    database.getReference("users")
                            .child(selectedString)
                            .child("friendIDs")
                            .child(uid)
                            .setValue(false);
                    database.getReference("users")
                            .child(selectedString)
                            .child("friendIDsNotSharing")
                            .child(uid)
                            .setValue(true);
                }
            }
        });

        frq = (ViewGroup) binding.friendRequestHolder;
        fh = (ViewGroup) binding.friendHolder;
        bS = binding.friendSettingsBottomsheet;


        //createFriendRequestCard("mattE", "Matthew Erickson");
        //createFriendCard("mvolovoi", "Mark Volovoi", true);
        //createFriendCard("ehule", "Ethan H", false);

        DatabaseReference dataRef = database.getReference("users");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            frq.removeAllViews();
                            fh.removeAllViews();
                            UserData d = snapshot.child(uid).getValue(UserData.class);
                            // set the text in the ui
                            for (String f : d.friendIDs.keySet()) {
                                if (!d.friendIDs.get(f) || Objects.isNull(f)) {
                                    continue;
                                }
                                UserData fr = snapshot.child(f).getValue(UserData.class);
                                createFriendCard(f, fr.getId(), fr.getName(), true);
                            }
                            for (String f : d.friendIDsNotSharing.keySet()) {
                                if (!d.friendIDsNotSharing.get(f) || Objects.isNull(f)) {
                                    continue;
                                }
                                UserData fr = snapshot.child(f).getValue(UserData.class);
                                createFriendCard(f, fr.getId(), fr.getName(), false);
                            }
                            for (String user : d.friendRequests.keySet()) {
                                if (!d.friendRequests.get(user) || Objects.isNull(user)) {
                                    continue;
                                }
                                UserData f = snapshot.child(user).getValue(UserData.class);
                                createFriendRequestCard(user, f.getId(), f.getName());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadCard:onCancelled", error.toException());
            }
        });
    }
}