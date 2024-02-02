package fr.frozenia.shop.utils;

public class Calcul
{

	private double 	price;
	private int 	numberSoldToday;
	private int 	numberSoldYesterday;
	private int 	numberSoldTwoDays;
	private double 	priceMinimum;
	private double 	priceMaximum;
	
	
	
	/**
	 * @param price 				= item configuration
	 * @param numberSoldToday 		= data configuration
	 * @param numberSoldYesterday 	= data configuration
	 * @param numberSoldTwoDays 	= data configuration
	 * @param priceMinimum 			= item configuration
	 * @param priceMaximum 			= item configuration
	 */
	public Calcul(double price, int numberSoldToday, int numberSoldYesterday, int numberSoldTwoDays, double priceMinimum, double priceMaximum)
	{
		this.price 					= price;
		this.numberSoldToday 		= numberSoldToday;
		this.numberSoldYesterday 	= numberSoldYesterday;
		this.numberSoldTwoDays 		= numberSoldTwoDays;
		this.priceMinimum 			= priceMinimum;
		this.priceMaximum 			= priceMaximum;
	}
	
	
	public double calculePrice()
	{
		if (this.numberSoldTwoDays <= 0) return this.price;
		if (this.numberSoldToday <= 0) this.numberSoldToday = 1;
		this.price = this.price + ((this.numberSoldTwoDays - this.numberSoldYesterday) / this.numberSoldTwoDays) * this.price;
		if (price > this.priceMaximum) this.price = this.priceMaximum;
		if (price < this.priceMinimum) this.price = this.priceMinimum;
		this.numberSoldYesterday = this.numberSoldToday; 
		this.numberSoldToday = 0;
		return this.price;
	}
}
