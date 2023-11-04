package io.bnbutton;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;

public class MetamaskConnector {

    //MAIN PAGE
    private static final By BUTTON_CONNECT_WALLET_LOCATOR = Selectors.byText("Connect Wallet");
    private static final By BUTTON_LOGOUT_LOCATOR = Selectors.byText("Logout");
    //METAMASK
    private static final By BUTTON_APPROVE_LOCATOR = Selectors.byText("Approve");
    private static final By BUTTON_SWITCH_NETWORK_LOCATOR = Selectors.byText("Switch network");
    private static final By BUTTON_SIGN_LOCATOR = Selectors.byText("Sign");

    public static void connectWallet(String mainTabId) {
        if (!isLogoutButtonVisible()) {
            clickOnConnectWalletButton();
            if (BrowserConfig.waitForOpenSecondWindow()) {
                BrowserConfig.switchToSecondWindow();
//                if (isApproveButtonVisible()) {
//                    clickOnApproveButton();
//                    clickOnSwitchNetworkButton();
//                    Selenide.sleep(5000);
//                }
                MetamaskConnector.clickOnSignButton();
                if (BrowserConfig.waitForCloseSecondWindow()) {
                    BrowserConfig.switchToWindow(mainTabId);
                    System.out.println("Metamask successfully connected");
                }
            }
        }
    }

    private static void clickOnConnectWalletButton() {
        Helpers.clickByActions(Selenide.$(BUTTON_CONNECT_WALLET_LOCATOR));
    }

    private static boolean isLogoutButtonVisible() {
        return Selenide.$(BUTTON_LOGOUT_LOCATOR).exists();
    }

    private static boolean isApproveButtonVisible() {
        return Selenide.$(BUTTON_APPROVE_LOCATOR).exists();
    }

    private static void clickOnApproveButton() {
        Helpers.clickByActions(Selenide.$(BUTTON_APPROVE_LOCATOR));
    }

    private static void clickOnSwitchNetworkButton() {
        Helpers.clickByActions(Selenide.$(BUTTON_SWITCH_NETWORK_LOCATOR));
    }

    private static void clickOnSignButton() {
        Helpers.clickByActions(Selenide.$(BUTTON_SIGN_LOCATOR));
    }
}
