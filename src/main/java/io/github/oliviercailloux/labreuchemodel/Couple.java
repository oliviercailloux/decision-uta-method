package io.github.oliviercailloux.labreuchemodel;

public class Couple<L,R> {

		
	private L left;
	private R right;
	
	
	/*****************************
	 * 							 *
	 * 		Constructors         *
	 *                           *
	 *****************************/
		
	public Couple(L l, R r) {
			this.left = l;
			this.right = r;
	}
	
	public Couple() {
		this.right = null;
		this.left = null;
	}
	
	/*****************************
	 * 							 *
	 *	   Getters & Setters     *
	 *                           *
	 *****************************/
	

	public L getLeft() { 			return this.left; 	}
	
	public R getRight() { 			return this.right; 	}
	
	public void setLeft(L l) {		this.left = l; 		}
	
	public void setRight(R r) {		this.right = r;		}
	
	
	/*****************************
	 * 							 *
	 * 		   Methodes          *
	 *                           *
	 *****************************/

	@Override
	public String toString() { 
		return "( " + this.left.toString() + ", " + this.right.toString() + " )";  }
}
