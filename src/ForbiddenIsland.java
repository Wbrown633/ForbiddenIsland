import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class ForbiddenIslandWorld extends World {

  // Defines an int constant
  static final int ISLAND_SIZE = 62;
  // define the height of the island
  static final int ISLAND_HEIGHT = ForbiddenIslandWorld.ISLAND_SIZE / 2;
  // define the floodrate of the island
  static final int FLOOD_RATE = 1;
  // helicopter pieces in the game
  static final int PARTS = 5;
  // cell size
  static final int CELL_SIZE = 10;
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight;
  // the heights of all cells
  ArrayList<ArrayList<Double>> heights;
  // all the cells of this board
  ArrayList<ArrayList<Cell>> cells;
  // image for the game
  WorldImage image = new EmptyImage();
  // temp for testing
  WorldScene scene = new WorldScene(ISLAND_SIZE * 100, ISLAND_SIZE * 100);
  // the player
  Player player;
  // pieces for the player to pick up
  ArrayList<Target> pieces = new ArrayList<Target>();
  // the helicopter
  Target helicopter;
  // tick step
  int tick = 0;

  ForbiddenIslandWorld() {
    this.heights = new ArrayList<ArrayList<Double>>();
    this.board = new MtList();
    this.waterHeight = 0;
    this.cells = new ArrayList<ArrayList<Cell>>(ForbiddenIslandWorld.ISLAND_SIZE);
  }

  // Make the scene to display the game on every tick
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(ForbiddenIslandWorld.ISLAND_SIZE * 100,
        ForbiddenIslandWorld.ISLAND_SIZE);
    // Render the Cells 
    for (Cell c : this.board) {
      w.placeImageXY(c.drawAt(this.image), c.x * ForbiddenIslandWorld.CELL_SIZE,
          c.y * ForbiddenIslandWorld.CELL_SIZE);
    }
    // Render  player
    w.placeImageXY(this.player.image, this.player.xpos * ForbiddenIslandWorld.CELL_SIZE,
        this.player.ypos * ForbiddenIslandWorld.CELL_SIZE);
    // Render helicopter parts
    for (Target t : this.pieces) {
      w.placeImageXY(t.image, t.location.x * ForbiddenIslandWorld.CELL_SIZE,
          t.location.y * ForbiddenIslandWorld.CELL_SIZE);
    }
    // Render helicopter
    w.placeImageXY(helicopter.image, helicopter.location.x * ForbiddenIslandWorld.CELL_SIZE,
        helicopter.location.y * ForbiddenIslandWorld.CELL_SIZE);
    
    return w;
  }

  public void onTick() {

    // Flood board
    tick++;
    if(tick % 10 == 0) {
      this.board.flood();
    }
    
    // if the player is touching one of the helicopter pieces, remove it from the map
    for (int i = this.pieces.size() - 1; i >= 0; i--) {
      Target piece = this.pieces.get(i);
      if (player.isTouching(piece)) {
        this.pieces.remove(piece);
      }
    }
    // if the player is touching the helicopter and has all the pieces they win
    if (player.isTouching(helicopter) && this.pieces.isEmpty()) {
      this.endOfWorld("YOU WIN!!!");
    }
    // if the player is in a flooded cell they drown and lose
    if (player.location.isFlooded) {
      this.endOfWorld("YOU LOSE :( ");
    }
    
  }

  // Handle the key presses of the player
  // EFFECT: move the player character and restart the game if needed
  public void onKeyEvent(String key) {
    // 'm' render the regular mountain
    if (key.equals("m")) {
      this.listHeightsMountain();
      this.initCellsMountain();
      this.startGame();
    }
    // 'r' render the random mountain
    else if (key.equals("r")) {
      this.listHeightsMountain();
      this.initCellsRandom();
      this.startGame();
    }
    // 't' render the procedural mountain
    else if (key.equals("t")) {
      this.listHeightsProc();
      this.initCellsProc();
      this.startGame();
    }
    // handle player movement using the arrow keys
    else if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      this.player = this.player.movePlayer(key);
    }
  }
  
  // Last Scene of the game win or lose
  public WorldScene lastScene(String msg) {
    int screensize = ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE;
    WorldScene w = new WorldScene(ForbiddenIslandWorld.ISLAND_SIZE * 100,
        ForbiddenIslandWorld.ISLAND_SIZE * 100);
    w.placeImageXY(new TextImage(msg, Color.RED), screensize/2, screensize/2);
    return w;
  }

  // reset or start the game for the first time
  void startGame() {
    this.spawnPlayer();
    this.spawnParts(ForbiddenIslandWorld.PARTS);
    this.spawnHelicopter();
  }

  // EFFECT: Fix the links of the cells
  void linkCells() {

    // fix the top left corner cell
    this.cells.get(0).get(0).right = this.cells.get(1).get(0);
    this.cells.get(0).get(0).bottom = this.cells.get(0).get(1);

    // fix the top right corner cell
    int rightindex = this.cells.size() - 1;
    this.cells.get(rightindex).get(0).left = this.cells.get(rightindex - 1).get(0);
    this.cells.get(rightindex).get(0).bottom = this.cells.get(rightindex).get(1);

    // fix the bottom left corner cell
    int bottomindex = this.cells.get(this.cells.size() - 1).size() - 1;
    this.cells.get(0).get(bottomindex).right = this.cells.get(1).get(bottomindex);
    this.cells.get(0).get(bottomindex).top = this.cells.get(0).get(bottomindex - 1);

    // fix the bottom right corner cell
    this.cells.get(rightindex).get(bottomindex).left = this.cells.get(rightindex - 1)
        .get(bottomindex);
    this.cells.get(rightindex).get(bottomindex).top = this.cells.get(rightindex)
        .get(bottomindex - 1);

    // fix the links of the cells on the left border
    for (int j = 1; j < this.cells.get(0).size() - 1; j++) {
      this.cells.get(0).get(j).top = this.cells.get(0).get(j - 1);
      this.cells.get(0).get(j).bottom = this.cells.get(0).get(j + 1);
      this.cells.get(0).get(j).right = this.cells.get(1).get(j);
    }

    // fix the cell links on the right border
    for (int j = 1; j < this.cells.get(this.cells.size() - 1).size() - 1; j++) {
      this.cells.get(rightindex).get(j).top = this.cells.get(rightindex).get(j - 1);
      this.cells.get(rightindex).get(j).bottom = this.cells.get(rightindex).get(j + 1);
      this.cells.get(rightindex).get(j).left = this.cells.get(rightindex - 1).get(j);
    }

    // fix the cell links on the top border
    for (int i = 1; i < this.cells.get(this.cells.size() - 1).size() - 1; i++) {
      this.cells.get(i).get(0).right = this.cells.get(i + 1).get(0);
      this.cells.get(i).get(0).left = this.cells.get(i - 1).get(0);
      this.cells.get(i).get(0).bottom = this.cells.get(i).get(1);
    }

    // fix the cell links on the bottom border
    for (int i = 1; i < this.cells.get(this.cells.size() - 1).size() - 1; i++) {
      this.cells.get(i).get(bottomindex).right = this.cells.get(i + 1).get(bottomindex);
      this.cells.get(i).get(bottomindex).left = this.cells.get(i - 1).get(bottomindex);
      this.cells.get(i).get(bottomindex).top = this.cells.get(i).get(bottomindex - 1);
    }

    // fix the cell links for the rest of the array
    for (int i = 1; i < this.cells.size() - 1; i++) {
      for (int j = 1; j < this.cells.get(0).size() - 1; j++) {
        Cell center = this.cells.get(i).get(j);
        center.top = this.cells.get(i).get(j - 1);
        center.bottom = this.cells.get(i).get(j + 1);
        center.right = this.cells.get(i + 1).get(j);
        center.left = this.cells.get(i - 1).get(j);
      }
    }

  }

  // Effect: create the list of cells for the mountain style of island
  void initCellsMountain() {
    this.cells = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
        if (this.heights.get(i).get(j) < 0.0) {
          temp.add(new OceanCell(this.heights.get(i).get(j), i, j));
        }
        else {
          temp.add(new Cell(this.heights.get(i).get(j), i, j));
        }
      }
      this.cells.add(temp);
      temp = new ArrayList<Cell>();
    }
    this.linkCells();
    this.listCells(this.cells);
    this.spawnPlayer();
  }

  // Effect: create the list of cells for the random style of island
  void initCellsRandom() {
    this.cells = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
        if (this.heights.get(i).get(j) < 0.0) {
          temp.add(new OceanCell(this.heights.get(i).get(j), i, j));
        }
        else {
          temp.add(new Cell(Math.random() * ForbiddenIslandWorld.ISLAND_HEIGHT, i, j));
        }
      }
      this.cells.add(temp);
      temp = new ArrayList<Cell>();
    }
    this.linkCells();
    this.listCells(this.cells);
    this.spawnPlayer();
  }

  // Effect: create the list of cells for the mountain style of island
  void initCellsProc() {
    this.cells = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
        double height = this.heights.get(i).get(j);
        this.heights.get(i).set(j, height - 6.0);
        if (this.heights.get(i).get(j) < 0.0) {
          temp.add(new OceanCell(this.heights.get(i).get(j), i, j));
        }
        else {
          temp.add(new Cell(this.heights.get(i).get(j), i, j));
        }
      }
      this.cells.add(temp);
      temp = new ArrayList<Cell>();
    }
    this.linkCells();
    this.listCells(this.cells);
    this.spawnPlayer();
  }

  // Can only be called after the cells have been initialized
  // Effect: create the player in this game
  public void spawnPlayer() {
    this.player = new Player(this.board.land().randomCell());
  }

  // Spawn a given number of helicopter parts
  // Effect: add new targets to the array list 'pieces'
  public void spawnParts(int parts) {
    this.pieces = new ArrayList<Target>();
    IList<Cell> land = this.board.land();
    // get a random cell on the land
    for (int i = 0; i < parts; i++) {
      this.pieces.add(new Target(land.randomCell(), new Color(255, 0, 255)));
    }
  }

  // Spawn the helicopter on the tallest cell
  public void spawnHelicopter() {
    IList<Cell> land = this.board.land();
    this.helicopter = new Helicopter(land.maxHeight(), Color.CYAN);
  }

  // Effect: create an IList<Cell> from the given nested Array list of cells
  IList<Cell> listCells(ArrayList<ArrayList<Cell>> cells) {
    this.board = new MtList();
    for (ArrayList<Cell> list : cells) {
      for (Cell c : list) {
        this.board = this.board.add(c);
      }
    }
    return this.board;
  }

  // EFFECT: update the the list of doubles for a mountain 'heights'
  void listHeightsMountain() {
    ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
      ArrayList<Double> row = new ArrayList<Double>();
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
        double height = (ForbiddenIslandWorld.ISLAND_HEIGHT
            - (Math.abs(ForbiddenIslandWorld.ISLAND_HEIGHT - i)
                + Math.abs(ForbiddenIslandWorld.ISLAND_HEIGHT - j)));
        row.add(height);
      }
      temp.add(row);
    }
    this.heights = temp;
  }

  // EFFECT: update the list of doubles for a procedurally generated island
  void listHeightsProc() {
    int size = ForbiddenIslandWorld.ISLAND_SIZE;

    // Initialize the size of this.heights
    this.heights = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < size + 1; i++) {
      this.heights.add(new ArrayList<Double>(size + 1));
      for (int j = 0; j < size + 1; j++) {
        this.heights.get(i).add(null);
      }
    }

    // Initialize corner heights to 0.0
    this.heights.get(0).set(0, 0.0);
    this.heights.get(0).set(size, 0.0);
    this.heights.get(size).set(0, 0.0);
    this.heights.get(size).set(size, 0.0);

    // Initialize middle and edge cells for recursion base
    if (size % 2 == 0) {
      // Calculate and set top, bottom, left, and right edge cells
      // Calculate and set top, bottom, left, and right edge cells
      this.heights.get(0).set(size / 2, 1.0); // Left edge single cell
      this.heights.get(size).set(size / 2, 1.0); // Right edge single cell
      this.heights.get(size / 2).set(size, 1.0); // Bottom edge single cell
      this.heights.get(size / 2).set(0, 1.0); // Top edge single cell

      // Set Middle cell to max island height
      this.heights.get(size / 2).set(size / 2, (double) size);

      // Recur on 4 corner grids
      this.subdivide(0, size / 2, 0, size / 2); // Top left
      this.subdivide(0, size / 2, size / 2, size); // Top right
      this.subdivide(size / 2, size, 0, size / 2); // Bottom Left
      this.subdivide(size / 2, size, size / 2, size); // Bottom Right Subdivision
    }
    else {
      // Left 2 edge cells
      this.heights.get(0).set(size / 2, 1.0);
      this.heights.get(0).set(size / 2 + 1, 1.0);

      // Right 2 edge cells
      this.heights.get(size).set(size / 2, 1.0);
      this.heights.get(size).set(size / 2 + 1, 1.0);

      // Bottom 2 edge cells
      this.heights.get(size / 2).set(size, 1.0);
      this.heights.get(size / 2 + 1).set(size, 1.0);

      // Top 2 edge cells
      this.heights.get(size / 2).set(0, 1.0);
      this.heights.get(size / 2 + 1).set(0, 1.0);

      // Set 4 middle heights
      this.heights.get(size / 2).set(size / 2, (double) size); // top-left middle
      this.heights.get(size / 2 + 1).set(size / 2, (double) size); // top-right middle
      this.heights.get(size / 2).set(size / 2 + 1, (double) size); // bottom-left middle
      this.heights.get(size / 2 + 1).set(size / 2 + 1, (double) size); // bottom-right middle

      // Subdivide 4 distinct corners of the grid
      this.subdivide(0, size / 2, 0, size / 2); // Top left
      this.subdivide(size / 2 + 1, size, 0, size / 2); // Top right
      this.subdivide(0, size / 2, size / 2 + 1, size); // Bottom Left
      this.subdivide(size / 2 + 1, size, size / 2 + 1, size); // Bottom Right Subdivision
    }
  }

  void subdivide(int xmin, int xmax, int ymin, int ymax) {

    double tl = this.heights.get(xmin).get(ymin);
    double tr = this.heights.get(xmax).get(ymin);
    double bl = this.heights.get(xmin).get(ymax);
    double br = this.heights.get(xmax).get(ymax);

    if ((ymax - ymin == 1) && (xmax - xmin == 1)) {

    }
    // else if (true) {
    else if ((xmax - xmin) % 2 == 0) {
      // Calculate and set top, bottom, left, and right edge cells
      this.heights.get(xmin).set((ymin + ymax) / 2, avgRand(tl, bl, xmax - xmin)); // Left edge
                                                                                   // single cell
      this.heights.get(xmax).set((ymin + ymax) / 2, avgRand(tr, br, xmax - xmin)); // Right edge
                                                                                   // single cell
      this.heights.get((xmin + xmax) / 2).set(ymax, avgRand(br, bl, xmax - xmin)); // Bottom edge
                                                                                   // single cell
      this.heights.get((xmin + xmax) / 2).set(ymin, avgRand(tr, tl, xmax - xmin)); // Top edge
                                                                                   // single cell
      this.heights.get((xmin + xmax) / 2).set((ymin + ymax) / 2,
          avgRandM(tl, tr, bl, br, xmax - xmin)); // Middle single cell

      // Subdivide 4 corners of grid with overlapping edges
      this.subdivide(xmin, (xmax + xmin) / 2, ymin, (ymax + ymin) / 2); // Top left
      this.subdivide((xmax + xmin) / 2, xmax, ymin, (ymax + ymin) / 2); // Top right
      this.subdivide(xmin, (xmax + xmin) / 2, (ymax + ymin) / 2, ymax); // Bottom left
      this.subdivide((xmax + xmin) / 2, xmax, (ymax + ymin) / 2, ymax); // Bottom right recur
    }
    else {
      // Left 2 edge cells
      double l = avgRand(tl, bl, xmax - xmin);
      this.heights.get(xmin).set((ymin + ymax) / 2, l);
      this.heights.get(xmin).set((ymin + ymax) / 2 + 1, l);

      // Right 2 edge cells
      double r = avgRand(tr, br, xmax - xmin);
      this.heights.get(xmax).set((ymin + ymax) / 2, r);
      this.heights.get(xmax).set((ymin + ymax) / 2 + 1, r);

      // Bottom 2 edge cells
      double b = avgRand(bl, br, xmax - xmin);
      this.heights.get((xmin + xmax) / 2).set(ymax, b);
      this.heights.get((xmin + xmax) / 2 + 1).set(ymax, b);

      // Top 2 edge cells
      double t = avgRand(tl, tr, xmax - xmin);
      this.heights.get((xmin + xmax) / 2).set(ymin, t);
      this.heights.get((xmin + xmax) / 2 + 1).set(ymin, t);

      // Middle 4 cells
      double m = avgRandM(tl, tr, bl, br, xmax - xmin);
      this.heights.get((xmin + xmax) / 2).set((ymin + ymax) / 2, m); // Middle- Top Left
      this.heights.get((xmin + xmax) / 2 + 1).set((ymin + ymax) / 2, m); // Middle- Top Right
      this.heights.get((xmin + xmax) / 2).set((ymin + ymax) / 2 + 1, m); // Middle- Bottom Left
      this.heights.get((xmin + xmax) / 2 + 1).set((ymin + ymax) / 2 + 1, m); // Middle- Bottom Right

      // Subdivide 4 distinct corners of the grid
      this.subdivide(xmin, (xmax + xmin) / 2, ymin, (ymax + ymin) / 2); // Top left
      this.subdivide((xmax + xmin) / 2 + 1, xmax, ymin, (ymax + ymin) / 2); // Top right
      this.subdivide(xmin, (xmax + xmin) / 2, (ymax + ymin) / 2 + 1, ymax); // Bottom left
      this.subdivide((xmax + xmin) / 2 + 1, xmax, (ymax + ymin) / 2 + 1, ymax); // Bottom right
    }

  }

  // Calculate height of edge cell of subdivision
  double avgRand(double a, double b, int size) {
    return (a + b) / 2 + (Math.random() - .5) * size;
  }

  // Calculate height of middle cell of subdivision
  double avgRandM(double a, double b, double c, double d, int size) {
    return (a + b + c + d) / 4 + (Math.random() - .5) * size;
  }

}

// Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  double height;
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  // reports whether this cell is flooded or not
  boolean isFlooded;

  // Construct a cell given the height as a double
  Cell(Double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.isFlooded = false;
    this.left = this;
    this.right = this;
    this.bottom = this;
    this.top = this;
  }

  // Draws this tile onto the background at the specified logical coordinates
  WorldImage drawAt(WorldImage background) {
    return new OverlayImage(new RectangleImage(ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID, this.getColor()), background);
  }

  // determine the color of this cell
  private Color getColor() {
    int x = (int) (this.height * 255) / ForbiddenIslandWorld.ISLAND_SIZE;
    x = Math.max(-255, Math.min(255, x));
    int r;
    int g;
    int b;
    if (!this.isFlooded) {
      if (this.height >= 0 && this.height <= 255) {
        r = x / 2;
        g = x / 2 + 128;
        b = x / 2;
      }
      else if (this.height < 0) {
        r = 255;
        g = Math.abs(x);
        b = 0;
      }
      else {
        r = 127;
        g = 255;
        b = 127;
      }
    }
    else {
      r = 0;
      g = 0;
      System.out.println("debug: blue rgb value outof bounds: " + x + "---" + this.height);
      b = 255 - Math.abs(x);
    }
    return new Color(r, g, b);
  }

  // is this cell an ocean cell?
  public boolean isOcean() {
    return false;
  }

  // for writing tests, make this a neighbor with that
  public void makeNeighbor(Cell c) {
    this.left = c;
  }

  // is this cell's location close to that cell's location
  public boolean isTouching(Cell that) {
    return Math.sqrt(Math.abs(this.x - that.x) * Math.abs(this.x - that.x)
        + Math.abs(this.y - that.y) * Math.abs(this.y - that.y)) <= ForbiddenIslandWorld.CELL_SIZE
            / 4;
  }
}

