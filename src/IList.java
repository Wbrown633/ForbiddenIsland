import java.util.ArrayList;
import java.util.Iterator;

// an interface to make a list of Cells
interface IList<T> extends Iterable<T> {

	// add the cell to this list of cells
	IList<T> add(T t);

	// return the length of this list
	int length();

	// is this a conslist (for iterator)
	boolean isCons();

	// safe cast this IList to a conslist
	ConsList<T> asCons();

	// does this list contain this item?
	boolean contains(T t);

	// filter out all items that don't pass the pred
	IList<T> filter(IPred<T> p);

	// return a list of cells that form the coastline
	// TODO : implement a filter method?
	IList<Cell> coastline();

	// TODO : test
	// flood all the cells in the given list
	IList<Cell> flood(IList<Cell> list);

	
}

// a class to represent a list of cells
class MtList<T> implements IList<T> {

	// add the given cell to this mt list
	public IList<T> add(T t) {
		return new ConsList<T>(t, this);
	}

	// the length of this mt list
	public int length() {
		return 0;
	}

	// an iterator for the mtlist
	public Iterator<T> iterator() {
		return new IListIterator<T>(this);
	}

	// this isn't a conslist
	public boolean isCons() {
		return false;
	}

	// we can't cast an mtlist as a conslist
	public ConsList<T> asCons() {
		throw new ClassCastException("Can't cast an MtList as a ConsList");
	}

	// the empty list doesn't contain this item
	public boolean contains(T t) {
		return false;
	}

	// find the coastline cells in this mtlist
	public IList<Cell> coastline() {
		return new MtList<Cell>();
	}

	// filter the mt list
	public IList<T> filter(IPred<T> p) {
		return this;
	}

}

// a class to represent a non empty list of cells
class ConsList<T> implements IList<T> {

	T first;
	IList<T> rest;

	ConsList(T first, IList<T> rest) {
		this.first = first;
		this.rest = rest;
	}

	// add the cell to this list
	public IList<T> add(T t) {
		return new ConsList<T>(t, this);
	}

	// return the length of this list
	public int length() {
		return 1 + this.rest.length();
	}

	// create an iterator for this Conslist
	public Iterator<T> iterator() {
		return new IListIterator<T>(this);
	}

	// tell the iterator this is a conslist
	public boolean isCons() {
		return true;
	}

	// cast this list as a conslist
	public ConsList<T> asCons() {
		return this;
	}

	// does this conslist contain the given item?
	public boolean contains(T t) {
		return this.first.equals(t) || this.rest.contains(t);
	}

	// filter this list return only cells that are on the coastline/will be flooded
	// on the next tick
	// TODO: implement this method
	public IList<Cell> coastline() {
		IPred<Cell> pred = new CoastCell();
		return this.filter(pred);
	}

	// filter this list of T by the given predicate
	public IList<T> filter(IPred<T> p) {
	    if(p.apply(this.first)) {
	        return new ConsList<T>(this.first, this.rest.filter(p));
	      }
	      else {
	        return this.rest.filter(p);
	      }
	}


}

class IListIterator<T> implements Iterator<T> {

	IList<T> items;

	IListIterator(IList<T> items) {
		this.items = items;
	}

	// does this IList have a next
	public boolean hasNext() {
		return this.items.isCons();
	}

	// get the next item
	public T next() {
		ConsList<T> temp = items.asCons();
		T result = temp.first;
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
class CoastCell implements IPred<Cell> {

	// does this cell have an ocean cell neighbor?
	public boolean apply(Cell t) {
		return t.top.isOcean() || t.bottom.isOcean() || t.right.isOcean() || t.left.isOcean();
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