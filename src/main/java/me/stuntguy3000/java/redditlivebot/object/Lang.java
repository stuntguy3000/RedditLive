/*
 * MIT License
 *
 * Copyright (c) 2016 Luke Anderson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.stuntguy3000.java.redditlivebot.object;

import me.stuntguy3000.java.redditlivebot.RedditLiveBot;
import me.stuntguy3000.java.redditlivebot.handler.LogHandler;
import me.stuntguy3000.java.redditlivebot.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// @author Luke Anderson | stuntguy3000
public class Lang {
    public static final Pattern USERNAME_PATTERN = Pattern.compile("\\s/u/(\\S+)");
    public static final Pattern SUBREDDIT_PATTERN = Pattern.compile("\\s/r/(\\S+)");
    public static final String CHAT_SUBSCRIBED = Emoji.GREEN_BOX_TICK.getText() + " *This chat has subscribed to RedditLiveBot's updates.*";
    public static final String CHAT_UNSUBSCRIBED = Emoji.GREEN_BOX_TICK.getText() + " *You have unsubscribed from updates.*";
    public static final String COMMAND_ADMIN_DEBUG = "*Debug is set to* `%s`*.*";
    public static final String COMMAND_ADMIN_DEBUG_TOGGLE = "*Debug mode has been changed to* `%s`*.*";
    public static final String COMMAND_ADMIN_STATUS = "*RedditLiveBot Status:*\n\n";
    public static final String COMMAND_ADMIN_SUBSCRIPTIONS = "*RedditLive Subscriptions (%s):* \n`%s`";
    public static final String GENERAL_BROADCAST = Emoji.PERSON_SPEAKING.getText() + "*Announcement by* %s\n\n%s";
    public static final String GENERAL_RESTART = "*Manual Restart engaged by* `%s`*.*";
    public static final String LIVE_THREAD_START = Emoji.BLUE_RIGHT_ARROW.getText() + " *Following a new feed!*\n\n" +
            "_URL: _ https://reddit.com/live/%s";
    public static final String LIVE_THREAD_STOP = Emoji.REPLAY.getText() + " *RedditLive has stopped tracking this live feed due to inactivity*";
    public static final String LIVE_THREAD_UPDATE = Emoji.PERSON_SPEAKING.getText() + " `%s` *New update by %s*\n\n%s";
    public static final String COMMAND_ADMIN_UNFOLLOW = Emoji.GREEN_BOX_TICK.getText() + " *Unfollowed the current live thread.*";
    public static final String LIVE_THREAD_REPOST_UPDATE = Emoji.PERSON_SPEAKING.getText() + " `%s` *Last update by %s*\n\n%s";
    private static final String MISC_ERROR_PREFIX = Emoji.RED_CROSS.getText() + " ";
    public static final String ERROR_CHAT_NOT_SUBSCRIBED = Lang.MISC_ERROR_PREFIX + "*This chat is not subscribed.*";
    public static final String ERROR_CHAT_SUBSCRIBED = Lang.MISC_ERROR_PREFIX + "*This chat is already subscribed.*";
    public static final String ERROR_NOT_ADMIN = Lang.MISC_ERROR_PREFIX + "*You are not a RedditLiveBot administrator!*";
    public static final String ERROR_NOT_ENOUGH_ARGUMENTS = Lang.MISC_ERROR_PREFIX + "*Invalid command usage!*";
    public static final String ERROR_COMMAND_INVALID = Lang.MISC_ERROR_PREFIX + "*Invalid command or arguments.*";

    private static SendableMessage build(String message, Object... format) {
        SendableTextMessage.SendableTextMessageBuilder sendableTextMessageBuilder = SendableTextMessage.builder();
        String formatted = String.format(message, format);
        formatted = rigerousReplace(USERNAME_PATTERN, formatted, "[/u/<r>](https://reddit.com/u/<r>)");
        formatted = rigerousReplace(SUBREDDIT_PATTERN, formatted, "[/r/<r>](https://reddit.com/r/<r>)");

        sendableTextMessageBuilder.message(formatted);
        sendableTextMessageBuilder.parseMode(ParseMode.MARKDOWN);

        return sendableTextMessageBuilder.build();
    }

    private static String rigerousReplace(Pattern pattern, String text, String replace) {
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            text = matcher.replaceFirst(" " + replace.replace("<r>", matcher.group(1)));
            matcher = pattern.matcher(text);
        }

        return text;
    }

    public static void send(Long chatID, String message, Object... format) {
        TelegramHook.getBot().sendMessage(TelegramHook.getBot().getChat(chatID), build(message, format));
    }

    public static Message send(Chat chat, String message, Object... format) {
        return TelegramHook.getBot().sendMessage(chat, build(message, format));
    }

    public static void send(User user, String message, Object... format) {
        send(user.getId(), message, format);
    }

    public static void sendAdmin(String message, Object... format) {
        send(Long.valueOf(-115432737), "*[ADMIN]* " + message, format);
        LogHandler.log("[ADMIN] " + message.replace("`[DEBUG]`", "[DEBUG]").replace("*", "").replace("_", ""), format);
    }

    public static void sendDebug(String message, Object... format) {
        if (RedditLiveBot.DEBUG) {
            sendAdmin("`[DEBUG]` " + message, format);
        }
    }

    private static void sendRaw(long chatID, String message, Object... format) {
        TelegramHook.getBot().sendMessage(TelegramHook.getBot().getChat(chatID), SendableTextMessage.builder().message(String.format(message, format)).build());
    }

    public static String stringJoin(String[] aArr, String prefix, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0) {
                sbStr.append(sSep);
            }
            sbStr.append(prefix).append(aArr[i]);
        }
        return sbStr.toString();
    }

    public static String stringJoin(List<String> aArr, String prefix, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.size(); i < il; i++) {
            if (i > 0) {
                sbStr.append(sSep);
            }
            sbStr.append(prefix).append(aArr.get(i));
        }
        return sbStr.toString();
    }

    public static Message sendHtml(Chat chat, String message, Object... format) {
        Object[] newFormat = new Object[format.length];

        for (int i = 0; i < 3; i++) {
            newFormat[i] = format[i].toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }

        message = String.format(message, newFormat);

        SendableTextMessage.SendableTextMessageBuilder sendableTextMessageBuilder = SendableTextMessage.builder();
        sendableTextMessageBuilder.message(message);
        sendableTextMessageBuilder.parseMode(ParseMode.HTML);

        return TelegramHook.getBot().sendMessage(chat, sendableTextMessageBuilder.build());
    }
}
    