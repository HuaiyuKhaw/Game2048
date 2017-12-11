package game2048;

/**
 * @author Huaiyu Khaw
 */

import Databases.Controller;
import Databases.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Game2048 extends JPanel {
    private Controller controller = new Controller();
    private Data database;
    private String name;
    private List<Data> data = controller.getArrayList();
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Verdana";
    private static int ROW = getRow();
    private static int COLUMN = getColumn();
    private static int newCOLUMN = COLUMN;
    private static int HOLD;
    private static final int TILE_SIZE = (int) ((520 / ROW));
    private static final int TILES_MARGIN = (int) ((16 / ROW) * 0.6);
    private static Tile[] hold = new Tile[ROW * COLUMN];
    Tile emptyTime = new Tile();

    int undo_score = 0;

    private static int getRow() {
        System.out.print("Enter Row: ");
        Scanner s = new Scanner(System.in);
        return s.nextInt();
    }

    private static int getColumn() {
        System.out.print("Enter Column: ");
        Scanner s = new Scanner(System.in);
        return s.nextInt();
    }

    private Tile[] myTiles;
    boolean myWin = false;
    boolean myLose = false;
    int myScore = 0;
    int num;


    public Game2048(String name) {
        this.name = name;
        setPreferredSize(new Dimension(340, 400));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //ESC button to exit the game
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                //R button to reset
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    resetGame();
                }
                
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    Undo();
                    emptyTime.value = 0;
                }


                if (!canMove()) {
                    myLose = true;
                }

                if (!myWin && !myLose) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            if (!remain()) {
                                save();
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            myTiles = rotate(180);
                            if (!remain()) {
                                myTiles = rotate(180);
                                save();
                            } else {
                                myTiles = rotate(180);
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            myTiles = rotate(90);
                            swap();
                            if (!remain()) {
                                myTiles = rotate(270);
                                swap();
                                save();
                            } else {
                                myTiles = rotate(270);
                                swap();
                            }
                            break;
                        case KeyEvent.VK_UP:
                            myTiles = rotate(270);
                            swap();
                            if (!remain()) {
                                myTiles = rotate(90);
                                swap();
                                save();
                            } else {
                                myTiles = rotate(90);
                                swap();
                            }
                            break;
                    }
                }

                if (!myWin && !myLose) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                    }
                }

                if (!myWin && !canMove()) {
                    myLose = true;
                }

                repaint();
            }
        });
        resetGame();
    }

    public void resetGame() {
        myScore = 0;
        myWin = false;
        myLose = false;
        myTiles = new Tile[ROW * COLUMN];
        for (int i = 0; i < myTiles.length; i++) {
            myTiles[i] = new Tile();
        }
        addTile();
        addTile();

    }

    public void clear() {
        for (int i = 0; i < ROW * COLUMN; i++) {
            myTiles[i].value = 0;
        }
    }

    public void Undo() {
        //clear();
        System.arraycopy(hold, 0, myTiles, 0, ROW * COLUMN);
    }

    public void save() {
        undo_score = myScore;
        for (int i = 0; i < ROW; i++) {
            Tile[] line = getLine(i);
            System.arraycopy(line, 0, hold, i * COLUMN, COLUMN);
        }
    }

    public boolean remain() {
        boolean change = false;
        for (int i = 0; i < ROW; i++) {
            Tile[] line = getLine(i);
            Tile[] merged = mergeLine(moveLine(line));
            if (!compare(line, merged)) {
                change = true;
                break;
            }
        }
        return !change;
    }

    public void left() {
        undo_score = myScore;
        boolean needAddTile = false;
        for (int i = 0; i < ROW; i++) {
            Tile[] line = getLine(i);
            // System.arraycopy(line, 0, hold[i], 0, COLUMN);
            Tile[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;
            }

        }

        if (needAddTile) {
            addTile();
        }

    }

    public void right() {
        myTiles = rotate(180);
        left();
        myTiles = rotate(180);
    }

    private void up() {
        myTiles = rotate(270);
        swap();
        left();
        myTiles = rotate(90);
        swap();
    }

    private void down() {
        myTiles = rotate(90);
        swap();
        left();
        myTiles = rotate(270);
        swap();
    }

    private Tile tileAt(int x, int y) {
        return myTiles[x + y * COLUMN];
    }

    private void swap() {
        HOLD = ROW;
        ROW = COLUMN;
        COLUMN = HOLD;
    }

    private void addTile() {
        List<Tile> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            emptyTime = list.get(index);
            emptyTime.value = Math.random() < 0.9 ? 65 : 66;

        }
    }

    private List<Tile> availableSpace() {
        final List<Tile> list = new ArrayList<>(ROW * COLUMN);
        for (Tile t : myTiles) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().isEmpty();
    }

    boolean canMove() {
        if (!isFull()) {
            return true;
        }
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COLUMN; x++) {
                Tile t = tileAt(x, y);
                if ((x < COLUMN - 1 && t.value == tileAt(x + 1, y).value)           // so that the tiles won't exceed the border
                        || ((y < ROW - 1) && t.value == tileAt(x, y + 1).value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(Tile[] line1, Tile[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }

        for (int i = 0; i < line1.length; i++) {
            if (line1[i].value != line2[i].value) {
                return false;
            }
        }
        return true;
    }

    private Tile[] rotate(int angle) {
        Tile[] newTiles = new Tile[ROW * COLUMN];
        int offsetX = COLUMN - 1, offsetY = ROW - 1;
        switch (angle) {
            case 90:
                offsetX = offsetY;
                offsetY = 0;
                newCOLUMN = ROW;
                break;
            case 270:
                offsetY = offsetX;
                offsetX = 0;
                newCOLUMN = ROW;
                break;
            default:
                newCOLUMN = COLUMN;
                break;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);

        for (int x = 0; x < COLUMN; x++) {
            for (int y = 0; y < ROW; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newTiles[(newX) + (newY) * newCOLUMN] = tileAt(x, y);
            }
        }
        return newTiles;
    }

    private Tile[] moveLine(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<>();
        for (int i = 0; i < COLUMN; i++) {
            if (!oldLine[i].isEmpty())
                l.addLast(oldLine[i]);
        }
        if (l.isEmpty()) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[COLUMN];
            ensureSize(l, COLUMN);
            for (int i = 0; i < COLUMN; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeLine(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<>();
        for (int i = 0; i < COLUMN && !oldLine[i].isEmpty(); i++) {
            num = oldLine[i].value;
            if (i < COLUMN - 1 && oldLine[i].value == oldLine[i + 1].value) {
                num++;
                myScore += (num - 64);
                int ourTarget = 91;
                if (num == ourTarget) {
                    num = 0;
                    myWin = true;
                }
                i++;
            }
            list.add(new Tile(num));
        }
        if (list.isEmpty()) {
            return oldLine;
        } else {
            ensureSize(list, COLUMN);
            return list.toArray(new Tile[COLUMN]);
        }
    }

    private static void ensureSize(java.util.List<Tile> l, int s) {
        while (l.size() != s) {
            l.add(new Tile());
        }
    }

    private Tile[] getLine(int index) {
        Tile[] result = new Tile[COLUMN];
        for (int i = 0; i < COLUMN; i++) {
            result[i] = tileAt(i, index);
        }
        return result;
    }

    private void setLine(int index, Tile[] re) {
        System.arraycopy(re, 0, myTiles, index * COLUMN, COLUMN);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < ROW; y++) {                                     // change board layout here
            for (int x = 0; x < COLUMN; x++) {
                drawTile(g, myTiles[x + y * COLUMN], x, y);
            }
        }
    }

    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        char value = (char) tile.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(tile.getBackground());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
        g.setColor(tile.getForeground());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0)
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
        
        if(TILE_SIZE*COLUMN<400){
            if (myWin || myLose) {
                compareScore();
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(78, 139, 202));
                g.setFont(new Font(FONT_NAME, Font.BOLD, 50));
                if (myWin) {
                    g.drawString("You won!", 80, 240);
                }
                if (myLose) {
                    g.drawString("Game over!", 50, 240);
                    g.drawString("You lose!", 80, 300);
                }
                if (myWin || myLose) {
                    g.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
                    g.setColor(new Color(128, 128, 128, 128));
                    g.drawString("Press R to play again", 90, 300);
                    g.drawString("Press ESC to quit", 90, 325);        //Added new text
                }
            }
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
            g.drawString("Score: " + myScore, 0, 575);

            try {
                g.drawString("High Score: " + data.get(0).score, 0, 550);  //Align to left
            } catch (NullPointerException e) {
                database = new Data();
                database.name = "Null";
                database.score = 0;
                data.set(0, database);
                g.drawString("High Score: 0", 255, 555);
            }
        }else{
            if (myWin || myLose) {
                compareScore();
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(78, 139, 202));
                g.setFont(new Font(FONT_NAME, Font.BOLD, 50));
                if (myWin) {
                    g.drawString("You won!", TILE_SIZE*COLUMN/4, 240);
                }
                if (myLose) {
                    g.drawString("Game over!", TILE_SIZE*COLUMN/4-TILE_SIZE/5, 240);
                    g.drawString("You lose!", TILE_SIZE*COLUMN/4, 300);
                }
                if (myWin || myLose) {
                    g.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
                    g.setColor(new Color(128, 128, 128, 128));
                    g.drawString("Press R to play again", 120, TILE_SIZE*(ROW-1));
                    g.drawString("Press ESC to quit", 120, TILE_SIZE*(ROW-1)+25);        //Added new text
                }
            }
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
            g.drawString("Score: " + myScore, 0, 575);

            try {
                g.drawString("High Score: " + data.get(0).score, 0, 550);  //Align to left
            } catch (NullPointerException e) {
                database = new Data();
                database.name = "Null";
                database.score = 0;
                data.set(0, database);
                g.drawString("High Score: 0", 255, 555);
            }
        }
    }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }

    static class Tile {
        int value;

        Tile() {
            this(0);
        }

        Tile(int num) {
            value = num;
        }

        boolean isEmpty() {
            return value == 0;
        }

        Color getForeground() {
            return value < ROW * COLUMN ? new Color(0x776e65) : new Color(0xf9f6f2);
        }

        Color getBackground() {
            switch (value) {
                case 'A':
                    return new Color(0xeee4da);
                case 'B':
                    return new Color(0xede0c8);
                case 'C':
                    return new Color(0xf2b179);
                case 'D':
                    return new Color(0xf59563);
                case 'E':
                    return new Color(0xf67c5f);
                case 'F':
                    return new Color(0xf65e3b);
                case 'G':
                    return new Color(0xedcf72);
                case 'H':
                    return new Color(0xedcc61);
                case 'I':
                    return new Color(0xedc850);
                case 'J':
                    return new Color(0xedc53f);
                case 'K':
                    return new Color(0xeee4da);
                case 'L':
                    return new Color(0xede0c8);
                case 'M':
                    return new Color(0xf2b179);
                case 'N':
                    return new Color(0xf59563);
                case 'O':
                    return new Color(0xf67c5f);
                case 'P':
                    return new Color(0xf65e3b);
                case 'Q':
                    return new Color(0xedcf72);
                case 'R':
                    return new Color(0xedcc61);
                case 'S':
                    return new Color(0xedc850);
                case 'T':
                    return new Color(0xedc53f);
                case 'W':
                    return new Color(0x00ffbf);
                case 'X':
                    return new Color(0x0040ff);
                case 'Y':
                    return new Color(0xcc2efa);
                case 'Z':
                    return new Color(0x5858fa);
            }
            return new Color(0xcdc1b4);
        }
    }

    private void compareScore() {
        boolean isDuplicated = true;
        List<Data> new_data = data;

        outer:
        for (Data check : data) {
            // Checking whether the score can be inside the list or not
            if (check.score <= myScore) {
                //Check the name
                for (Data check2 : data) {

                    //If same name
                    if (check2.name.equalsIgnoreCase(name) && data.indexOf(check) <= data.indexOf(check2)) {
                        //Update the name according to the score
                        database = new Data();
                        database.name = name;
                        database.score = myScore;
                        new_data.set(data.indexOf(check), database);
                        break outer;
                    } else {
                        isDuplicated = false;
                    }
                }
                //New Ppl
                if (!isDuplicated) {
                    database = new Data();
                    database.name = name;
                    database.score = myScore;
                    new_data.add(data.indexOf(check), database);
                    break;
                }
            }
        }
        data = new_data;
        controller.serialize(data);
    }

    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("2048 Game");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setMinimumSize(new Dimension(400, 400));                   //Set minimum size to prevent shit from happening
        game.setSize(COLUMN*TILE_SIZE+17, ROW*TILE_SIZE+82);            //Allow the window to be flexible
        game.setResizable(true);
        game.add(new Introduction().getMain_panel());
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setAlwaysOnTop(true);
        System.out.println(TILE_SIZE);


    }
}
