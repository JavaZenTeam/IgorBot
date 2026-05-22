# Toast Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a drinking toast feature that responds with a random Russian toast when someone mentions drinking.

**Architecture:** New `ToastHandler` (TextMessageHandler) picks random toast from weighted container. Wrapped in `FilterAdapter` with a `RegexpFilter` trigger pattern. Toast list in separate `toasts.xml`.

**Tech Stack:** Java, Spring XML config, Telegram Bot API, JUnit 4

---

### Task 1: Create ToastHandler class

**Files:**
- Create: `src/main/java/ru/javazen/telegram/bot/handler/ToastHandler.java`
- Test: `src/test/java/ru/javazen/telegram/bot/handler/ToastHandlerTest.java`

- [ ] **Step 1: Write the failing test**

`src/test/java/ru/javazen/telegram/bot/handler/ToastHandlerTest.java`:

```java
package ru.javazen.telegram.bot.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.util.SizedItemsContainer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ToastHandlerTest {

    private ToastHandler handler;
    private SizedItemsContainer<String> container;

    @Before
    public void setUp() {
        container = new SizedItemsContainer<>();
        container.put("За встречу!", 1.0);
        container.put("Будем здоровы!", 1.0);

        handler = new ToastHandler();
        handler.setContainer(container);
    }

    @Test
    public void testHandleSendsToast() throws TelegramApiException {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getMessageThreadId()).thenReturn(null);

        AbsSender sender = mock(AbsSender.class);

        boolean result = handler.handle(message, "выпьем", sender);

        assertTrue(result);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sent = captor.getValue();
        assertEquals("12345", sent.getChatId());
        assertTrue(sent.getText().equals("За встречу!") || sent.getText().equals("Будем здоровы!"));
    }

    @Test
    public void testHandleEmptyContainerReturnsFalse() throws TelegramApiException {
        ToastHandler emptyHandler = new ToastHandler();
        SizedItemsContainer<String> emptyContainer = new SizedItemsContainer<>();
        emptyHandler.setContainer(emptyContainer);

        Message message = mock(Message.class);
        AbsSender sender = mock(AbsSender.class);

        boolean result = emptyHandler.handle(message, "выпьем", sender);
        assertFalse(result);
        verify(sender, never()).execute(any());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -B -ntp test -Dtest=ToastHandlerTest -DfailIfNoTests=false`
Expected: Build failure — ToastHandler class not found

- [ ] **Step 3: Write minimal implementation**

`src/main/java/ru/javazen/telegram/bot/handler/ToastHandler.java`:

```java
package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.util.SizedItemsContainer;

import java.util.Map;
import java.util.Random;

public class ToastHandler implements TextMessageHandler {
    private final Random random = new Random();
    private SizedItemsContainer<String> container;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (container == null || container.size() == 0) return false;

        String toast = container.get(random.nextDouble() * container.size());
        if (toast == null) return false;

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), toast);
        sendMessage.setMessageThreadId(message.getMessageThreadId());
        sender.execute(sendMessage);
        return true;
    }

    public void setContainer(SizedItemsContainer<String> container) {
        this.container = container;
    }

    public void setToasts(Map<String, Double> toasts) {
        container = new SizedItemsContainer<>();
        toasts.forEach(container::put);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -B -ntp test -Dtest=ToastHandlerTest -DfailIfNoTests=false`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/ru/javazen/telegram/bot/handler/ToastHandler.java src/test/java/ru/javazen/telegram/bot/handler/ToastHandlerTest.java
git commit -m "feat: add ToastHandler for random drinking toasts"
```

---

### Task 2: Wire XML configuration

**Files:**
- Create: `src/main/resources/spring/toasts.xml`
- Modify: `src/main/resources/spring/botConfig.xml`

- [ ] **Step 1: Create toasts.xml**

`src/main/resources/spring/toasts.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/util
            http://www.springframework.org/schema/util/spring-util.xsd">

    <util:map id="toasts">
        <entry key="За встречу!" value="5"/>
        <entry key="За нас!" value="4"/>
        <entry key="Будем здоровы!" value="4"/>
        <entry key="За любовь!" value="4"/>
        <entry key="Ну, поехали!" value="3"/>
        <entry key="Горько!" value="3"/>
        <entry key="Выпьем за удачу!" value="3"/>
        <entry key="Пусть всё будет хорошо!" value="3"/>
        <entry key="До дна!" value="2"/>
        <entry key="С Богом!" value="2"/>
        <entry key="Будем!" value="2"/>
        <entry key="За тех, кто в море!" value="2"/>
        <entry key="За присутствующих!" value="2"/>
        <entry key="За отсутствующих!" value="2"/>
    </util:map>
</beans>
```

- [ ] **Step 2: Add import and FilterAdapter bean in botConfig.xml**

Add import line after `<import resource="songs.xml"/>`:

```xml
    <import resource="toasts.xml"/>
```

Add FilterAdapter bean before `updateHandlers` list:

```xml
    <bean id="toastHandlerModule" class="ru.javazen.telegram.bot.handler.FilterAdapter">
        <constructor-arg name="filter">
            <bean class="ru.javazen.telegram.bot.filter.RegexpFilter">
                <property name="pattern"
                          value=".*(выпь(ем|[ие]ть|ют)|тост\p{L}*|наливай|налито|горько|поднимем\s+бокалы|за\s+встречу|бокалы).*"/>
            </bean>
        </constructor-arg>
        <constructor-arg name="handler">
            <bean class="ru.javazen.telegram.bot.handler.ToastHandler">
                <property name="toasts" ref="toasts"/>
            </bean>
        </constructor-arg>
    </bean>
```

Add to `updateHandlers` list (add after the pinned forwarder or in the subscription area):

```xml
                <ref bean="toastHandlerModule"/>
```

Choose placement in the list that makes semantic sense — add it near the end, before the help handlers.

- [ ] **Step 3: Build and verify**

Run: `mvn -B -ntp clean package -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/spring/toasts.xml src/main/resources/spring/botConfig.xml
git commit -m "feat: wire toast handler with trigger regex and toast list"
```

---

### Task 3: Full verification

- [ ] **Step 1: Run all tests**

Run: `mvn -B -ntp test`
Expected: BUILD SUCCESS

- [ ] **Step 2: Final commit**

```bash
git add -A
git commit -m "chore: finalize toast feature"
```
