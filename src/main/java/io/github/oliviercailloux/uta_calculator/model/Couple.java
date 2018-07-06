package io.github.oliviercailloux.uta_calculator.model;


import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Couple {

		
	private Criterion left;
	private Criterion right;
	
	
	/*****************************
	 * 							 *
	 * 		Constructors         *
	 *                           *
	 *****************************/
		
	public Couple(Criterion l, Criterion r) {
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
	

	public Criterion getLeft() { 			return this.left; 	}
	
	public Criterion getRight() { 			return this.right; 	}
	
	public void setLeft(Criterion l) {		this.left = l; 		}
	
	public void setRight(Criterion r) {		this.right = r;		}
	
	
	/*****************************
	 * 							 *
	 * 		   Methodes          *
	 *                           *
	 *****************************/

	public String toString() { 	return "( " + this.left.getName() + ", " + this.right.getName() + " )";  }
}
