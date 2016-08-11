package ru.javazen.telegram.bot.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;

import java.util.Random;

public class RandomFilter implements Filter {
    @Autowired
    private Random random;
    private double probability;

    @Override
    public boolean check(Update update) {
        random.setSeed(System.nanoTime());
        return random.nextDouble() < probability;
    }

    @Required
    public void setProbability(double probability) {
        Assert.isTrue(probability >= 0 && probability <= 1, "probability must be between 0 and 1");
        this.probability = probability;
    }
}