class OceanCell extends Cell {

  OceanCell(Double height, int x, int y) {
    super(height, x, y);
    this.isFlooded = true;
  }

  // Draws this tile onto the background
  WorldImage drawAt(WorldImage background) {
    return new OverlayImage(new RectangleImage(ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID, Color.BLUE), background);
  }

  // is this cell an ocean cell?
  public boolean isOcean() {
    return true;
  }

}

class ExamplesForbidden {
  ForbiddenIslandWorld ex1;

  Cell c1 = new Cell(2.0, 1, 1);
  Cell c2 = new Cell(4.0, 2, 2);
  Cell c3 = new Cell(5.0, 1, 5);
  MtList mt = new MtList();

  IList<Cell> l1 = new ConsList(c1, mt);
  IList<Cell> l2 = new ConsList(c2, l1);

  // ------------------------------------ World Tests
  // --------------------------------------

  // run the game
  void testGame(Tester t) {
    this.initTest();
    this.ex1.listHeightsProc();
    this.ex1.initCellsProc();
    this.ex1.startGame();
    ex1.bigBang(ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE, .5);
  }

  // initialize the test conditions
  void initTest() {
    ex1 = new ForbiddenIslandWorld();
    this.ex1.listHeightsMountain();
  }

  // init the test to run the mountain game
  void initTestMountain() {
    ex1 = new ForbiddenIslandWorld();
    this.ex1.listHeightsMountain();
    this.ex1.initCellsMountain();
  }

