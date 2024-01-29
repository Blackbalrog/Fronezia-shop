package fr.frozenia.shop.utils;

public class Calcul
{

	/**
	 * 
	 * @param prixActuel = prix fixer
	 * @param vendusN = item vendu aujourd'hui
	 * @param vendusNMinus1 = item vendu hier
	 * @param minVente = prix minimum fixer
	 * @param maxVente prix maximum fixer
	 * @return
	 */
	public static double setNewPrix(double prixActuel, int vendusN, int vendusNMinus1, double minVente, double maxVente)
	{
		double nouveauPrix = calculerNouveauPrix(prixActuel, vendusN, vendusNMinus1);
		nouveauPrix = ajusterPrixMinMax(nouveauPrix, minVente, maxVente);
		return nouveauPrix;
	}
	
	// Fonction pour calculer le nouveau prix en fonction de la formule donnée
	private static double calculerNouveauPrix(double prixActuel, int vendusN, int vendusNMinus1)
	{
		if (vendusNMinus1 == 0)
		{
			return prixActuel;
		}
		return prixActuel + ((vendusNMinus1 - vendusN) / (double) vendusNMinus1) * prixActuel;
	}

	// Fonction pour ajuster le prix entre min_vente et max_vente
	private static double ajusterPrixMinMax(double prix, double minVente, double maxVente)
	{
		if (prix > maxVente)
		{
			return maxVente;
		}
		else if (prix < minVente)
		{
			return minVente;
		}
		else
		{
			return prix;
		}
	}
}
