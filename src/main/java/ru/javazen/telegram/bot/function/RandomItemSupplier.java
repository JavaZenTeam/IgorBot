package ru.javazen.telegram.bot.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import ru.javazen.telegram.bot.container.SizedItemsContainer;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class RandomItemSupplier<T> implements Supplier<T> {
    private Random random;
    private SizedItemsContainer<T> container;

    @Override
    public T get() {
        return container.get(random.nextDouble() * container.size());
    }

    @Autowired
    public void setRandom(Random random) {
        this.random = random;
    }

    @Autowired
    public void setContainer(SizedItemsContainer<T> container) {
        this.container = container;
    }

    @Required
    public void setOptionRatios(Map<T, Double> optionRatios){
        optionRatios.forEach((option, ratio) -> container.put(option, ratio));
    }
}