  // test the mountain island height creation
  void testListHeights(Tester t) {
    this.initTest();
    t.checkExpect(ex1.heights.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    for (ArrayList<Double> list : ex1.heights) {
      t.checkExpect(list.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    }
  }

  // test the initCells method
  // test that we make enough cells, that all cells of height < 0 are ocean cells
  // and test that every height is a valid height from the list
  void testInitCells(Tester t) {
    this.initTest();
    this.ex1.initCellsMountain();
    Utils<Double> u = new Utils<Double>();
    Utils<Cell> uc = new Utils<Cell>();
    t.checkExpect(this.ex1.cells.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    for (ArrayList<Cell> list : ex1.cells) {
      t.checkExpect(list.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    }
    ArrayList<Double> flatten = u.flatten(this.ex1.heights);
    ArrayList<Cell> flattenCell = uc.flatten(this.ex1.cells);
    for (Cell c : flattenCell) {
      if (c.height < 0) {
        t.checkExpect(c instanceof OceanCell, true);
      }
      t.checkExpect(flatten.contains(c.height), true);
    }
    this.ex1.initCellsMountain();
    t.checkExpect(this.ex1.cells.size(), ForbiddenIslandWorld.ISLAND_SIZE);
  }

  // test the list cells method
  void testListCells(Tester t) {
    this.initTest();
    Utils<Cell> u = new Utils<Cell>();
    this.ex1.listHeightsMountain();
    this.ex1.initCellsMountain();
    t.checkExpect(ex1.board.length(),
        ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.ISLAND_SIZE);
    ArrayList<Cell> flat = u.flatten(this.ex1.cells);
    for (Cell c : flat) {
      t.checkExpect(this.ex1.board.contains(c), true);
    }
  }

  // test the onKey Method
  void testOnKey(Tester t) {
    this.initTest();
    IList<Cell> temp = this.ex1.board;
    this.ex1.onKeyReleased("r");
  }

  // test the spawnPlayer method
  void testSpawnPlayer(Tester t) {
    this.initTest();
    this.ex1.listHeightsMountain();
    this.ex1.initCellsMountain();
    this.ex1.spawnPlayer();
    t.checkExpect(this.ex1.player.location.isOcean(), false);
    t.checkExpect(this.ex1.board.land().contains(this.ex1.player.location), true);
  }

  // test the spawnParts method
  void testSpawnParts(Tester t) {
    this.initTest();
    this.ex1.listHeightsProc();
    this.ex1.initCellsProc();
    this.ex1.spawnParts(10);
    IList<Cell> land = this.ex1.board.land();
    for (Target target : this.ex1.pieces) {
      t.checkExpect(land.contains(target.location), true);
    }
  }

  // -------------------------- Test Cell Linking ---------------------

  // test the link Cells method
  void testLinkCells(Tester t) {
    this.initTest();
    ex1.initCellsMountain();
    // test a cell in the center
    Cell center = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT);
    t.checkExpect(center.left, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT));
    t.checkExpect(center.right, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT));
    t.checkExpect(center.top, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1));
    t.checkExpect(center.bottom, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1));

