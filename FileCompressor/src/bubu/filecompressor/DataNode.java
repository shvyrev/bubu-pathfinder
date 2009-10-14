package bubu.filecompressor;

import java.util.ArrayList;

public class DataNode {

    private short character;
    private ArrayList<DataNode> followingCharacters;
    private long quantity;

    public DataNode() {
    }

    public DataNode(short character) {
        this.character = character;
    }

    public short getCharacter() {
        return character;
    }

    public void setCharacter(short character) {
        this.character = character;
    }

    public ArrayList<DataNode> getFollowingCharacters() {
        if (followingCharacters == null) {
            followingCharacters = new ArrayList<DataNode>();
        }
        return followingCharacters;
    }

    public void setFollowingCharacters(ArrayList<DataNode> followingCharacters) {
        this.followingCharacters = followingCharacters;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String toString() {

        String x = ((char) character) +"->";
        for (DataNode current : getFollowingCharacters()) {
            x = x + ((char)current.getCharacter());
        }

        return x;
    }

    public void incrementQuantity() {
        quantity++;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataNode other = (DataNode) obj;
        if (this.character != other.character) {
            return false;
        }
        return true;
    }

  
}
