package telegrambot.utils;

import java.io.File;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class TelegramBotHelper {

    public static SendAudio prepareAudio(final String fileId, final String pathname) {
        SendAudio audio = new SendAudio();
        audio.setChatId(fileId);
        audio.setAudio(new InputFile(new File(pathname)));

        return audio;
    }

}
