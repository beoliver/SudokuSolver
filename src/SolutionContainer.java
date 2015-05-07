/**
 * Created by boliver on 07/05/15.
 */
import java.util.ArrayList;
import java.util.List;

public class SolutionContainer {

    private int limit;
    private int count = 0;
    private List<List<List<String>>> solutions = new ArrayList<>();

    public SolutionContainer(int limit) {
        this.limit = limit;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized List<List<String>> getSolution(int index) {
        return solutions.get(index);
    }

    public synchronized void addSolution(List<List<String>> solution) {
        if (count < limit) {
            solutions.add(solution);
        }
        count++;
    }

    public synchronized int size() {
        return solutions.size();
    }
}
