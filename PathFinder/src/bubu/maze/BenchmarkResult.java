package bubu.maze;

public class BenchmarkResult {

    private String title;
    private long aStarScore;
    private long bubuScore;

    public BenchmarkResult() {
    }

    public BenchmarkResult(String title) {
        this.title = title;
    }

    public long getaStarScore() {
        return aStarScore;
    }

    public void setaStarScore(long aStarScore) {
        this.aStarScore = aStarScore;
    }

    public long getBubuScore() {
        return bubuScore;
    }

    public void setBubuScore(long bubuScore) {
        this.bubuScore = bubuScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    


    

}
