package gt.fel2.wrapped;

import static android.content.ContentValues.TAG;

import android.app.UiModeManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.auth.LoginActivity;

import gt.fel2.wrapped.databinding.ActivityMainBinding;
import gt.fel2.wrapped.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {
    private Button button;
    private FragmentProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.FriendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // NavHostFragment.findNavController(ProfileFragment.this)
//                        //.navigate(R.id.action_profileFrag_to_friendFrag);
//            }
//        });

        binding.prevYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFrag_to_pastWrapped);
            }
        });

        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(binding.getRoot(), "Signing out...", Snackbar.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        binding.syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFrag_to_spotifyAPI2);
                Snackbar.make(binding.getRoot(), "Syncing from Spotify...", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder m = new MaterialAlertDialogBuilder(requireActivity());
                m.setTitle(getResources().getString(R.string.deleteAccountConfirmationTitle));
                m.setMessage(getResources().getString(R.string.deleteConfirmationMessage));
                m.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                m.setNegativeButton(getResources().getString(R.string.confirmDelete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(binding.getRoot(), "Deleting account...", Snackbar.LENGTH_SHORT).show();
                    }
                });
                m.show();
            }
        });

        binding.unlinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder m = new MaterialAlertDialogBuilder(requireActivity());
                m.setTitle(getResources().getString(R.string.unlinkAccountConfirmationTitle));
                m.setMessage(getResources().getString(R.string.deleteConfirmationMessage));
                m.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                m.setNegativeButton(getResources().getString(R.string.confirmUnlink), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(binding.getRoot(), "Deleting account...", Snackbar.LENGTH_SHORT).show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                        }
                                    }
                                });
                        Intent intent = new Intent(getActivity(), Login.class);
                        startActivity(intent);
                    }
                });
                m.show();
            }
        });

        binding.loginInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFrag_to_updateLogin);
            }
        });

        binding.darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //turn on dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    //turn off dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        binding.notifyTheirToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference df = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("notifAccess");
                if (isChecked) {
                    //Update firebase to set notification preference on
                    df.setValue(true);
                } else {
                    //Update firebase to set notification preference off
                    df.setValue(false);
                }
            }
        });

        binding.notifyFriendRequestsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference df = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("notifReqs");
                if (isChecked) {
                    //Update firebase to set notification preference on
                    df.setValue(true);
                } else {
                    //Update firebase to set notification preference off
                    df.setValue(false);
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("users");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userID)) {
                    getActivity().runOnUiThread(() -> {
                        UserData d = snapshot.child(userID).getValue(UserData.class);
                        binding.userFullName.setText(d.name);
                        binding.userID.setText("@" + d.id);
                        binding.notifyFriendRequestsToggle.setChecked(d.getNotifReqs());
                        binding.notifyTheirToggle.setChecked(d.getNotifAccess());
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