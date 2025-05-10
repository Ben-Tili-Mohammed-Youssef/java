package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConsulterClients extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ConsulterClients() {
        setTitle("Liste des Clients");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nom", "Prénom", "Email", "Téléphone"});

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        chargerClients();
    }

    private void chargerClients() {
        ClientDAO clientDAO = new ClientDAO();
        List<Client> clients = clientDAO.listerTousLesClients();
        for (Client c : clients) {
            model.addRow(new Object[]{
                    c.getId(),
                    c.getNom(),
                    c.getPrenom(),
                    c.getEmail(),
                    c.getTelephone()
            });
        }
    }
}
