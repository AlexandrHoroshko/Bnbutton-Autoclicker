package io.bnbutton;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;

public class MetamaskConnector {

    //MAIN PAGE
    private static final By BUTTON_CONNECT_WALLET_LOCATOR = Selectors.byText("Connect Wallet");
    private static final By BUTTON_LOGOUT_LOCATOR = Selectors.byText("Logout");
    //METAMASK
    private static final By BUTTON_SIGN_LOCATOR = Selectors.byText("Sign");

    public static void connectWallet(String mainTabId) {
        try {
            System.out.println("Checking if Metamask is connected...");
            if (!isLogoutButtonVisible()) {
                System.out.println("Metamask is not connected, trying to connect...");
                clickOnConnectWalletButton();
                if (BrowserConfig.waitForOpenSecondWindow()) {
                    BrowserConfig.switchToSecondWindow();
                    MetamaskConnector.clickOnSignButton();
                    if (BrowserConfig.waitForCloseSecondWindow()) {
                        BrowserConfig.switchToWindow(mainTabId);
                        System.out.println("Metamask successfully connected");
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Something went wrong while connecting Metamask. Caught exception: \n");
            e.printStackTrace();
        }
    }

    private static void clickOnConnectWalletButton() throws RuntimeException {
        System.out.println("Click on 'Connect Wallet' button");
        try {
            Helpers.clickByActions(Selenide.$(BUTTON_CONNECT_WALLET_LOCATOR));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isLogoutButtonVisible() {
        System.out.println("Check if 'Logout' button is visible");
        return Selenide.$(BUTTON_LOGOUT_LOCATOR).exists();
    }

    private static void clickOnSignButton() throws RuntimeException {
        System.out.println("Click on 'Sign' button");
        try {
            Helpers.clickByActions(Selenide.$(BUTTON_SIGN_LOCATOR));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
