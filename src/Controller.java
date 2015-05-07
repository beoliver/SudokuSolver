import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by boliver on 07/05/15.
 */

public class Controller extends JFrame {

    private SudokuPuzzle puzzle;
    private SolutionContainer solutions;
    private Viewer grid = new Viewer();
    private int solutionIndex;
    private Thread puzzleSolver;
    private Thread countLabelUpdater;


    private JPanel box = new JPanel(new GridLayout(7, 1));
    private JButton b1 = new JButton("Load Puzzle");
    private JButton b2 = new JButton("Solve");
    private JButton b3 = new JButton("Previous");
    private JButton b4 = new JButton("Next");
    private JButton b5 = new JButton("QUIT searching!");
    private JButton b6 = new JButton("Save Solution(s)");
    private JLabel  indexPosition = new JLabel("0 /", SwingConstants.RIGHT);
    private JLabel  solutionCount = new JLabel("0", SwingConstants.LEFT);
    private JPanel  info = new JPanel();

    public Controller() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(700, 100);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
        b5.setEnabled(false);
        b6.setEnabled(false);
        info.add(indexPosition);
        info.add(solutionCount);
        box.add(b1);
        box.add(b2);
        box.add(b3);
        box.add(b4);
        box.add(b5);
        box.add(b6);
        box.add(info);
        setContentPane(box);
        pack();
        setVisible(true);


        // button 1: "Load puzzle"
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ok...");
                SudokuFilePathGetter filePathGetter = new SudokuFilePathGetter();

                try {
                    puzzleSolver.interrupt();
                } catch (Exception puzzleworkerException) {
                }

                try {
                    countLabelUpdater.interrupt();
                } catch (Exception countLabelException) {
                }

                solutionIndex = 0;
                puzzle = new SudokuPuzzle();
                solutions = puzzle.getSolutionContainer();
                puzzle.load(filePathGetter.getPath());

                grid.setup(puzzle.getBoard(), puzzle.getRowCount(), puzzle.getColumnCount());
                indexPosition.setText("N");
                solutionCount.setText("A");

                b2.setEnabled(true);
                b3.setEnabled(false);
                b4.setEnabled(false);
                b5.setEnabled(false);
                b6.setEnabled(false);
            }
        });


        // button 2 "Solve"
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!puzzle.isValid()) {
                    indexPosition.setText("INVALID");
                    solutionCount.setText("BOARD");
                } else {
                    puzzleSolver = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5); // i dont think this helps at all
                                puzzle.solve(); // as puzzle.solve is a call to another method.
                            } catch (InterruptedException stopThatShit) {
                                return;
                            }
                        }
                    });
                    puzzleSolver.start();

                    countLabelUpdater = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(5);
                                    int counts = solutions.getCount();
                                    if (puzzleSolver.isAlive()) {
                                        b5.setEnabled(true);
                                    }
                                    solutionCount.setText("" + counts);
                                } catch (InterruptedException stopThatShit) {
                                    return;
                                }

                            }
                        }
                    });
                    countLabelUpdater.start();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }


                    if (!puzzleSolver.isAlive() && (solutions.getCount() == 0)) {
                        // no solution found
                        indexPosition.setText("No solution");
                        solutionCount.setText("exists");
                    }

                    try {
                        grid.updateGrid(solutions.getSolution(solutionIndex));
                        indexPosition.setText(solutionIndex + 1 + " /");
                        solutionCount.setText("" + solutions.getCount());
                    } catch (Exception _e) {
                        indexPosition.setText("0 /");
                        solutionCount.setText("calculating...");
                    }

                    if (solutions.getCount() == 1) {
                        // if there is only one solution, then we dont need a previous or next button
                        b3.setEnabled(false);
                        b4.setEnabled(false);
                        b5.setEnabled(false); // stop calculation
                    } else {
                        b3.setEnabled(false); // we are at the start
                        b4.setEnabled(true);
                        b4.setEnabled(true);
                    }
                    // finally dim this button as we can't do anything
                    b2.setEnabled(false);
                }
            }
        });


        // b3 "previous"
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("solution index is: " + solutionIndex);
                // System.out.println("solution count is: " + solutions.size());
                System.out.println("subtracting one");
                solutionIndex--;
                System.out.println("solution index is: " + solutionIndex);
                grid.updateGrid(solutions.getSolution(solutionIndex));
                indexPosition.setText(solutionIndex+1 + " /");
                if (solutionIndex == 0) {
                    // check out position again
                    // can not go back at beginning
                    b3.setEnabled(false);
                    b4.setEnabled(true);
                }
            }
        });



        // b4 "next" is now working
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solutionIndex++;
                System.out.println("solution index is: " + solutionIndex);
                // System.out.println("solution count is: " + solutions.size());
                grid.updateGrid(solutions.getSolution(solutionIndex));
                indexPosition.setText((solutionIndex + 1) + " /");
                if (solutionIndex + 1 == solutions.size()) {
                    // if we are at the end of the list
                    System.out.println("end of solutions");
                    b4.setEnabled(false);
                }
                if (solutionIndex != 0) {
                    b3.setEnabled(true);
                } else {
                    b3.setEnabled(false);
                }
            }
        });




        // b5 "stop calculation"
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("stopping problem solver thread");
                    puzzleSolver.interrupt();
                    System.out.println(puzzleSolver.isInterrupted());
                } catch (Exception puzzleworkerException) {
                }

                try {
                    System.out.println("stopping countLabelUpdater thread");
                    countLabelUpdater.interrupt();
                    System.out.println(countLabelUpdater.isInterrupted());
                } catch (Exception countLabelException) {
                }
                b5.setEnabled(false);
            }
        });














    }

}

class SudokuFilePathGetter extends JPanel {

    public SudokuFilePathGetter() {}

    public String getPath() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                return selectedFile.getAbsolutePath();
            }
        } catch (Exception e) {
            System.exit(1);
        }
        return null;
    }


}
