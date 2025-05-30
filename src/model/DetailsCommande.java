package model;

public class DetailsCommande {
    private int id;
    private int commandeId;
    private int articleId;
    private String nomArticle; // Pour afficher dans l'interface
    private int quantite;
    private double prixUnitaire;

    public DetailsCommande(int id, int commandeId, int articleId, String nomArticle, int quantite, double prixUnitaire) {
        this.id = id;
        this.commandeId = commandeId;
        this.articleId = articleId;
        this.nomArticle = nomArticle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Constructeur sans ID pour la création
    public DetailsCommande(int commandeId, int articleId, String nomArticle, int quantite, double prixUnitaire) {
        this.commandeId = commandeId;
        this.articleId = articleId;
        this.nomArticle = nomArticle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getNomArticle() {
        return nomArticle;
    }

    public void setNomArticle(String nomArticle) {
        this.nomArticle = nomArticle;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    // Méthodes utilitaires
    public double getMontantTotal() {
        return quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return nomArticle + " - " + quantite + " x " + prixUnitaire + "€ = " + getMontantTotal() + "€";
    }
}