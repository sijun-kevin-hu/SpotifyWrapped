package gt.fel2.wrapped;

import android.app.Activity;
import android.content.Intent;
import android.health.connect.datatypes.SleepSessionRecord;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import gt.fel2.wrapped.databinding.FragmentFriendBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * ApiCallback
 * interface for handling API callback
 */
interface ApiCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}

/**
 * A simple {@link Fragment} subclass.
 * Use the   factory method to
 * create an instance of this fragment.
 */
public class SpotifyAPI extends AppCompatActivity {
    public static final String CLIENT_ID = "bbc6fb9a5752451aaddb3bec492a2d7a";
    public static final String REDIRECT_URI = "gt.fel2.wrapped://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    private FirebaseDatabase database;
    private ApiCallback tokenCallBack;
    private TextView tokenTextView, codeTextView, profileTextView,
            artistTextView, tracksTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_spotify_a_p_i);

        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.response_text_view);
        artistTextView =(TextView)findViewById(R.id.artest_text_view);
        tracksTextView = (TextView) findViewById(R.id.tracks_text_view);

        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        Button getArtistBtn = (Button) findViewById(R.id.artist_btn);
        Button getTracksBtn = (Button) findViewById(R.id.tracks_btn);

        // Set the click listeners for the buttons
        //make the first call and wait for ApiCallback
        final String user;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            user = currentUser.getUid();
        } else {
            user = null;
            Toast.makeText(this, "User account is empty", Toast.LENGTH_SHORT).show();
        }

         getToken(tokenCallBack = new ApiCallback() {
             @Override
             public void onSuccess() {
                 onGetUserProfileClicked(new ApiCallback() {
                     @Override
                     public void onSuccess() {
                         Log.d("SpotifyApi", "user profile call succeed");
                         getTopArtistCollection(savedInstanceState, user);

                     }
                     @Override
                     public void onFailure(String errorMessage) {
                        Log.d("SpotifyApi", "user profile call failed");
                     }
                 });
             }

             @Override
             public void onFailure(String errorMessage) {
                  Log.d("SpotifyApi","getToken call failed");
             }
         });

    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken(ApiCallback callback) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(SpotifyAPI.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(SpotifyAPI.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);
            setCallBackAsync(tokenCallBack);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }



    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked(ApiCallback callback) {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.toString());
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(SpotifyAPI.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView); //del;ete this later on
                    callback.onSuccess(); //callback sucess test


                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(SpotifyAPI.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getTopArtist(String range, String user, ApiCallback callback) {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Creat request to get the Top Artist
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range="+range+"&" +
                        "limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(SpotifyAPI.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               // String id, name, imageUrl;
                callback.onSuccess();
                try {
                    String jsonResponse = response.body().string();
                    Log.d("OUTPUT", jsonResponse);
                    final JSONObject jsonObject = new JSONObject(jsonResponse);

                   // setTextAsync(jsonObject.toString(3), artistTextView);

                    database = FirebaseDatabase.getInstance();
                    DatabaseReference artistsRef = database.getReference().
                            child("users").child(user).
                            child("topArtists"); //add +range for timing later
                    DatabaseReference genresRef = database.getReference().
                            child("users").
                            child(user).
                            child("topGenres"); //add +range for timing later

                    JSONArray items = jsonObject.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject artist = items.getJSONObject(i);
                        String id = artist.getString("id");
                        String name = artist.getString("name");

                        String imageUrl = "";
                        JSONArray images = artist.getJSONArray("images");
                        if (images.length() > 0) {
                            imageUrl = images.getJSONObject(0).getString("url");
                        }

                        // Create a unique key for the artist
                        DatabaseReference artistRef = artistsRef.child(Integer.toString(i));
                        artistRef.child("id").setValue(id);
                        artistRef.child("name").setValue(name);
                        artistRef.child("imageUrl").setValue(imageUrl);

                        //Creating unique key for top generes
                        JSONArray genres = artist.getJSONArray("genres");

                        for (int j =0; j< genres.length(); j++) {
                            DatabaseReference listofGenres = genresRef.child(Integer.toString(j));
                            listofGenres.setValue(genres.get(j));
                        }

                    }

                    // Add a ValueEventListener to check if data exists
                    artistsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("Firebase", "Data exists at the location.");
                            } else {
                                Log.d("Firebase", "Data does not exist at the location.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure(error.getMessage());
                            Log.e("Firebase", "Database error: " + error.getMessage());
                        }
                    });
                } catch (IOException | JSONException e) {
                    Log.e("Exception", "Error processing response: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(SpotifyAPI.this, "Failed to process data", Toast.LENGTH_SHORT).show();
                    });

                }
            }
        });
    }
    public void getTopArtistCollection(Bundle savedInstanceState, String user) {
//        getTopArtist("short_term", user,new ApiCallback() {
//            @Override
//            public void onSuccess() {
//                getTopArtist("medium_term",user, new ApiCallback() {
//                    @Override
//                    public void onSuccess() {
//
//
//                    }
//
//                    @Override
//                    public void onFailure(String errorMessage) {
//
//                    }
//                });
//            }
//            @Override
//            public void onFailure(String errorMessage) {
//
//            }
//        });
        getTopArtist("long_term", user, new ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("SpotifyApi", "All Artists call back are succeed");
                getTopTracksCollection(savedInstanceState, user);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    public void getTopTracks(String range, String user, ApiCallback callback) {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Creat request to get the Top Artist
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range="+range+"&" +
                        "limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                callback.onFailure(e.getMessage());
                Toast.makeText(SpotifyAPI.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    //setTextAsync(jsonObject.toString(3), artistTextView);

                    database = FirebaseDatabase.getInstance();
                    DatabaseReference trackRef = database.getReference().child("users").
                            child(user).
                            child("topSongs"); //add +range for timing later
                    DatabaseReference albumRef = database.getReference().
                            child("users").
                            child(user).
                            child("topAlbums"); //add +range for timing later
                    JSONArray items = jsonObject.getJSONArray("items");
                    for (int i =0; i<items.length(); i++) {
                        JSONObject tracks = items.getJSONObject(i);
                        String id = tracks.getString("id");
                        String name = tracks.getString("name");
                        JSONObject albums = tracks.getJSONObject("album");
                        String albumName = albums.getString("name");
                        String imageUrl = ""; // Default value
                         if (albums.has("images")) {
                            JSONArray images = albums.getJSONArray("images");
                            if (images.length() > 0) {
                                imageUrl = images.getJSONObject(0).getString("url");
                           }
                       }

                        //creating a unique key for the top songs
                         DatabaseReference track = trackRef.child(Integer.toString(i));
                         DatabaseReference album = albumRef.child(Integer.toString(i));
                         track.child("id").setValue(id);
                         track.child("name").setValue(name);
                         album.child("name").setValue(albumName);
                         album.child("imageUrl").setValue(imageUrl);
                    }

                    trackRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("FireBase", "Data exist at this locations");
                            } else {
                                Log.d("FireBase", "Data does not exist at this location");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          Log.d("FireBase", "Database error: " + error.getMessage());
                        }
                    });

                  callback.onSuccess();

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    callback.onFailure(e.getMessage());
                    Toast.makeText(SpotifyAPI.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * collection call to all short_term, medium_term, long_term tracks
     */
    public void getTopTracksCollection(Bundle savedInstanceState, String user) {
        getTopTracks("short_term", user, new ApiCallback() {
            @Override
            public void onSuccess() {
                getTopTracks("medium_term",user, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        getTopTracks("long_term", user, new ApiCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("SpotifyApi", "All call back are succeed");
                                //Navigating back to profileFragment
                                if (savedInstanceState == null) {
                                    // Begin the transaction
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    // Replace the fragment_container with the new fragment
                                    ProfileFragment profileFragment = new ProfileFragment();
                                    fragmentTransaction.replace(R.id.spotifyAPI, profileFragment);

                                    // add this transaction to the back stack
                                    fragmentTransaction.addToBackStack(null);

                                    // Commit the transaction
                                    fragmentTransaction.commit();
                                }

                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }
            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }
    private void setCallBackAsync(ApiCallback callback) {
        runOnUiThread(()->callback.onSuccess());
    }
    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email user-top-read" })
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}