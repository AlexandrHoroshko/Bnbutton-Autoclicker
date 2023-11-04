package io.bnbutton;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class Clicker {

    private static final By TAB_NFT_BUTTONS_LOCATOR = Selectors.byText("NFT BUTTONS");

    private static final By COMMON_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/1.png");
    private static final By UNCOMMON_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/2.png");
    private static final By RARE_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/3.png");
    private static final By MAGIC_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/4.png");
    private static final By MYSTIC_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/5.png");
    private static final By GOLD_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/6.png");
    private static final By PLATINUM_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/7.png");
    private static final By DIAMOND_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/8.png");
    private static final By LEGENDARY_BUTTON_LOCATOR = Selectors.byAttribute("src", "./imgBNB/9.png");

    private static final By AVAILABLE_CLICKS_COUNT_LOCATOR = Selectors.byXpath(".//p[text()='Clicks Left']/following-sibling::span");
    private static final By STRENGTH_PERCENT_LOCATOR = Selectors.byXpath(".//p[text()='Strength']/following-sibling::span");

    private static final By BUTTON_REPAIR_LOCATOR = Selectors.byXpath(".//p[text()='REPAIR']/..");
    private static final By HEADER_REPAIR_CONFIRMATION_LOCATOR = Selectors.byXpath(".//*[text()='Repair button']");
    private static final By BUTTON_OK_REPAIR_CONFIRMATION_LOCATOR = Selectors.byXpath(".//button[text()='Ok']");


    public static boolean doClicksOnAllButtons() {
        boolean isClicksFinished = false;
        try {
            openNftButtonsTab();
            doClicksOnCommonButton();
            doClicksOnUncommonButton();
            doClicksOnRareButton();
            doClicksOnMagicButton();
            doClicksOnMysticButton();
            doClicksOnGoldButton();
            doClicksOnPlatinumButton();
            doClicksOnDiamondButton();
            doClicksOnLegendaryButton();
            isClicksFinished = true;
        } catch (Exception e) {
            System.out.println("Something went wrong. Caught exception: \n");
            e.printStackTrace();
        }
        return isClicksFinished;
    }

    public static void doClicksOnCommonButton() {
        System.out.println("\nTrying to click on COMMON button");
        doClicksOnButton(COMMON_BUTTON_LOCATOR);
    }

    public static void doClicksOnUncommonButton() {
        System.out.println("\nTrying to click on UNCOMMON button");
        doClicksOnButton(UNCOMMON_BUTTON_LOCATOR);
    }

    public static void doClicksOnRareButton() {
        System.out.println("\nTrying to click on RARE button");
        doClicksOnButton(RARE_BUTTON_LOCATOR);
    }

    public static void doClicksOnMagicButton() {
        System.out.println("\nTrying to click on MAGIC button");
        doClicksOnButton(MAGIC_BUTTON_LOCATOR);
    }

    public static void doClicksOnMysticButton() {
        System.out.println("\nTrying to click on MYSTIC button");
        doClicksOnButton(MYSTIC_BUTTON_LOCATOR);
    }

    public static void doClicksOnGoldButton() {
        System.out.println("\nTrying to click on GOLD button");
        doClicksOnButton(GOLD_BUTTON_LOCATOR);
    }

    public static void doClicksOnPlatinumButton() {
        System.out.println("\nTrying to click on PLATINUM button");
        doClicksOnButton(PLATINUM_BUTTON_LOCATOR);
    }

    public static void doClicksOnDiamondButton() {
        System.out.println("\nTrying to click on DIAMOND button");
        doClicksOnButton(DIAMOND_BUTTON_LOCATOR);
    }

    public static void doClicksOnLegendaryButton() {
        System.out.println("\nTrying to click on LEGENDARY button");
        doClicksOnButton(LEGENDARY_BUTTON_LOCATOR);
    }

    private static void doClicksOnButton(By buttonLocator) {
        try {
            SelenideElement button = $(buttonLocator);
            button.scrollIntoView(true);
            if (isButtonBought(button)) {
                int availableClicksCount = getAvailableClicksCountForButton(button);
                if (availableClicksCount == 0) {
                    System.out.println("There is no available clicks for now. Available clicks count: " + availableClicksCount);
                }
                while (availableClicksCount > 0) {
                    System.out.println("Clicking on button");
                    Helpers.clickByActions(button);
                    long randomSleepTime = Helpers.RANDOM.nextLong(3000, 4000);
                    sleep(randomSleepTime);
                    availableClicksCount = getAvailableClicksCountForButton(button);
                    System.out.println("Remaining clicks count: " + availableClicksCount);
                    double strengthPercent = getStrengthPercentForButton(button);
                    System.out.println("Remaining strength percent: " + strengthPercent);
                    if (strengthPercent < 10) {
                        System.out.println("Button has less than 1% strength. Repairing...");
                        if (!repairButton(button)) {
                            System.out.println("Button is not repaired. Please repair it manually and then clicks will continue work.");
                        }
                    }
                    System.out.println("\n");
                }
            } else {
                System.out.println("Button is not bought");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong. Caught exception: \n");
            e.printStackTrace();
        }
    }

    private static boolean isButtonBought(SelenideElement button) {
        return getParentButtonContainer(button).find(AVAILABLE_CLICKS_COUNT_LOCATOR).exists();
    }

    private static int getAvailableClicksCountForButton(SelenideElement button) {
        System.out.println("Getting available clicks count for button");
        var countElement = getParentButtonContainer(button).find(AVAILABLE_CLICKS_COUNT_LOCATOR);
        String countText = countElement.text();
        if (countText.equals("...")) {
            sleep(1000);
            countText = countElement.text();
        }
        return Integer.parseInt(countText);
    }

    private static double getStrengthPercentForButton(SelenideElement button) {
        System.out.println("Getting strength percent for button");
        return Double.parseDouble(getParentButtonContainer(button).find(STRENGTH_PERCENT_LOCATOR).text().replace("%", ""));
    }

    private static boolean repairButton(SelenideElement button) {
        boolean isButtonRepaired = false;
        try {
            System.out.println("Clicking on REPAIR button");
            Helpers.clickByActions(getParentButtonContainer(button).find(BUTTON_REPAIR_LOCATOR));
            System.out.println("Waiting for REPAIR confirmation popup");
            $(HEADER_REPAIR_CONFIRMATION_LOCATOR).shouldBe(Condition.visible);
            System.out.println("Clicking on OK button in REPAIR confirmation popup");
            Helpers.clickByActions($(BUTTON_OK_REPAIR_CONFIRMATION_LOCATOR));
            System.out.println("Waiting for REPAIR confirmation popup to disappear");
            $(HEADER_REPAIR_CONFIRMATION_LOCATOR).should(Condition.disappear);
            isButtonRepaired = true;
        } catch (Exception ignored) {
        }
        return isButtonRepaired;
    }

    public static void openNftButtonsTab() {
        System.out.println("\nOpening NFT BUTTONS tab");
        Helpers.clickByActions($(TAB_NFT_BUTTONS_LOCATOR));
    }

    private static SelenideElement getParentButtonContainer(SelenideElement button) {
        return button.parent().parent().parent().parent().parent();
    }

}