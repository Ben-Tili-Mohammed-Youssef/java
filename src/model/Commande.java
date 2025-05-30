package model;

import java.sql.Timestamp;

public class Commande {
    private int id;
    private int clientId;
    private String etat;
    private String modeRecuperation;
    private String adresseLivraison;
    private java.sql.Timestamp dateCommande;
    private double total;

    public Commande(int id, int clientId, String etat, String modeRecuperation, String adresseLivraison, Timestamp dateCommande, double total) {
        this.id = id;
        this.clientId = clientId;
        this.etat = etat;
        this.modeRecuperation = modeRecuperation;
        this.adresseLivraison = adresseLivraison;
        this.dateCommande = dateCommande;
        this.total = total;
    }

    public Commande(int id, int clientId, String etat, String modeRecuperation,
                    String adresseLivraison, Timestamp dateCommande) {
        this(id, clientId, etat, modeRecuperation, adresseLivraison, dateCommande, 0.0);
    }
    // Getters et Setters
    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public String getEtat() { return etat; }
    public String getModeRecuperation() { return modeRecuperation; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public java.sql.Timestamp getDateCommande() { return dateCommande; }

    public void setEtat(String etat) { this.etat = etat; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
