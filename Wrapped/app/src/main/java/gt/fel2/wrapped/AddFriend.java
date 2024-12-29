package gt.fel2.wrapped;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gt.fel2.wrapped.databinding.FragmentAddFriendBinding;
import gt.fel2.wrapped.databinding.FragmentFriendBinding;
import gt.fel2.wrapped.databinding.FragmentProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFriend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriend extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentAddFriendBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;

    public AddFriend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriend.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriend newInstance(String param1, String param2) {
        AddFriend fragment = new AddFriend();
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
        binding = FragmentAddFriendBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        binding.submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.userID.getText().toString();
                if (input != "") {
                    DatabaseReference dbRf = database.getReference("users");
                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dbRf.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           String uid = "";
                           for (DataSnapshot child: snapshot.getChildren()) {
                               Log.d("FRIEND", "Searching friend ID " + child.getKey());
                               String key = child.getKey();
                               if (child.child("id").getValue().toString().equals(input)) {
                                   uid = key;
                                   break;
                               }
                           }
                           if (uid.equals("")) return;
                           Log.d("FRIEND", "Adding friend with ID " + uid);
                           dbRf.child(uid).child("friendRequests").child(myUserID).setValue(true);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {
                           Log.w(TAG, "cancelled", error.toException());
                       }

                    });
                    NavHostFragment.findNavController(AddFriend.this)
                            .navigate(R.id.action_addFriend_to_friendFrag);
                }
            }
        });
    }
}