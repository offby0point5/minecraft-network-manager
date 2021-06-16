package com.github.offby0point5.mc.plugin.hybrid.servermanager;

public interface ILogger {
    void debug(String string);
    void info(String string);
    void warning(String string);
    void error(String string);
    void error(String string, Throwable e);
}
