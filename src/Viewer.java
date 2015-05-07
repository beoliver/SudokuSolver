
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Viewer extends JFrame {

    private JPanel grid = new JPanel();
    ArrayList<SingleFrame> frames;

    public static int pointSize(int width, int boxesPerRow) {
        double a = (width / boxesPerRow) / 2;
        return (int) Math.floor(a + (a / 3));
    }

    public Viewer() {
        super("suduko");
    }

    public Viewer(List<List<String>> unsolved, int row_count, int col_count) {
        super("suduko");
        setup(unsolved,row_count,col_count);
    }

    public void setup(List<List<String>> unsolved,int row_count, int col_count) {
        grid = new JPanel();
        frames = new ArrayList<>();
        int n = row_count * col_count;
        setSize(500, 500);
        setLocation(100, 100);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        grid.setBackground(Color.white);
        grid.setLayout(new GridLayout(n, n, -1, -1));
        grid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        for (List<String> row : unsolved) {
            for (String ascii : row) {
                SingleFrame frame = new SingleFrame(500, row_count, col_count);
                if (!(ascii.equals("."))) {
                    frame.update(ascii);
                    frame.makeImmutable();
                }
                frame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                frames.add(frame);
                grid.add(frame);
            }
        }
        add(grid);
        setVisible(true);
    }



    public void updateGrid(List<List<String>> solution) {
        for (SingleFrame frame : frames) {
            frame.setVisible(false);
        }
        int index = 0;
        for (List<String> row : solution){
            for (String ascii : row) {
                frames.get(index).update(ascii);
                frames.get(index).setVisible(true);
                index++;
            }
        }
    }






    class SingleFrame extends JLabel {

        boolean mutable = true;

        public SingleFrame(int size, int rows, int columns) {
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setVerticalAlignment(SwingConstants.CENTER);
            this.setFont(new Font("Sans Serif", Font.BOLD, pointSize(size, rows * columns)));
            this.setForeground(Color.RED);
            this.setVisible(true);
        }

        public void makeImmutable() {
            this.mutable = false;
            setForeground(Color.GRAY);
        }

        public void update(String value) {
            setText(value);
        }

    }


}

