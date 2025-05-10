package model;

public class Article {
    private int id;
    private String nom;
    private String description;
    private double prix;
    private TypeArticle type;
    private String imagePath;

    public Article(int id, String nom, String description, double prix, TypeArticle type, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.type = type;
        this.imagePath = imagePath;
    }

    public Article(String nom, String description, double prix, TypeArticle type, String imagePath) {
        this(-1, nom, description, prix, type, imagePath);
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public TypeArticle getType() { return type; }
    public void setType(TypeArticle type) { this.type = type; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

}
