package ru.javazen.telegram.bot.scheduler.parser.service;

import java.util.Date;

public interface TimeExtractionService {

    Date extractTime(String message);
}
