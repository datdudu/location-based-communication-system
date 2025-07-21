package com.locationBasedCommunicationSystem;

import com.locationBasedCommunicationSystem.ui.StartupScreen;
import com.locationBasedCommunicationSystem.ui.UserInterface;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			StartupScreen startup = new StartupScreen(user -> {
				new UserInterface(user).setVisible(true);
			});
			startup.setVisible(true);
		});
	}
}