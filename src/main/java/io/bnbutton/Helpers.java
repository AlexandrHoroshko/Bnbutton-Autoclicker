package io.bnbutton;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;

import java.util.Random;

import static com.codeborne.selenide.Selenide.actions;

public class Helpers {

    public static final Random RANDOM = new Random();

    public static void clickByActions(SelenideElement element) throws RuntimeException {
        try {
            actions().moveToElement(element).click().perform();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
