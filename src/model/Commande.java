package model;

public class Commande {
    private int id;
    private int clientId;
    private String etat;
    private String modeRecuperation;
    private String adresseLivraison;
    private java.sql.Timestamp dateCommande;

    public Commande(int id, int clientId, String etat, String modeRecuperation, String adresseLivraison, java.sql.Timestamp dateCommande) {
        this.id = id;
        this.clientId = clientId;
        this.etat = etat;
        this.modeRecuperation = modeRecuperation;
        this.adresseLivraison = adresseLivraison;
        this.dateCommande = dateCommande;
    }

    // Getters et Setters
    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public String getEtat() { return etat; }
    public String getModeRecuperation() { return modeRecuperation; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public java.sql.Timestamp getDateCommande() { return dateCommande; }

    public void setEtat(String etat) { this.etat = etat; }
}
