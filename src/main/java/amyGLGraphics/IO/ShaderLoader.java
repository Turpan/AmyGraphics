package amyGLGraphics.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ShaderLoader {
	private ShaderLoader() {
		
	}
	public static String loadFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		File file = new File(fileName);
		if (!file.exists()) {
			return "";
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		} catch (IOException e) {
			System.out.println("Failed to load " + fileName);
			e.printStackTrace();
			return "";
		}
		return sb.toString();
	}
}
