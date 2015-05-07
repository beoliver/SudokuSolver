import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Created by boliver on 28/04/15.
 */


public class SudokuPuzzle {

    private SolutionContainer solutions = new SolutionContainer(2500);
    private Board board = new Board();

    SudokuPuzzle() {}

    public SolutionContainer getSolutionContainer() {
        return solutions;
    }

    public void load(String filepath) {
        board.readFile(filepath);
    }

    public boolean isValid() {
        return board.isValidBoard();
    }


    public void solve() {
        board.findSolutions();
    }

    static String int_to_ascii(int i) {
        if (i == -1) {
            return ".";
        } else if (i < 10) {
            return Integer.toString(i);
        } else {
            i += 55;
            return Character.toString((char) i);
        }
    }

    static int ascii_to_int(String s) {
        if (s.equals(".")) {
            return -1;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return ((int) s.charAt(0)) - 55;
        }
    }

    public int getRowCount() {
        return board.getRowCount();
    }

    public int getColumnCount() {
        return board.getColCount();
    }

    public List<List<String>> getBoard() {
        return board.getBoard();
    }


    public class Board {

        private List<Row> board = new ArrayList<>();
        private int row_count;
        private int col_count;

        public Board() {}

        public int getRowCount() {
            return row_count;
        }

        public int getColCount() {
            return col_count;
        }

        public void readFile(String path) {
            try {
                File file = new File(path);
                Scanner sr = new Scanner(new FileReader(file));
                row_count = Integer.parseInt(sr.nextLine());
                col_count = Integer.parseInt(sr.nextLine());
                int n = row_count * col_count;
                Field prevField = null;

                List<Col> columns = new ArrayList<>();
                List<Box> boxes = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    columns.add(new Col());
                    boxes.add(new Box());
                }

                int indexPosition, groupedByBoxWidth, groupedByBoxHeight, boxColumn, boxRow, box;

                indexPosition = 0;
                for (int row = 0; row < n; row++) {
                    board.add(new Row());
                    String[] rowString = sr.nextLine().split("");
                    for (int column = 0; column < n; column++) {
                        int charOffset = column + 1;
                        Field newField = new Field(ascii_to_int(rowString[charOffset]));
                        if (prevField != null) {
                            prevField.next = newField;
                        }
                        board.get(row).add(newField);
                        newField.row = board.get(row);
                        columns.get(column).add(newField);
                        newField.col = columns.get(column);
                        groupedByBoxWidth = (int) (Math.floor(indexPosition / col_count));
                        groupedByBoxHeight = (int) (Math.floor(row / row_count));
                        boxColumn = groupedByBoxWidth % row_count;
                        boxRow = groupedByBoxHeight % col_count;
                        box = (boxRow * row_count) + boxColumn;
                        boxes.get(box).add(newField);
                        newField.box = boxes.get(box);
                        prevField = newField;
                        indexPosition++;
                    }
                }
                sr.close();
            } catch (Exception e) {
                System.exit(1);
            }
        }

        public List<List<String>> getBoard() {
            List<List<String>> original = new ArrayList<>();
            for (Row row : board) {
                for (Field field : row) {
                    original.add(new ArrayList<String>());
                    original.get(original.size() - 1).add(int_to_ascii(field.value));
                }
            }
            return original;
        }

        public void findSolutions() {
            board.get(0).get(0).recursiveSolution();
        }

        public boolean isValidBoard() {
            return board.get(0).get(0).recursiveValid();
        }


        class Field {

            int value;
            Field next;
            Row row;
            Col col;
            Box box;

            Field(int v) {
                value = v;
            }

            public List<Integer> findPossibleValues() {
                List<Integer> possibleValues = new ArrayList<>();
                if (value >= 0) { // !mutable
                    possibleValues.add(value);
                } else {
                    for (Integer v = 1; v <= row.size(); v++) {
                        if (!(row.lookupValue(v)) && !(col.lookupValue(v)) && !(box.lookupValue(v))) {
                            possibleValues.add(v);
                        }
                    }
                }
                return possibleValues;
            }

            public void recursiveSolution() {
                Integer originalValue = this.value;
                for (int possibleValue : findPossibleValues()) {
                    this.value = possibleValue;
                    if (next == null) {
                        solutions.addSolution(getBoard());
                    } else {
                        next.recursiveSolution();
                    }
                }
                this.value = originalValue;
            }

            public boolean recursiveValid() {

                if (value > 0) {

                    for (Field field : row) {
                        if ((field.value == this.value) && !field.equals(this)) {
                            return false;
                        }
                    }
                    for (Field field : col) {
                        if ((field.value == this.value) && !field.equals(this)) {
                            return false;
                        }
                    }
                    for (Field field : box) {
                        if ((field.value == this.value) && !field.equals(this)) {
                            return false;
                        }
                    }
                }
                if (this.next == null) {
                    return true;
                } else {
                    return next.recursiveValid();
                }
            }

        }


        class Section extends ArrayList<Field> {

            public boolean lookupValue(int val) {
                for (Field f : this) {
                    if ((f.value >= 0) && (val == (f.value))) {
                        return true;
                    }
                }
                return false;
            }

        }


        class Row extends Section {}

        class Col extends Section {}

        class Box extends Section {}

    }


}