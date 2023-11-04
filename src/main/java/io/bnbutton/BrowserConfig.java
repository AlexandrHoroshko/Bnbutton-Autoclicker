package io.bnbutton;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

public final class BrowserConfig {

    public static synchronized boolean configAndOpenBrowser() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--allow-file-access-from-files");
            options.addArguments("--use-fake-device-for-media-stream");
            options.addArguments("--use-fake-ui-for-media-stream");
            options.addArguments("user-agent=" +
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/117.0.0.0 " +
                    "Safari/537.36");
            // disable the AutomationControlled feature of Blink rendering engine
            options.addArguments("--disable-blink-features=AutomationControlled");
            // disable pop-up blocking
            options.addArguments("--disable-popup-blocking");
            // start the browser window in maximized mode
            options.addArguments("--start-maximized");
            // disable extensions
//      options.addArguments("--disable-extensions");
            // disable sandbox mode
            options.addArguments("--no-sandbox");
            // disable shared memory usage
            options.addArguments("--disable-dev-shm-usage");
            options.addExtensions(new File("Metamask.crx"));
            Configuration.browserCapabilities = options;
            Configuration.savePageSource = false;
            Configuration.screenshots = false;
            open();
            return true;
        } catch (Exception e) {
            System.out.println("Something went wrong. Caught exception: \n");
            e.printStackTrace();
        }
        return false;
    }

    public static void closeAllWindowsExceptOfMain(String mainTabId) {
        for (String tabId : WebDriverRunner.getWebDriver().getWindowHandles()) {
            if (!tabId.equals(mainTabId)) {
                WebDriverRunner.getWebDriver().switchTo().window(tabId);
                WebDriverRunner.getWebDriver().close();
            }
        }
        WebDriverRunner.getWebDriver().switchTo().window(mainTabId);
    }

    public static void switchToWindow(String windowId) {
        WebDriverRunner.getWebDriver().switchTo().window(windowId);
    }

    public static void switchToSecondWindow() {
        for (String tabId : WebDriverRunner.getWebDriver().getWindowHandles()) {
            if (!tabId.equals(WebDriverRunner.getWebDriver().getWindowHandles().toArray()[0].toString())) {
                WebDriverRunner.getWebDriver().switchTo().window(tabId);
            }
        }
    }

    public static boolean waitForOpenSecondWindow() {
        int tries = 0;
        boolean isSecondWindowOpened = false;
        System.out.println("Waiting for opening Metamask window");
        while (!isSecondWindowOpened && tries < 90) {
            isSecondWindowOpened = WebDriverRunner.getWebDriver().getWindowHandles().size() > 1;
            sleep(1000);
            tries++;
        }
        if (isSecondWindowOpened) {
            System.out.println("Metamask window is opened");
        } else {
            System.out.println("Metamask window didn't open");
        }
        return isSecondWindowOpened;
    }

    public static boolean waitForCloseSecondWindow() {
        int tries = 0;
        boolean isSecondWindowClosed = false;
        System.out.println("Waiting for closing Metamask window");
        while (!isSecondWindowClosed && tries < 10) {
            isSecondWindowClosed = WebDriverRunner.getWebDriver().getWindowHandles().size() == 1;
            sleep(1000);
            tries++;
        }
        if (isSecondWindowClosed) {
            System.out.println("Metamask window is closed");
        } else {
            System.out.println("Metamask window didn't close");
        }
        return isSecondWindowClosed;
    }

}
