package gt.fel2.wrapped;

public class ItemEntry {

    public String name; //"Kendrick Lamar" or "Humble"

    public String id; // might not even need, "4kad99c9a93cvbacr5f"

    public String imageUrl; //i.scdn.co link

    public ItemEntry() {
        name = "";
        id = "";
        imageUrl = "";
    }

    public ItemEntry(String name, String id, String imageURL) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageURL;
    }

    public ItemEntry(String name, String imageURL) {
        this.name = name;
        this.id = "";
        this.imageUrl = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
