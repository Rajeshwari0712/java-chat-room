package chatRoom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class UserThread extends Thread {
    private String name;
    private ChatRoom room;

    private JTextArea chatArea;
    private JTextField input;
    private JCheckBox emojiCheckBox;
    private JComboBox<String> colorBox;
    private DefaultListModel<String> userListModel;

    public UserThread(String name, ChatRoom room) {
        this.name = name;
        this.room = room;
        setupUI();
    }

    public String getUserName() {
        return name;
    }

    private void setupUI() {
        JFrame frame = new JFrame("Fun Chat Room - " + name);
        frame.setSize(800, 600);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.BLACK);
        chatArea.setForeground(Color.GREEN);

        JScrollPane chatScroll = new JScrollPane(chatArea);

        input = new JTextField();
        input.addActionListener(e -> sendMessage());

        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendMessage());

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> chatArea.setText(""));

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> {
            room.removeUser(this);
            frame.dispose();
        });

        // Emoji checkbox
        emojiCheckBox = new JCheckBox("Enable Emojis");

        // Color change
        colorBox = new JComboBox<>(new String[]{"Green","Blue","Red","Yellow","Cyan"});
        colorBox.addActionListener(e -> {
            switch((String)colorBox.getSelectedItem()) {
                case "Green": chatArea.setForeground(Color.GREEN); break;
                case "Blue": chatArea.setForeground(Color.BLUE); break;
                case "Red": chatArea.setForeground(Color.RED); break;
                case "Yellow": chatArea.setForeground(Color.YELLOW); break;
                case "Cyan": chatArea.setForeground(Color.CYAN); break;
            }
        });

        // User list
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);

        // Bottom panel
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(input, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(sendBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(exitBtn);

        bottom.add(btnPanel, BorderLayout.EAST);

        // Top controls
        JPanel control = new JPanel();
        control.add(emojiCheckBox);
        control.add(new JLabel("Color:"));
        control.add(colorBox);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chatScroll, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(userList), BorderLayout.EAST);
        mainPanel.add(bottom, BorderLayout.SOUTH);
        mainPanel.add(control, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Chat", mainPanel);
        tabs.add("Settings", createSettingsPanel());
        tabs.add("About", createAboutPanel());

        frame.add(tabs);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateUserList();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();

        JComboBox<Integer> fontSize = new JComboBox<>(new Integer[]{10,12,14,16,18});
        fontSize.addActionListener(e -> {
            chatArea.setFont(new Font("Monospaced", Font.PLAIN, (int)fontSize.getSelectedItem()));
        });

        JButton bgBtn = new JButton("Background Color");
        bgBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null,"Choose", chatArea.getBackground());
            if (c != null) chatArea.setBackground(c);
        });

        panel.add(new JLabel("Font Size:"));
        panel.add(fontSize);
        panel.add(bgBtn);

        return panel;
    }

    private JPanel createAboutPanel() {
        JTextArea about = new JTextArea(
            "Fun Chat Room\n\nFeatures:\n- Multi user chat\n- Emojis\n- Colors\n- Tabs"
        );
        about.setEditable(false);
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(about), BorderLayout.CENTER);
        }};
    }

    private void sendMessage() {
        String msg = input.getText().trim();
        if (!msg.isEmpty()) {

            // Emoji logic
            if (emojiCheckBox.isSelected()) {
                msg = msg.replace(":)", "😊")
                         .replace(":(", "😢")
                         .replace(":D", "😃")
                         .replace("<3", "❤️");
            }

            room.sendMessage(name, msg);
            input.setText("");
        }
    }

    public void receiveMessage(String msg) {
        chatArea.append(msg + "\n"); 
    }

    private void updateUserList() {
        userListModel.clear();
        for (String u : room.getUserNames()) {
            userListModel.addElement(u);
        }
    }

    public void run() {
        room.addUser(this);

        while (true) {
            try {
                Thread.sleep(2000);
                updateUserList(); 
            } catch (Exception e) {
                break;
            }
        }
    }
}
