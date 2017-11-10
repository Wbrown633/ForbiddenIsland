import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class ForbiddenIslandWorld extends World {

  // Defines an int constant
  static final int ISLAND_SIZE = 65;
  // define the height of the island
  static final int ISLAND_HEIGHT = ForbiddenIslandWorld.ISLAND_SIZE / 2;
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
  // cell size
  static final int CELL_SIZE = 10;

  ForbiddenIslandWorld() {
    this.heights = new ArrayList<ArrayList<Double>>();
    this.board = new MtList<Cell>();
    this.waterHeight = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    this.cells = new ArrayList<ArrayList<Cell>>(ForbiddenIslandWorld.ISLAND_SIZE);
  }

  // Render the game
  // EFFECT: update the stored image for the
  public WorldScene makeScene() {
    for (Cell c : this.board) {
      scene.placeImageXY(c.drawAt(this.image), c.x * ForbiddenIslandWorld.CELL_SIZE,
          c.y * ForbiddenIslandWorld.CELL_SIZE);
    }
    return scene;
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

  }

  // Effect: create the list of cells for the mountain style of island
  void initCellsMountain() {
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
  }

  // Effect: create the list of cells for the random style of island
  void initCellsRandom() {
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
  }

  // Effect: create an IList<Cell> from the given nested Array list of cells
  IList<Cell> listCells(ArrayList<ArrayList<Cell>> cells) {
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
    int x = Math.abs((int) this.height * 255 / ForbiddenIslandWorld.ISLAND_HEIGHT);
    Color c = new Color(x/2, x/2 + 128, x/2);
    return c;
  }
}

class OceanCell extends Cell {

  OceanCell(Double height, int x, int y) {
    super(height, x, y);
    this.isFlooded = true;
  }

  // Draws this tile onto the background at the specified logical coordinates
  WorldImage drawAt(WorldImage background) {
    return new OverlayImage(new RectangleImage(ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID, Color.BLUE), background);
  }

}

class ExamplesForbidden {
  ForbiddenIslandWorld ex1;

  Cell c1 = new Cell(2.0, 1, 1);
  Cell c2 = new Cell(4.0, 2, 2);
  Cell c3 = new Cell(5.0, 1, 5);
  MtList<Cell> mt = new MtList<Cell>();

  IList<Cell> l1 = new ConsList<Cell>(c1, mt);
  IList<Cell> l2 = new ConsList<Cell>(c2, l1);

  // run the game
  void testGame(Tester t) {
    this.initTest();
    this.ex1.listHeightsMountain();
    // this.ex1.initCellsRandom(); // RENDERS THE RANDOM MOUNTAIN
    this.ex1.initCellsMountain(); // RENDERS THE REGULAR MOUNTAIN
    ex1.bigBang(ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE, 0);
  }

  // initialize the test conditions
  void initTest() {
    ex1 = new ForbiddenIslandWorld();
  }

  // test the mountain island height creation
  void testListHeights(Tester t) {
    this.initTest();
    ex1.listHeightsMountain();
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
    this.ex1.listHeightsMountain();
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
  }

  // test the link Cells method
  void testLinkCells(Tester t) {
    this.initTest();
    ex1.listHeightsMountain();
    ex1.initCellsMountain();
    t.checkFail(this.ex1.cells.get(0).get(0).right, this.ex1.cells.get(0).get(0));

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
    IListIterator<Cell> iterator = new IListIterator<Cell>(this.l2);
    t.checkExpect(iterator.hasNext(), true);
    t.checkExpect(iterator.next(), this.c2);
    t.checkExpect(iterator.hasNext(), true);
    t.checkExpect(iterator.next(), this.c1);
    t.checkExpect(iterator.hasNext(), false);
  }

}
