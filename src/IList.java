import java.util.ArrayList;
import java.util.Iterator;

// an interface to make a list of Cells
interface IList<Cell> extends Iterable<Cell> {

	// add the cell to this list of cells
	IList<Cell> add(Cell c);

	// return the length of this list
	int length();

	// is this a conslist (for iterator)
	boolean isCons();

	// safe cast this IList to a conslist
	ConsList asCons();

	// does this list contain this item?
	boolean contains(Cell c);

	// return a list of cells that form the coastline
	// TODO : implement a filter method?
	IList<Cell> coastline();

	// TODO : test
	// flood all the cells in the given list
	IList<Cell> flood();

	
}

// a class to represent a list of cells
class MtList implements IList<Cell> {

	// add the given cell to this mt list
	public IList<Cell> add(Cell c) {
		return new ConsList(c, this);
	}

	// the length of this mt list
	public int length() {
		return 0;
	}

	// an iterator for the mtlist
	public Iterator<Cell> iterator() {
		return new IListIterator(this);
	}

	// this isn't a conslist
	public boolean isCons() {
		return false;
	}

	// we can't cast an mtlist as a conslist
	public ConsList asCons() {
		throw new ClassCastException("Can't cast an MtList as a ConsList");
	}

	// the empty list doesn't contain this item
	public boolean contains(Cell t) {
		return false;
	}

	// find the coastline cells in this mtlist
	public IList<Cell> coastline() {
		return this;
	}

  // we don't need to flood anything in the mt list
  public IList<Cell> flood() {
    return this;
  }

}

// a class to represent a non empty list of cells
class ConsList implements IList<Cell> {

	Cell first;
	IList<Cell> rest;

	ConsList(Cell first, IList<Cell> rest) {
		this.first = first;
		this.rest = rest;
	}

	// add the cell to this list
	public IList<Cell> add(Cell c) {
		return new ConsList(c, this);
	}

	// return the length of this list
	public int length() {
		return 1 + this.rest.length();
	}

	// create an iterator for this Conslist
	public Iterator<Cell> iterator() {
		return new IListIterator(this);
	}

	// tell the iterator this is a conslist
	public boolean isCons() {
		return true;
	}

	// cast this list as a conslist
	public ConsList asCons() {
		return this;
	}

	// does this conslist contain the given item?
	public boolean contains(Cell t) {
		return this.first.equals(t) || this.rest.contains(t);
	}

	// filter this list return only cells that are on the coastline/will be flooded
	// on the next tick
	// TODO: implement this method
	public IList<Cell> coastline() {
	   IList<Cell> result = new MtList();
	   IPred<Cell> p = new CanFlood();
	   for (Cell c : this) {
	     if (p.apply(c)) {
	       result = result.add(c);
	     }
	   }
	   return result;
	   
	}

  // TODO IMPLEMENT 
	// lower the height of each cell in this list by the floodrate 
  public IList<Cell> flood() {
    IList<Cell> result = this.coastline();
    for (Cell c : result) { 
      c.height = c.height - ForbiddenIslandWorld.FLOOD_RATE;
      c.isFlooded = true;
    }
    return result;
  }

}

class IListIterator implements Iterator<Cell> {

	IList<Cell> items;

	IListIterator(IList<Cell> items) {
		this.items = items;
	}

	// does this IList have a next
	public boolean hasNext() {
		return this.items.isCons();
	}

	// get the next item
	public Cell next() {
		ConsList temp = items.asCons();
		Cell result = temp.first;
		items = temp.rest;
		return result;
	}

	// don't use this
	public void remove() {
		throw new UnsupportedOperationException("Method not implemented. Don't use it!");
	}

}

// Predicate interface using a "type parameter"
interface IPred<T> {
	boolean apply(T t);
}

// a class to check if a cell has an Ocean cell neighbor
class CanFlood implements IPred<Cell> {
  
  IPred<Cell> oceanNeighbor, notFlooded;
  
  CanFlood(){
    this.oceanNeighbor = new OceanNeighbor();
    this.notFlooded = new NotFlooded();
  }
  // can this cell be flooded? 
	// does this cell have an ocean cell neighbor?
	public boolean apply(Cell c) {
		return this.oceanNeighbor.apply(c) && this.notFlooded.apply(c);
	}
}

// does this cell have any ocean cell neighbors
class OceanNeighbor implements IPred<Cell> {
  
  public boolean apply(Cell c) {
    return c.top.isOcean() || c.right.isOcean() || c.bottom.isOcean() || c.left.isOcean();
  }
}

// is this cell not flooded? 
class NotFlooded implements IPred<Cell> {
  public boolean apply(Cell c) {
    return !c.isFlooded;
  }
}

// a class of utility functions
class Utils<T> {

	// flatten an 2d array list
	ArrayList<T> flatten(ArrayList<ArrayList<T>> nested) {
		ArrayList<T> result = new ArrayList<T>();
		for (ArrayList<T> list : nested) {
			for (T item : list) {
				result.add(item);
			}
		}
		return result;
	}
}