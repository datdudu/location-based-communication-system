package com.locationBasedCommunicationSystem.ui;

import com.locationBasedCommunicationSystem.communication.SyncCommunicationClient;
import com.locationBasedCommunicationSystem.communication.SyncCommunicationServer;
import com.locationBasedCommunicationSystem.middleware.RabbitMQHandler;
import com.locationBasedCommunicationSystem.model.Contact;
import com.locationBasedCommunicationSystem.model.Location;
import com.locationBasedCommunicationSystem.model.User;
import com.locationBasedCommunicationSystem.service.UserService;
import com.locationBasedCommunicationSystem.util.DistanceCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UserInterface extends JFrame {
    private JTextField latitudeField, longitudeField, radiusField, contactField, messageField;
    private JTextArea logArea;
    private JCheckBox onlineCheckBox;
    private User user;
    private UserService userService;
    private RabbitMQHandler rabbitHandler;
    private SyncCommunicationServer syncServer;

    private DefaultListModel<String> contactListModel;
    private JList<String> contactList;
    private Map<String, Boolean> contactInRadiusMap = new HashMap<>();

    public UserInterface(User user) {
        this.user = user;
        this.userService = new UserService(user);
        this.rabbitHandler = new RabbitMQHandler();

        // Mostra o nome do usuário no título da janela
        setTitle("Comunicação por Localização - " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Painel de dados do usuário
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Seus Dados"));
        GridBagConstraints upGbc = new GridBagConstraints();
        upGbc.insets = new Insets(2, 2, 2, 2);
        upGbc.fill = GridBagConstraints.HORIZONTAL;
        upGbc.weightx = 1;

        upGbc.gridx = 0; upGbc.gridy = 0;
        userPanel.add(new JLabel("Latitude:"), upGbc);
        upGbc.gridx = 1;
        latitudeField = new JTextField(String.valueOf(user.getLocation().getLatitude()), 10);
        userPanel.add(latitudeField, upGbc);

        upGbc.gridx = 2;
        userPanel.add(new JLabel("Longitude:"), upGbc);
        upGbc.gridx = 3;
        longitudeField = new JTextField(String.valueOf(user.getLocation().getLongitude()), 10);
        userPanel.add(longitudeField, upGbc);

        upGbc.gridx = 0; upGbc.gridy = 1;
        userPanel.add(new JLabel("Raio (m):"), upGbc);
        upGbc.gridx = 1;
        radiusField = new JTextField(String.valueOf(user.getCommunicationRadius()), 10);
        userPanel.add(radiusField, upGbc);

        upGbc.gridx = 2;
        onlineCheckBox = new JCheckBox("Online", user.isOnline());
        userPanel.add(onlineCheckBox, upGbc);

        upGbc.gridx = 3;
        JButton updateBtn = new JButton("Atualizar");
        userPanel.add(updateBtn, upGbc);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(userPanel, gbc);

        // Lista de contatos
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setVisibleRowCount(10);
        contactList.setFixedCellWidth(260);
        JScrollPane contactScroll = new JScrollPane(contactList);
        contactScroll.setBorder(BorderFactory.createTitledBorder("Contatos"));

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 3;
        mainPanel.add(contactScroll, gbc);
        gbc.gridheight = 1;

        // Área de chat
        logArea = new JTextArea(10, 36);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(logArea);
        chatScroll.setBorder(BorderFactory.createTitledBorder("Chat"));

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(chatScroll, gbc);

        // Painel de envio de mensagem
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(BorderFactory.createTitledBorder("Enviar Mensagem"));
        GridBagConstraints spGbc = new GridBagConstraints();
        spGbc.insets = new Insets(2, 2, 2, 2);
        spGbc.fill = GridBagConstraints.HORIZONTAL;
        spGbc.weightx = 1;

        spGbc.gridx = 0; spGbc.gridy = 0;
        sendPanel.add(new JLabel("Contato:"), spGbc);
        spGbc.gridx = 1;
        contactField = new JTextField();
        contactField.setPreferredSize(new Dimension(100, 25));
        sendPanel.add(contactField, spGbc);

        spGbc.gridx = 0; spGbc.gridy = 1;
        sendPanel.add(new JLabel("Mensagem:"), spGbc);
        spGbc.gridx = 1;
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(250, 25));
        sendPanel.add(messageField, spGbc);

        spGbc.gridx = 2; spGbc.gridy = 0; spGbc.gridheight = 2;
        spGbc.fill = GridBagConstraints.BOTH;
        JButton sendMessageBtn = new JButton("Enviar");
        sendPanel.add(sendMessageBtn, spGbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        mainPanel.add(sendPanel, gbc);

        // Botões de adicionar/remover contato
        JButton addContactBtn = new JButton("Adicionar Contato");
        JButton removeContactBtn = new JButton("Remover Contato");
        JPanel contactBtnPanel = new JPanel();
        contactBtnPanel.add(addContactBtn);
        contactBtnPanel.add(removeContactBtn);

        gbc.gridx = 2; gbc.gridy = 3;
        mainPanel.add(contactBtnPanel, gbc);

        add(mainPanel);

        // Iniciar servidor síncrono para receber mensagens
        try {
            syncServer = new SyncCommunicationServer(user, msg -> {
                SwingUtilities.invokeLater(() -> {
                    if (msg.startsWith("UPDATE;")) {
                        handleContactUpdate(msg);
                    } else if (msg.startsWith("REQUEST_UPDATE;")) {
                        handleRequestUpdate(msg);
                    } else {
                        // Exibe o nome do remetente se vier no formato "NOME: mensagem"
                        String displayMsg = msg;
                        if (msg.contains(":")) {
                            displayMsg = "Recebido de " + msg.substring(0, msg.indexOf(':')).trim() + ": " + msg.substring(msg.indexOf(':') + 1).trim();
                        }
                        logArea.append(displayMsg + "\n");
                    }
                });
            });
            syncServer.start();
        } catch (Exception e) {
            logArea.append("Erro ao iniciar servidor síncrono: " + e.getMessage() + "\n");
        }

        // Iniciar consumo de mensagens assíncronas apenas se o usuário já estiver online
        if (user.isOnline()) {
            startRabbitConsumer();
        }

        // Inicializa o mapa de estado de raio
        for (Contact c : user.getContacts().values()) {
            boolean inRadius = DistanceCalculator.isWithinRadius(user.getLocation(), c.getLocation(), user.getCommunicationRadius());
            contactInRadiusMap.put(c.getName(), inRadius);
        }
        updateContactList();

        // Listeners
        updateBtn.addActionListener(e -> performUpdate());

        sendMessageBtn.addActionListener(e -> {
            String contato = contactField.getText().trim();
            String mensagem = messageField.getText().trim();

            Contact c = user.getContacts().get(contato);
            if (c == null) {
                logArea.append("Contato não encontrado!\n");
                return;
            }

            boolean dentroDoRaio = DistanceCalculator.isWithinRadius(
                    user.getLocation(), c.getLocation(), user.getCommunicationRadius());
            boolean online = c.isOnline();

            // Sempre envia no formato "NOME: mensagem"
            String msgToSend = user.getName() + ": " + mensagem;

            if (dentroDoRaio && online) {
                boolean enviado = SyncCommunicationClient.sendMessage(c.getIpAddress(), c.getPort(), msgToSend);
                if (enviado) {
                    logArea.append("Você (síncrono) para " + contato + ": " + mensagem + "\n");
                } else {
                    logArea.append("Falha ao enviar mensagem síncrona.\n");
                }
            } else {
                rabbitHandler.sendMessage("user_" + contato, msgToSend);
                logArea.append("Você (assíncrono) para " + contato + ": " + mensagem + "\n");
            }
            messageField.setText("");
        });

        addContactBtn.addActionListener(e -> {
            JTextField nome = new JTextField();
            JTextField ip = new JTextField();
            JTextField porta = new JTextField();
            JPanel panel = new JPanel(new GridLayout(3,2));
            panel.add(new JLabel("Nome:")); panel.add(nome);
            panel.add(new JLabel("IP:")); panel.add(ip);
            panel.add(new JLabel("Porta:")); panel.add(porta);
            int result = JOptionPane.showConfirmDialog(this, panel, "Novo Contato", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Contact c = new Contact(nome.getText(), new Location(0, 0), false, Integer.parseInt(porta.getText()), ip.getText());
                user.addContact(c);
                contactInRadiusMap.put(c.getName(), false);
                updateContactList();
                logArea.append("Contato " + c.getName() + " adicionado.\n");

                String requestMsg = "REQUEST_UPDATE;" + user.getName() + ";" + user.getLocation().getLatitude() + ";" + user.getLocation().getLongitude() + ";" + user.getPort() + ";" + "127.0.0.1"; // ou IP real
                SyncCommunicationClient.sendMessage(c.getIpAddress(), c.getPort(), requestMsg);
            }
        });

        removeContactBtn.addActionListener(e -> {
            String selected = contactList.getSelectedValue();
            if (selected != null) {
                String name = selected.split(" \\| ")[0];
                user.getContacts().remove(name);
                contactInRadiusMap.remove(name);
                updateContactList();
                logArea.append("Contato " + name + " removido.\n");
            }
        });

        performUpdate();
    }

    private void updateContactList() {
        contactListModel.clear();
        for (Contact c : user.getContacts().values()) {
            double dist = DistanceCalculator.calculateDistance(user.getLocation(), c.getLocation());
            String status = c.isOnline() ? "Online" : "Offline";
            String inRadius = DistanceCalculator.isWithinRadius(user.getLocation(), c.getLocation(), user.getCommunicationRadius()) ? "No raio" : "Fora do raio";
            contactListModel.addElement(c.getName() + " | " + status + " | " + String.format("%.1f m", dist) + " | " + inRadius);
        }
    }

    private void handleContactUpdate(String msg) {
        try {
            String[] parts = msg.split(";");
            if (parts.length < 7) return;
            String nome = parts[1];
            boolean online = Boolean.parseBoolean(parts[2]);
            double lat = Double.parseDouble(parts[3]);
            double lon = Double.parseDouble(parts[4]);
            int porta = Integer.parseInt(parts[5]);
            String ip = parts[6];

            Contact c = user.getContacts().get(nome);
            if (c == null) {
                c = new Contact(nome, new Location(lat, lon), online, porta, ip);
                user.addContact(c);
                logArea.append("Contato " + nome + " adicionado automaticamente ao receber UPDATE.\n");
            }
            c.setOnline(online);
            c.setLocation(new Location(lat, lon));
            c.setPort(porta);
            c.setIpAddress(ip);

            boolean prev = contactInRadiusMap.getOrDefault(c.getName(), false);
            boolean now = DistanceCalculator.isWithinRadius(user.getLocation(), c.getLocation(), user.getCommunicationRadius());
            if (prev != now) {
                if (now) {
                    logArea.append("Contato " + c.getName() + " entrou no raio!\n");
                } else {
                    logArea.append("Contato " + c.getName() + " saiu do raio!\n");
                }
                contactInRadiusMap.put(c.getName(), now);
            }
            updateContactList();
        } catch (Exception ex) {
            logArea.append("Erro ao processar atualização de contato.\n");
        }
    }

    private void handleRequestUpdate(String msg) {
        try {
            String[] parts = msg.split(";");
            if (parts.length < 6) return;
            String nomeSolicitante = parts[1];
            double lat = Double.parseDouble(parts[2]);
            double lon = Double.parseDouble(parts[3]);
            int porta = Integer.parseInt(parts[4]);
            String ip = parts[5];

            if (!user.getContacts().containsKey(nomeSolicitante)) {
                Contact novoContato = new Contact(nomeSolicitante, new Location(lat, lon), true, porta, ip);
                user.addContact(novoContato);
                contactInRadiusMap.put(nomeSolicitante, DistanceCalculator.isWithinRadius(user.getLocation(), novoContato.getLocation(), user.getCommunicationRadius()));
                updateContactList();
                logArea.append("Contato " + nomeSolicitante + " adicionado automaticamente ao receber solicitação.\n");
            }

            String updateMsg = "UPDATE;" + user.getName() + ";" + user.isOnline() + ";" +
                    user.getLocation().getLatitude() + ";" + user.getLocation().getLongitude() + ";" +
                    user.getPort() + ";" + "127.0.0.1"; // ou IP real
            SyncCommunicationClient.sendMessage(ip, porta, updateMsg);
        } catch (Exception ex) {
            logArea.append("Erro ao processar REQUEST_UPDATE.\n");
        }
    }

    private void startRabbitConsumer() {
        rabbitHandler.startConsuming("user_" + user.getName(), msg -> {
            SwingUtilities.invokeLater(() -> {
                // Exibe o nome do remetente se vier no formato "NOME: mensagem"
                String displayMsg = msg;
                if (msg.contains(":")) {
                    displayMsg = "Recebido de " + msg.substring(0, msg.indexOf(':')).trim() + ": " + msg.substring(msg.indexOf(':') + 1).trim();
                }
                logArea.append(displayMsg + "\n");
            });
        });
    }

    private void stopRabbitConsumer() {
        rabbitHandler.stopConsuming();
    }

    private void performUpdate() {
        double lat = Double.parseDouble(latitudeField.getText());
        double lon = Double.parseDouble(longitudeField.getText());
        double raio = Double.parseDouble(radiusField.getText());
        boolean online = onlineCheckBox.isSelected();

        boolean wasOnline = user.isOnline();
        userService.updateLocation(new Location(lat, lon));
        userService.updateRadius(raio);
        userService.updateStatus(online);

        // Notificações de entrada/saída do raio
        for (Contact c : user.getContacts().values()) {
            boolean prev = contactInRadiusMap.getOrDefault(c.getName(), false);
            boolean now = DistanceCalculator.isWithinRadius(user.getLocation(), c.getLocation(), user.getCommunicationRadius());
            if (prev != now) {
                if (now) {
                    logArea.append("Contato " + c.getName() + " entrou no raio!\n");
                } else {
                    logArea.append("Contato " + c.getName() + " saiu do raio!\n");
                }
                contactInRadiusMap.put(c.getName(), now);
            }
        }

        updateContactList();

        // Envia atualização para todos os contatos online
        for (Contact c : user.getContacts().values()) {
            if (c.isOnline()) {
                String updateMsg = "UPDATE;" + user.getName() + ";" + user.isOnline() + ";" +
                        user.getLocation().getLatitude() + ";" + user.getLocation().getLongitude() + ";" +
                        user.getPort() + ";" + "127.0.0.1"; // ou IP real
                SyncCommunicationClient.sendMessage(c.getIpAddress(), c.getPort(), updateMsg);
            }
        }

        if (online && !wasOnline) {
            startRabbitConsumer();
            logArea.append("Você está ONLINE. Mensagens assíncronas serão entregues.\n");
        } else if (!online && wasOnline) {
            stopRabbitConsumer();
            logArea.append("Você está OFFLINE. Mensagens assíncronas serão armazenadas.\n");
        } else {
            logArea.append("Dados atualizados!\n");
        }
    }
}