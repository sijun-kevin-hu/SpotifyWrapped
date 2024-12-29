package gt.fel2.wrapped;

import java.util.ArrayList;
import java.util.HashMap;

public class UserData {

    public String id;

    public String name;

    public HashMap<String, Boolean> friendIDs;

    public HashMap<String, Boolean> friendIDsNotSharing;

    public HashMap<String, Boolean> friendRequests;

    public ArrayList<ItemEntry> topArtists;

    public ArrayList<ItemEntry> topSongs;

    public ArrayList<String> topGenres;

    public ArrayList<ItemEntry> topAlbums;

    public boolean notifReqs;

    public boolean notifAccess;

    public String notifToken;

    public UserData() {
        id = "placeholder";
        name = "Unknown Name";
        friendIDs = new HashMap<>();
        friendIDsNotSharing = new HashMap<>();
        friendRequests = new HashMap<>();
        topArtists = new ArrayList<>();
        topSongs = new ArrayList<>();
        topGenres = new ArrayList<>();
        topAlbums = new ArrayList<>();
        notifAccess = false;
        notifReqs = false;
        notifToken = "";
    }

    public UserData(String id,
                    String name,
                    HashMap<String, Boolean> friendIDs,
                    HashMap<String, Boolean> friendIDsNotSharing,
                    HashMap<String, Boolean> friendRequests,
                    ArrayList<ItemEntry> topArtists,
                    ArrayList<ItemEntry> topSongs,
                    ArrayList<String> topGenres,
                    ArrayList<ItemEntry> topAlbums,
                    boolean notifAccess,
                    boolean notifReqs,
                    String notifToken

    ) {
        this.id = id;
        this.name = name;
        this.friendIDs = friendIDs;
        this.friendIDsNotSharing = friendIDsNotSharing;
        this.friendRequests = friendRequests;
        this.topArtists = topArtists;
        this.topGenres = topGenres;
        this.topSongs = topSongs;
        this.topAlbums = topAlbums;
        this.notifAccess = notifAccess;
        this.notifReqs = notifReqs;
        this.notifToken = notifToken;
    }

    public HashMap<String, Boolean> getFriendIDs() {
        return friendIDs;
    }

    public HashMap<String, Boolean> getFriendIDsNotSharing() {
        return friendIDsNotSharing;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Boolean> getFriendRequests() {
        return friendRequests;
    }

    public ArrayList<ItemEntry> getTopAlbums() {
        return topAlbums;
    }

    public ArrayList<ItemEntry> getTopArtists() {
        return topArtists;
    }

    public ArrayList<String> getTopGenres() {
        return topGenres;
    }

    public ArrayList<ItemEntry> getTopSongs() {
        return topSongs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFriendIDs(HashMap<String, Boolean> friendIDs) {
        this.friendIDs = friendIDs;
    }

    public void setFriendIDsNotSharing(HashMap<String, Boolean> friendIDsNotSharing) {
        this.friendIDsNotSharing = friendIDsNotSharing;
    }

    public void setFriendRequests(HashMap<String, Boolean> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void setTopAlbums(ArrayList<ItemEntry> topAlbums) {
        this.topAlbums = topAlbums;
    }

    public void setTopArtists(ArrayList<ItemEntry> topArtists) {
        this.topArtists = topArtists;
    }

    public void setTopGenres(ArrayList<String> topGenres) {
        this.topGenres = topGenres;
    }

    public void setTopSongs(ArrayList<ItemEntry> topSongs) {
        this.topSongs = topSongs;
    }

    public void setNotifAccess(boolean notifAccess) {
        this.notifAccess = notifAccess;
    }

    public void setNotifReqs(boolean notifReqs) {
        this.notifReqs = notifReqs;
    }

    public boolean getNotifAccess() {
        return notifAccess;
    }

    public boolean getNotifReqs() {
        return notifReqs;
    }

    public String getNotifToken() {
        return notifToken;
    }

    public void setNotifToken(String notifToken) {
        this.notifToken = notifToken;
    }
}
