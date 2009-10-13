package bubu.filecompressor;

public class DataNode {

    private char character;
    private char followingCharacter;
    private long quantity;

    public DataNode() {
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }
    public long getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public char getFollowingCharacter() {
        return followingCharacter;
    }

    public void setFollowingCharacter(char followingCharacter) {
        this.followingCharacter = followingCharacter;
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
        if (this.followingCharacter != other.followingCharacter) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.character;
        hash = 41 * hash + this.followingCharacter;
        return hash;
    }

   


    
}