    // test a cell on the right border
    Cell rightBorderCell = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT);
    t.checkExpect(rightBorderCell.right, rightBorderCell); // right border cells should be linked to
    // themselves on the right
    t.checkExpect(rightBorderCell.left, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 2)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT));
    t.checkExpect(rightBorderCell.top, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1));
    t.checkExpect(rightBorderCell.bottom, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1));

    // test a cell on the left border
    Cell leftBorderCell = this.ex1.cells.get(0).get(ForbiddenIslandWorld.ISLAND_HEIGHT);
    t.checkExpect(leftBorderCell.right,
        this.ex1.cells.get(1).get(ForbiddenIslandWorld.ISLAND_HEIGHT));
    t.checkExpect(leftBorderCell.left, leftBorderCell);
    t.checkExpect(leftBorderCell.top,
        this.ex1.cells.get(0).get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1));
    t.checkExpect(leftBorderCell.bottom,
        this.ex1.cells.get(0).get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1));

    // test a cell on the bottom border
    Cell bottomBorderCell = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 1);
    t.checkExpect(bottomBorderCell.right, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 1));
    t.checkExpect(bottomBorderCell.left, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 1));
    t.checkExpect(bottomBorderCell.top, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 2));
    t.checkExpect(bottomBorderCell.bottom, bottomBorderCell);

    // test a cell on the top border
    Cell topBorderCell = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT).get(0);
    t.checkExpect(topBorderCell.right,
        this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT + 1).get(0));
    t.checkExpect(topBorderCell.left,
        this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1).get(0));
    t.checkExpect(topBorderCell.top, topBorderCell);
    t.checkExpect(topBorderCell.bottom,
        this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT).get(1));

    // test top right corner
    Cell topRight = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1).get(0);
    t.checkExpect(topRight.right, topRight);
    t.checkExpect(topRight.left, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 2).get(0));
    t.checkExpect(topRight.top, topRight);
    t.checkExpect(topRight.bottom, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1).get(1));

    // test top left corner
    Cell topLeft = this.ex1.cells.get(0).get(0);
    t.checkExpect(topLeft.right, this.ex1.cells.get(1).get(0));
    t.checkExpect(topLeft.left, topLeft);
    t.checkExpect(topLeft.top, topLeft);
    t.checkExpect(topLeft.bottom, this.ex1.cells.get(0).get(1));

    // test the bottom right corner
    Cell bottomRight = this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 1);
    t.checkExpect(bottomRight.right, bottomRight);
    t.checkExpect(bottomRight.left, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 2)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 1));
    t.checkExpect(bottomRight.top, this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_SIZE - 1)
        .get(ForbiddenIslandWorld.ISLAND_SIZE - 2));
    t.checkExpect(bottomRight.bottom, bottomRight);

    // test the bottom left corner
    Cell bottomLeft = this.ex1.cells.get(0).get(ForbiddenIslandWorld.ISLAND_SIZE - 1);
    t.checkExpect(bottomLeft.right,
        this.ex1.cells.get(1).get(ForbiddenIslandWorld.ISLAND_SIZE - 1));
    t.checkExpect(bottomLeft.left, bottomLeft);
    t.checkExpect(bottomLeft.top, this.ex1.cells.get(0).get(ForbiddenIslandWorld.ISLAND_SIZE - 2));
    t.checkExpect(bottomLeft.bottom, bottomLeft);

  }

  // ------------------------------------- Test Cell Methods
  // ---------------------------------------------

  void testIsOcean(Tester t) {
    Cell c1 = new Cell(1.0, 1, 1);
    Cell o1 = new OceanCell(1.4, 1, 1);
    t.checkExpect(c1.isOcean(), false);
    t.checkExpect(o1.isOcean(), true);
  }

  // test isTouching in Cell class
  void testIsTouching(Tester t) {
    Cell c3 = new Cell(2.0, 100, 100);

    t.checkExpect(this.c1.isTouching(c2), true);
    t.checkExpect(c1.isTouching(c1), true);
    t.checkExpect(c1.isTouching(c3), false);
    t.checkExpect(c3.isTouching(c1), false);
    t.checkExpect(c2.isTouching(c1), true);
  }

  void testMakeNeightbor(Tester t) {
    Cell c1 = new Cell(1.0, 1, 1);
    Cell c2 = new OceanCell(1.0, 1, 4);
    c1.makeNeighbor(c2);
    t.checkExpect(c1.left.equals(c2), true);
    IPred<Cell> p = new WaterNeighbor();
    t.checkExpect(p.apply(c1), true);
    t.checkExpect(c1.right.equals(c1), true);
  }
  
  
  // ----------------- Test Player Methods-------------------------

  void testIsTouchingPlayer(Tester t) {
    Player p1 = new Player(c1);
    Target t1 = new Target(c1, Color.red);
    t.checkExpect(p1.isTouching(t1), true);
  }

  // test movePlayer
  void testMovePlayer(Tester t) {
    this.initTest();
    this.ex1.initCellsMountain();
    Player p = new Player(this.ex1.cells.get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1)
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1));
    this.ex1.player = p.movePlayer("up");
    t.checkExpect(this.ex1.player.location, this.ex1.cells
        .get(ForbiddenIslandWorld.ISLAND_HEIGHT - 1).get(ForbiddenIslandWorld.ISLAND_HEIGHT - 2));
  }

  // ---------------------- Test IList Methods -------------------------

  // test the add method in the IList interface
  void testAdd(Tester t) {

    t.checkExpect(mt.add(c1).add(c2), l2);
    t.checkExpect(mt.add(c1), l1);
    t.checkExpect(l1.add(c2), l2);
  }

  // test the length method in the IList interface
  void testLength(Tester t) {

    t.checkExpect(l2.length(), 2);
    t.checkExpect(mt.length(), 0);

  }

  // test the contains method in the IList
  void testContains(Tester t) {
    t.checkExpect(l2.contains(c1), true);
    t.checkExpect(mt.contains(c1), false);
    t.checkExpect(l2.contains(c3), false);
  }

  // test the flatten method in the utils class
  void testFlatten(Tester t) {
    Utils<Integer> u = new Utils<Integer>();
    ArrayList<Integer> l1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
    ArrayList<Integer> l2 = new ArrayList<Integer>(Arrays.asList(5, 6, 7, 8));
    ArrayList<Integer> l3 = new ArrayList<Integer>(Arrays.asList(9, 10, 11, 12));

    ArrayList<ArrayList<Integer>> nested = new ArrayList<ArrayList<Integer>>(
        Arrays.asList(l1, l2, l3));
    ArrayList<Integer> expected = new ArrayList<Integer>(
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

    t.checkExpect(u.flatten(nested), expected);
  }

  // test the isCons method
  void testIsCons(Tester t) {
    t.checkExpect(mt.isCons(), false);
    t.checkExpect(l1.isCons(), true);
  }

  // test the iterator in IList<T>
  void testIterator(Tester t) {
    IListIterator iterator = new IListIterator(this.l2);
    t.checkExpect(iterator.hasNext(), true);
    t.checkExpect(iterator.next(), this.c2);
    t.checkExpect(iterator.hasNext(), true);
    t.checkExpect(iterator.next(), this.c1);
    t.checkExpect(iterator.hasNext(), false);
  }

  // test the coastline method in IList<Cell>
  void testCoastline(Tester t) {
    this.initTestMountain();
    IPred<Cell> p = new WaterNeighbor();
    for (Cell c : this.ex1.board.coastline()) {
      t.checkExpect(c.isFlooded, false);
      t.checkExpect(c.isOcean(), false);
      t.checkExpect(p.apply(c), true);
    }

  }

  // test the flood method
  void testFlood(Tester t) {
    this.initTestMountain();
    for (Cell c : this.ex1.board.flood()) {
      t.checkExpect(c.isFlooded, true);
    }
  }

  // test the land method
  void testLand(Tester t) {
    this.initTestMountain();
    for (Cell c : this.ex1.board.land()) {
      t.checkExpect(c.isFlooded, false);
      t.checkExpect(c.isOcean(), false);
    }
  }

  // test the max height method
  void testMaxHeight(Tester t) {
    Cell c1 = new Cell(2.0, 1, 1);
    Cell c2 = new Cell(8.0, 2, 2);
    Cell c3 = new Cell(9.0, 2, 2);
    Cell c4 = new Cell(1.0, 1, 1);
    Cell c5 = new Cell(2.0, 0, 0);

    IList<Cell> mt = new MtList();
    IList<Cell> l1 = new ConsList(c1, mt);
    IList<Cell> l2 = new ConsList(c2, l1);
    IList<Cell> l3 = new ConsList(c3, l2);
    IList<Cell> l4 = new ConsList(c4, l3);
    IList<Cell> l5 = new ConsList(c5, l4);

    t.checkExpect(l5.maxHeight(), c3);
    // t.checkException("No max height of an empty list!", MtList, "maxHeight", mt);
  }
  
  // TODO: Test random cell
  void testRandomCell(Tester t) {
    
  }

  // -----------------------Test Function objects ------------------------------

  // test the CanFlood function object
  void testCanFlood(Tester t) {
    IPred<Cell> p = new CanFlood();
    Cell c = new Cell(1.0, 1, 1);
    Cell o1 = new OceanCell(1.0, 2, 2);
    t.checkExpect(p.apply(c), false);
    t.checkExpect(p.apply(o1), false);
    c.makeNeighbor(o1);
    t.checkExpect(p.apply(c), true);

  }

  // test the OceanNeighbor function object
  void testOceanNeighbor(Tester t) {
    this.initTestMountain();
    IPred<Cell> p = new WaterNeighbor();
    Cell c = new Cell(55.0, 1, 1);
    Cell o1 = new OceanCell(1.0, 1, 1);
    t.checkExpect(p.apply(o1), true);
    t.checkExpect(p.apply(c), false);
  }

  // test the NotFlooded function object
  void testNotFlooded(Tester t) {
    this.initTestMountain();
    IPred<Cell> p = new NotFlooded();
    Cell c = new Cell(1.0, 1, 1);
    Cell o1 = new OceanCell(1.0, 1, 1);
    t.checkExpect(p.apply(o1), false);
    t.checkExpect(p.apply(c), true);
  }

}
