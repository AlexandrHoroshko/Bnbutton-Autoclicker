package io.bnbutton;

import com.codeborne.selenide.SelenideElement;

import java.util.Random;

import static com.codeborne.selenide.Selenide.actions;

public class Helpers {

    public static final Random RANDOM = new Random();

    public static void clickByActions(SelenideElement element) throws RuntimeException {
        try {
            actions().moveToElement(element).click().perform();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
