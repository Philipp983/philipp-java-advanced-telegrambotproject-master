package telegrambot.utils;

import java.io.File;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class TelegramBotHelper {

    public static SendAudio prepareAudio(final String fileId, final String pathname) {
        SendAudio audio = new SendAudio();
        if (fileId != null && pathname != null) {
            audio.setChatId(fileId);
            audio.setAudio(new InputFile(new File(pathname)));
        } else {
            System.out.println("error");
            // another idea is throw Error
        }

        return audio;
    }

}
