package ru.javazen.telegram.bot;

import ru.javazen.telegram.bot.service.CallbackService;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;


public class RSApplication extends Application
{
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(CallbackService.class);
        return s;
    }
}