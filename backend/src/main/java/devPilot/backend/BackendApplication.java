package devPilot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		loadDotenv();
		SpringApplication.run(BackendApplication.class, args);
	}

	private static void loadDotenv() {
		Path[] paths = new Path[]{
			Paths.get(".env"),
			Paths.get("backend", ".env"),
			Paths.get("..", ".env")
		};
		for (Path path : paths) {
			if (Files.exists(path)) {
				try {
					List<String> lines = Files.readAllLines(path);
					for (String line : lines) {
						String trimmed = line.trim();
						if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
							continue;
						}
						int idx = trimmed.indexOf('=');
						String key = trimmed.substring(0, idx).trim();
						String value = trimmed.substring(idx + 1).trim();
						if (!key.isEmpty() && System.getProperty(key) == null && System.getenv(key) == null) {
							System.setProperty(key, value);
						}
					}
					break;
				} catch (IOException ignored) {
				}
			}
		}
	}

}

