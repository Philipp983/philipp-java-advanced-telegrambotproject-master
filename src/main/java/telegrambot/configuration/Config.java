package telegrambot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	private static Properties properties = new Properties();

	// Private constructor to prevent instantiation (Singleton Pattern)
	private Config() {}

	static {
		try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				System.out.println("Moved properties or is deleted?");
				throw new IllegalStateException("Unable to find config.properties");
			} else {
				properties.load(input);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public static String getProperty(String resource) {
		return properties.getProperty(resource);
	}
}
