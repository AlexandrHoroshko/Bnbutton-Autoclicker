package io.bnbutton;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public final class Main {

    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final long THREE_MINUTES_IN_MILLISECONDS = 180000;

    public static void main(String[] args) {

        final WebDriver[] browser = new WebDriver[1];
        final String[] mainTabId = new String[1];

        SwingUtilities.invokeLater(() -> {
            // Create a JFrame (the main window)
            JFrame frame = new JFrame("Bnbutton Auto-clicker");
            frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

            // Create a custom WindowAdapter to handle the window closing event
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    WebDriverRunner.closeWebDriver();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            });
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // Create a JPanel to hold the components
            var parentPanel = new JPanel();
            parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));

            var infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

            var buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

            var logsPanel = new JPanel();
            logsPanel.setLayout(new BoxLayout(logsPanel, BoxLayout.X_AXIS));

            // Create a JLabel to display a message
            JLabel label = new JLabel(
                    """
                            <html>
                            Browser will be opened automatically with installed metamask after clicking on 'Open Browser' button.
                            <br>Please add your wallet manually and click on 'Start' button.
                            <br>After that you can click on 'Stop' button to stop the process and run it again by clicking on 'Start' button.
                            <html>
                            """
            );

            JTextArea textArea = new JTextArea("Here you will see the logs of the process.\n\n", 20, 60);
            textArea.setEditable(false); // Make the text area read-only
            textArea.setAutoscrolls(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setAutoscrolls(true);
            scrollPane.setWheelScrollingEnabled(true);
            scrollPane.setName("Logs");

            // Redirect System.out and System.err to the JTextArea
            PrintStream printStream = new PrintStream(new TextAreaOutputStream(textArea));
            System.setOut(printStream);
            System.setErr(printStream);

            Timer countdownTimer = new Timer(1000, null);
            countdownTimer.setRepeats(true);

            final long[] millisecondsToWait = new long[1]; // Initial countdown time in milliseconds

            countdownTimer.addActionListener(e1 -> {
                if (millisecondsToWait[0] >= 0) {
                    // Get the previous text, append the countdown text, and set it back
                    String previousText = textArea.getText().replaceFirst("\nTime remaining: \\d+ seconds", ""); // Remove old countdown text
                    textArea.setText(previousText + "\nTime remaining: " + millisecondsToWait[0] / 1000 + " seconds");
                    millisecondsToWait[0] -= 1000; // millisecondsToWait - 1 second
                } else {
                    textArea.append("\nCountdown complete.");
                    countdownTimer.stop(); // Stop the timer when countdown is done
                }
            });

            // Create a JButton
            JButton openBrowserButton = new JButton("Open Browser");
            JButton startClickingButton = new JButton("Start Clicking");
            startClickingButton.setEnabled(false);
            JButton stopClickingButton = new JButton("Stop Clicking");
            stopClickingButton.setEnabled(false);

            // Add an ActionListener to the button
            openBrowserButton.addActionListener(e -> {
                final JDialog loadingDialog = new JDialog(frame, "Opening browser...");
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);

                // Create a SwingWorker to simulate the loading process
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    boolean isBrowserOpened = false;

                    @Override
                    protected Void doInBackground() {
                        isBrowserOpened = BrowserConfig.configAndOpenBrowser();
                        if (isBrowserOpened) {
                            Selenide.open("https://bnbutton.io");
                            browser[0] = WebDriverRunner.getWebDriver();
                            mainTabId[0] = WebDriverRunner.getWebDriver().getWindowHandles().toArray()[0].toString();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Browser is not opened. Please check logs and try to click on 'Open Browser' button again.");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        loadingDialog.dispose(); // Close the loading dialog when loading is complete
                        if (isBrowserOpened) {
                            JOptionPane.showMessageDialog(frame, "Please add your wallet manually and connect it for the first time, only after that click on 'Start' button.");
                            System.out.println("\nBrowser is opened");
                            startClickingButton.setEnabled(true);
                        }
                    }
                };

                worker.addPropertyChangeListener(evt -> {
                    if (evt.getPropertyName().equals("state") && evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                        loadingDialog.setSize(200, 100);
                        loadingDialog.add(progressBar, BorderLayout.CENTER);
                        loadingDialog.setLocationRelativeTo(frame);
                        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                        loadingDialog.setModal(true);
                        loadingDialog.setVisible(true);
                    }
                });

                worker.execute();
            });

            final Thread[] clickingThread = {null};
            final int[] unsuccessfulTries = {0};
            int unsuccessfulTriesLimit = 60;

            // Add an ActionListener to the button
            startClickingButton.addActionListener(e -> {
                if (clickingThread[0] == null || !clickingThread[0].isAlive()) {

                    JOptionPane.showConfirmDialog(frame, "Did you add your wallet manually and connect it for the first time?", "Please confirm", JOptionPane.YES_NO_OPTION);

                    // Start a new cycle thread
                    clickingThread[0] = new Thread(() -> {
                        if (browser[0] != null && !browser[0].toString().contains("(null)")) {
                            startClickingButton.setEnabled(false);
                            stopClickingButton.setEnabled(true);
                            WebDriverRunner.setWebDriver(browser[0]);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Browser is not opened. Please click on 'Open Browser' button first and config Metamask.");
                            return;
                        }

                        while (true) {
                            boolean isMetamaskConnectedInCurrentCycle = false;
                            boolean isClicksDone = false;
                            millisecondsToWait[0] = Helpers.RANDOM.nextLong(ONE_MINUTE_IN_MILLISECONDS, THREE_MINUTES_IN_MILLISECONDS);

                            try {
                                BrowserConfig.switchToWindow(mainTabId[0]);
                                Selenide.open("https://bnbutton.io/buy");

                                if (WebDriverRunner.getWebDriver().getCurrentUrl().equals("https://bnbutton.io/buy")) {
                                    isClicksDone = Clicker.doClicksOnAllButtons();
                                } else {
                                    BrowserConfig.closeAllWindowsExceptOfMain(mainTabId[0]);
                                    Selenide.open("https://bnbutton.io/");
                                    MetamaskConnector.connectWallet(mainTabId[0]);
                                    Selenide.open("https://bnbutton.io/buy");
                                    isMetamaskConnectedInCurrentCycle = true;
                                }
                                if (isMetamaskConnectedInCurrentCycle) {
                                    isClicksDone = Clicker.doClicksOnAllButtons();
                                }
                            } catch (WebDriverException ignored) {
                                JOptionPane.showMessageDialog(frame, "Something went wrong with browser. Please click on 'Open Browser' button and configure Metamask again.");
                                break;
                            } catch (Exception ex) {
                                System.out.println("\nSomething went wrong. Caught exception: \n");
                                ex.printStackTrace();
                            }

                            if (isClicksDone) {
                                unsuccessfulTries[0] = 0;
                                System.out.println("Clicks cycle is finished. Waiting for " + millisecondsToWait[0] / 1000 + " seconds to start a new cycle.");
                                countdownTimer.start(); // Start the countdown timer
                                Selenide.sleep(millisecondsToWait[0] + 1000);
                                textArea.setText("");
                            } else {
                                unsuccessfulTries[0]++;
                                if (unsuccessfulTries[0] >= unsuccessfulTriesLimit) {
                                    JOptionPane.showMessageDialog(frame, "Looks like there is to many unsuccessful clicks tries. Please check logs and try to click on 'Stop' and 'Start' button again.");
                                }

                                long millisecondsToWaitBeforeNextTry = Helpers.RANDOM.nextLong(5000, 10000);
                                System.out.println("Clicks cycle was not finished successfully. Waiting for " + millisecondsToWaitBeforeNextTry / 1000 + " seconds to retry cycle.");
                                Selenide.sleep(millisecondsToWaitBeforeNextTry);
                            }
                        }
                        stopClickingButton.setEnabled(false);
                    });
                    clickingThread[0].start();
                } else {
                    stopClickingButton.setEnabled(true);
                    JOptionPane.showMessageDialog(frame, "Something went wrong. Try to click on 'Stop' button and then on 'Start' button again.");
                }
            });

            // Add an ActionListener to the button
            stopClickingButton.addActionListener(e -> {
                // Interrupt the running thread
                if (clickingThread[0] != null) {
                    clickingThread[0].interrupt();
                }
                countdownTimer.stop();

                JOptionPane.showMessageDialog(frame, "Clicking was stopped by user. Please click on 'Start' button to start clicking again.");

                startClickingButton.setEnabled(true);
                stopClickingButton.setEnabled(false);
            });

            // Add components to the panels
            infoPanel.add(label);

            buttonsPanel.add(openBrowserButton);
            buttonsPanel.add(Box.createRigidArea(new Dimension(20, 0))); // 20-pixel horizontal spacing
            buttonsPanel.add(startClickingButton);
            buttonsPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            buttonsPanel.add(stopClickingButton);

            logsPanel.add(scrollPane);

            // Add the panels to the parent panel
            parentPanel.add(infoPanel);
            parentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            parentPanel.add(buttonsPanel);
            parentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            parentPanel.add(logsPanel);

            // Add the panels to the frame
            frame.add(parentPanel);

            frame.pack();

            // Make the frame visible
            frame.setVisible(true);
        });
    }

}