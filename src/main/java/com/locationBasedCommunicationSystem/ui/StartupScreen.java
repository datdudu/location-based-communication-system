package com.locationBasedCommunicationSystem.ui;

import com.locationBasedCommunicationSystem.model.*;
import javax.swing.*;
import java.awt.*;

public class StartupScreen extends JFrame {
    private JTextField nomeField, portaField, contatoNomeField, contatoIPField, contatoPortaField;

    public interface StartupListener {
        void onStart(User user);
    }

    public StartupScreen(StartupListener listener) {
        setTitle("Configuração do Usuário");
        setSize(350, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Seu nome:"));
        nomeField = new JTextField("Alice");
        panel.add(nomeField);

        panel.add(new JLabel("Sua porta:"));
        portaField = new JTextField("5000");
        panel.add(portaField);

        panel.add(new JLabel("Nome do contato:"));
        contatoNomeField = new JTextField("Bob");
        panel.add(contatoNomeField);

        panel.add(new JLabel("IP do contato:"));
        contatoIPField = new JTextField("127.0.0.1");
        panel.add(contatoIPField);

        panel.add(new JLabel("Porta do contato:"));
        contatoPortaField = new JTextField("5001");
        panel.add(contatoPortaField);

        JButton okButton = new JButton("OK");
        panel.add(new JLabel());
        panel.add(okButton);

        add(panel);

        okButton.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            int porta = Integer.parseInt(portaField.getText().trim());
            String contatoNome = contatoNomeField.getText().trim();
            String contatoIP = contatoIPField.getText().trim();
            int contatoPorta = Integer.parseInt(contatoPortaField.getText().trim());

            User user = new User(nome, new Location(-3.7, -38.5), true, 30000, porta);
            user.addContact(new Contact(contatoNome, new Location(-3.71, -38.52), true, contatoPorta, contatoIP));

            listener.onStart(user);
            dispose();
        });
    }
}